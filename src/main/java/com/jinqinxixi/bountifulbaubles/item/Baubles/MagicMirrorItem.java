// MagicMirrorItem.java
package com.jinqinxixi.bountifulbaubles.item.Baubles;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import com.jinqinxixi.bountifulbaubles.particle.ModParticles;
import com.jinqinxixi.bountifulbaubles.util.TeleportGuard;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.network.PacketDistributor;
import com.jinqinxixi.bountifulbaubles.network.handler.NetworkHandler;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nullable;
import java.util.List;

public class MagicMirrorItem extends Item {
    private static final int COOLDOWN_TICKS = 10; // 冷却
    private static final int USE_DURATION = 20;    // 1秒长按时间

    public MagicMirrorItem(Properties properties) {
        super(properties);
    }
    // 设置使用动作持续时间
    @Override
    public int getUseDuration(ItemStack stack) {
        return USE_DURATION;
    }

    // 定义使用动作为拉弓
    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;   // 还原 1.12 原版：EnumAction.NONE，无手臂动画
    }

    // 右键开始使用
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (canTeleport(player)) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }
        return InteractionResultHolder.fail(stack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingTicks) {
        if (level.isClientSide) return;
        if (entity instanceof ServerPlayer player) {
            float progress = 1.0f - (remainingTicks / (float) USE_DURATION);
            spawnChargeParticles(player, progress); // 长按蓄力粒子
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof ServerPlayer player) {
            if (checkCooldown(player)) {
                spawnCompletionParticles(player); // 完成粒子（原位置散开）
                teleportToSpawn(player); // 核心逻辑
                setCooldown(player);
            }
        }
        return stack;
    }


    private boolean canTeleport(Player player) {
        return !player.getCooldowns().isOnCooldown(this);
    }

    protected boolean checkCooldown(Player player) { // 注意参数改为 Player 类型
        if (player.getCooldowns().isOnCooldown(this)) {
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.sendSystemMessage(
                        Component.translatable("message.magic_mirror.cooldown")
                );
            }
            return false;
        }
        return true;
    }
    private void spawnChargeParticles(ServerPlayer player, float progress) {
        ServerLevel serverLevel = (ServerLevel) player.level();
        Vec3 lookVec = player.getLookAngle().scale(0.5);
        Vec3 pos = player.position()
                .add(0, player.getEyeHeight() - 0.2, 0)
                .add(lookVec);

        serverLevel.sendParticles(
                ModParticles.SKY_BLUE_DUST.get(),
                pos.x, pos.y, pos.z,
                20,                    // 每 tick 生成 20 个粒子
                0.5, 0.5, 0.5,         // 随机偏移量
                0                      // 基础速度
        );
    }

    protected void spawnCompletionParticles(ServerPlayer player) {
        ServerLevel serverLevel = (ServerLevel) player.level();
        Vec3 pos = player.position().add(0, player.getBbHeight() / 2, 0);

        serverLevel.sendParticles(
                ModParticles.SKY_BLUE_REVERSE_PORTAL.get(),
                pos.x, pos.y, pos.z,
                300,                    // 粒子数量
                1.0, 1.0, 1.0,          // 扩散范围
                0.3                     // 速度
        );
    }

    // 新增公共方法：播放传送效果（被子类复用）
    public void playTeleportEffects(ServerPlayer player) {
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
        ((ServerLevel) player.level()).sendParticles(ModParticles.SKY_BLUE_PORTAL.get(),
                player.getX(), player.getY(), player.getZ(),
                500, 0.5, 0.5, 0.5, 1.0);
    }

    protected void teleportToSpawn(ServerPlayer player) {
        ServerLevel targetLevel = player.server.getLevel(player.getRespawnDimension());
        BlockPos spawnPos = player.getRespawnPosition();
        float angle = player.getRespawnAngle();

        if (spawnPos == null || targetLevel == null) {
            targetLevel = player.server.overworld();
            spawnPos = targetLevel.getSharedSpawnPos();
        }

        // 传送限制检查（最大距离 / 是否允许跨维度）
        Component deny = TeleportGuard.checkRecall(player, targetLevel, spawnPos);
        if (deny != null) {
            player.displayClientMessage(deny, true);
            return;
        }

        player.teleportTo(targetLevel,
                spawnPos.getX() + 0.5,
                spawnPos.getY(),
                spawnPos.getZ() + 0.5,
                angle,
                0);

        // 调用效果方法
        player.fallDistance = 0;
        playTeleportEffects(player);

        // 传送成功：通知本人播放"脸上弹出"动画（药水等子类可覆盖为 false）
        if (shouldShowActivationAnimation()) {
            NetworkHandler.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new NetworkHandler.TotemActivationPacket(
                            BuiltInRegistries.ITEM.getKey(this).toString())
            );
        }
    }

    /**
     * 传送后是否播放"脸上弹出"动画。
     * <p>
     * 默认 true（魔镜、虫洞镜）；继承本类的药水等变体可覆盖为 false。
     */
    protected boolean shouldShowActivationAnimation() {
        return true;
    }

    protected void setCooldown(Player player) {
        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
    }

    // 添加Curios支持
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
        return CuriosApi.createCurioProvider(new ICurio() {
            @Override

            public ItemStack getStack() {
                return stack;
            }
        });
    }
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.magic_mirror.effect")
                .withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.magic_mirror.description")
                .withStyle(ChatFormatting.GREEN));
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.magic_mirror.description1")
                .withStyle(ChatFormatting.GREEN));
    }
    @Override
    public int getEnchantmentValue() {
        return 0; // 附魔等级为0
    }

    // 禁止任何形式的附魔（包括铁砧）
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    // 始终显示附魔光效
    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}