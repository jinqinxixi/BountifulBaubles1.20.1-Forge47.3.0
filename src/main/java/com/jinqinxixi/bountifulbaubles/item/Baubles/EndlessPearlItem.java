package com.jinqinxixi.bountifulbaubles.item.Baubles;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;
import java.util.List;

public class EndlessPearlItem extends Item {
    private static final int COOLDOWN_TICKS = 160; // 8秒冷却时间
    private static final String LAST_USE_TAG = "LastUseTime";

    public EndlessPearlItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            CompoundTag tag = stack.getOrCreateTag();
            long lastUse = tag.getLong(LAST_USE_TAG);
            long currentTime = level.getGameTime();

            if (currentTime - lastUse >= COOLDOWN_TICKS) {
                ThrownEndlessPearl pearl = new ThrownEndlessPearl(level, player);
                pearl.setItem(stack);
                pearl.shootFromRotation(player,
                        player.getXRot(), player.getYRot(),
                        0.0F, 1.5F, 1.0F);
                level.addFreshEntity(pearl);

                tag.putLong(LAST_USE_TAG, currentTime);
                stack.setTag(tag);
                player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);

                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ENDER_PEARL_THROW, SoundSource.NEUTRAL,
                        0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    public static class ThrownEndlessPearl extends ThrownEnderpearl {
        public ThrownEndlessPearl(Level level, Player player) {
            super(level, player);
        }

        @Override
        protected void onHit(HitResult result) {
            // 完全自定义命中逻辑
            if (!this.level().isClientSide) {
                Entity owner = this.getOwner();

                if (owner instanceof Player player) {
                    // 处理骑乘状态
                    if (player.isPassenger()) {
                        player.stopRiding();
                    }

                    // 安全传送
                    player.teleportTo(this.getX(), this.getY(), this.getZ());
                    player.fallDistance = 0.0F; // 重置摔落伤害计算

                    // 特效系统
                    ServerLevel serverLevel = (ServerLevel) this.level();
                    serverLevel.sendParticles(ParticleTypes.PORTAL,
                            player.getX(), player.getY() + 1, player.getZ(),
                            50, 0.5, 1, 0.5, 0.1);

                    serverLevel.playSound(null,
                            player.blockPosition(),
                            SoundEvents.ENDERMAN_TELEPORT,
                            SoundSource.PLAYERS,
                            1.0F, 1.0F);
                }
            }
            this.discard(); // 销毁实体
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.endless_pearl.effect")
                .withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.endless_pearl.effect1")
                .withStyle(ChatFormatting.BLUE));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
