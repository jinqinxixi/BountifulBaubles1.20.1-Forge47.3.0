package com.jinqinxixi.bountifulbaubles.item.Baubles;

import com.jinqinxixi.bountifulbaubles.config.ModConfig;
import com.jinqinxixi.bountifulbaubles.system.modifier.ModifiableBaubleItem;
import com.jinqinxixi.bountifulbaubles.network.handler.NetworkHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.List;

public class InfiniteTotemOfUndyingItem extends ModifiableBaubleItem {
    public static final String PUBLIC_COOLDOWN_TAG = "TotemPublicCooldown";
    public static final int DEFAULT_COOLDOWN = 6000;

    private static final ModifiableBaubleItem.Modifier[] MODIFIERS = ModifiableBaubleItem.Modifier.values();
    @Override
    public ModifiableBaubleItem.Modifier[] getModifiers() {
        return MODIFIERS;
    }

    public InfiniteTotemOfUndyingItem(Properties properties) {
        super(properties);
    }

    public static boolean isReady(Player player) {
        CompoundTag playerData = player.getPersistentData();
        return !playerData.contains(PUBLIC_COOLDOWN_TAG) || playerData.getInt(PUBLIC_COOLDOWN_TAG) <= 0;
    }

    public static void startCooldown(Player player) {
        int cooldown = Math.max(ModConfig.COOLDOWN_TICKS.get(), 1); // 确保至少1 tick
        player.getPersistentData().putInt(PUBLIC_COOLDOWN_TAG, cooldown);
    }
    public static void adjustExistingCooldown(Player player) {
        CompoundTag data = player.getPersistentData();
        if (data.contains(PUBLIC_COOLDOWN_TAG)) {
            int current = data.getInt(PUBLIC_COOLDOWN_TAG);
            int maxCooldown = Math.max(ModConfig.COOLDOWN_TICKS.get(), 1);
            if (current > maxCooldown) {
                data.putInt(PUBLIC_COOLDOWN_TAG, maxCooldown);
            }
        }
    }
    public static void triggerTotemEffects(Player player, ItemStack stack) {
        player.setHealth(1.0F);
        player.removeAllEffects();
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));

        Level world = player.level();
        world.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
        player.playSound(SoundEvents.TOTEM_USE, 1.0F, 1.0F);

        // === 修改此处：移除旧粒子生成代码，改为发送网络包 ===
        if (!world.isClientSide) {
            Vec3 pos = player.position();
            NetworkHandler.CHANNEL.send(
                    PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player),
                    new NetworkHandler.TotemParticlesPacket(pos.x, pos.y, pos.z)
            );
        }
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

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    // 客户端耐久条渲染
    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isBarVisible(ItemStack stack) {
        Player player = Minecraft.getInstance().player;
        return player != null && player.getPersistentData().contains(PUBLIC_COOLDOWN_TAG);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getBarWidth(ItemStack stack) {
        Player player = Minecraft.getInstance().player;
        if (player != null && player.getPersistentData().contains(PUBLIC_COOLDOWN_TAG)) {
            int remaining = player.getPersistentData().getInt(PUBLIC_COOLDOWN_TAG);
            return (int) (13.0F - (float) remaining * 13.0F / ModConfig.COOLDOWN_TICKS.get());
        }
        return 0;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getBarColor(ItemStack stack) {
        Player player = Minecraft.getInstance().player;
        if (player != null && player.getPersistentData().contains(PUBLIC_COOLDOWN_TAG)) {
            int remaining = player.getPersistentData().getInt(PUBLIC_COOLDOWN_TAG);
            float progress = (float) (ModConfig.COOLDOWN_TICKS.get() - remaining) / ModConfig.COOLDOWN_TICKS.get();
            return Mth.hsvToRgb(progress * 0.33F, 1.0F, 1.0F);
        }
        return super.getBarColor(stack);
    }
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.infinite_totem_undying.effect")
                .withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.infinite_totem_undying.description")
                .withStyle(ChatFormatting.GREEN));
        super.appendHoverText(stack, level, tooltip, flag); // 调用基类方法显示修饰符信息
    }
}