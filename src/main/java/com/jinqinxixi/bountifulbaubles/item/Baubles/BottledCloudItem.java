package com.jinqinxixi.bountifulbaubles.item.Baubles;

import com.jinqinxixi.bountifulbaubles.config.ModConfig;
import com.jinqinxixi.bountifulbaubles.system.modifier.ModifiableBaubleItem;
import com.jinqinxixi.bountifulbaubles.network.handler.NetworkHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler; // 正确导入

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber
public class BottledCloudItem extends ModifiableBaubleItem {

    private static final ModifiableBaubleItem.Modifier[] MODIFIERS = ModifiableBaubleItem.Modifier.values();

    public BottledCloudItem(Properties properties) {
        super(properties);
    }

    @Override
    public ModifiableBaubleItem.Modifier[] getModifiers() {
        return MODIFIERS;
    }

    public static void jump(Player player) {
        player.fallDistance = 0;

        // 基础垂直速度
        double upwardsMotion = 0.5;

        // 跳跃提升效果
        if (player.hasEffect(MobEffects.JUMP)) {
            upwardsMotion += 0.1 * (player.getEffect(MobEffects.JUMP).getAmplifier() + 1);
        }

        // 疾跑垂直加成（使用配置值）
        if (player.isSprinting()) {
            upwardsMotion *= 1 + ModConfig.SPRINT_JUMP_VERTICAL.get();
        }

        // 运动计算
        Vec3 currentMotion = player.getDeltaMovement();
        float yaw = player.getYRot() * ((float) Math.PI / 180);
        double horizontalBoost = player.isSprinting() ?
                ModConfig.SPRINT_JUMP_HORIZONTAL.get() : 0.0;

        // 运动矢量合成
        Vec3 addedMotion = new Vec3(
                -Mth.sin(yaw) * horizontalBoost,
                upwardsMotion - currentMotion.y,
                Mth.cos(yaw) * horizontalBoost
        );

        player.setDeltaMovement(currentMotion.add(addedMotion));
        player.hasImpulse = true;

        // 统计和消耗
        player.awardStat(Stats.JUMP);
        if (player.isSprinting()) {
            player.causeFoodExhaustion(0.2F);
        } else {
            player.causeFoodExhaustion(0.05F);
        }

        player.playSound(SoundEvents.WOOL_FALL, 1.0F, 0.9F + player.getRandom().nextFloat() * 0.2F);

        NetworkHandler.CHANNEL.sendToServer(new NetworkHandler.DoubleJumpPacket(true));

        // 客户端粒子效果
        if (player.level().isClientSide) {
            spawnJumpParticles(player);
        }
    }
    public static void spawnJumpParticles(Player player) {
        Vec3 pos = player.position();
        RandomSource random = player.getRandom();

        for(int i = 0; i < 8; ++i) {
            double dx = random.nextGaussian() * 0.02;
            double dy = random.nextGaussian() * 0.02 + 0.2;
            double dz = random.nextGaussian() * 0.02;

            player.level().addParticle(
                    ParticleTypes.CLOUD,
                    pos.x + random.nextFloat() * 0.6 - 0.3,
                    pos.y,
                    pos.z + random.nextFloat() * 0.6 - 0.3,
                    dx, dy, dz
            );
        }
    }

    // 跌落伤害减免
    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (hasBottledCloud(player)) {
                event.setDistance(Math.max(0, event.getDistance() - 3));
            }
        }
    }

    // 安全饰品检测方法（兼容1.20.1+）
    public static boolean hasBottledCloud(LivingEntity entity) {
        return CuriosApi.getCuriosInventory(entity)
                .resolve()
                .map(handler -> {
                    // 遍历所有Curios槽位
                    for (String identifier : handler.getCurios().keySet()) {
                        ICurioStacksHandler stackHandler = handler.getCurios().get(identifier); // 类名已更正
                        if (stackHandler != null) {
                            // 检查每个槽位中的物品
                            for (int i = 0; i < stackHandler.getSlots(); i++) {
                                if (stackHandler.getStacks().getStackInSlot(i).getItem() instanceof BottledCloudItem) {
                                    return true;
                                }
                            }
                        }
                    }
                    return false;
                })
                .orElse(false);
    }
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {

        tooltip.add(Component.translatable("tooltip.bountifulbaubles.bottled_cloud.double_jump")
                .withStyle(ChatFormatting.BLUE));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
