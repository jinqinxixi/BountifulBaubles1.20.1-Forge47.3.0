// MagicMirrorItem.java
package com.jinqinxixi.bountifulbaubles.item.Baubles;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import net.minecraftforge.common.capabilities.ICapabilityProvider;
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
        return UseAnim.BOW;
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
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof ServerPlayer player) {
            if (checkCooldown(player)) {
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
    // 新增公共方法：播放传送效果（被子类复用）
    public void playTeleportEffects(ServerPlayer player) {
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
        ((ServerLevel) player.level()).sendParticles(ParticleTypes.PORTAL,
                player.getX(), player.getY(), player.getZ(),
                100, 0.5, 0.5, 0.5, 1.0);
    }

    protected void teleportToSpawn(ServerPlayer player) {
        ServerLevel targetLevel = player.server.getLevel(player.getRespawnDimension());
        BlockPos spawnPos = player.getRespawnPosition();
        float angle = player.getRespawnAngle();

        if (spawnPos == null || targetLevel == null) {
            targetLevel = player.server.overworld();
            spawnPos = targetLevel.getSharedSpawnPos();
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
}