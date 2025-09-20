package com.jinqinxixi.bountifulbaubles.event.listener;

import com.jinqinxixi.bountifulbaubles.BountifulBaublesMod;
import com.jinqinxixi.bountifulbaubles.config.ModConfig;
import com.jinqinxixi.bountifulbaubles.util.CuriosUtil;
import com.jinqinxixi.bountifulbaubles.system.effect.ModEffects;
import com.jinqinxixi.bountifulbaubles.item.Baubles.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.Map;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = BountifulBaublesMod.MOD_ID)
public class ModEvents {

    // ==================== 暴击事件处理 ====================
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity().level().isClientSide()) return;

        handleFireResistance(event);
    }

    //================= Wrath Pendant Critical Hit Effects =================
    @SubscribeEvent
    public static void onCriticalHit(CriticalHitEvent event) {
        if (event.getResult() == Event.Result.ALLOW || (event.getResult() == Event.Result.DEFAULT && event.isVanillaCritical() && event.getDamageModifier() >= 1.5f)) { //We basically check if the event has been canceled, not exactly but works similar
            handleCriticalHit(event.getEntity());
        }
    }

    // ==================== 进食加速系统 ====================
    @SubscribeEvent
    public static void onItemUseStart(LivingEntityUseItemEvent.Start event) {
        if (event.getEntity() instanceof Player player &&
                event.getItem().isEdible() &&
                hasCurioItem(player, GluttonyPendantItem.class)) {
            event.setDuration((int) (event.getDuration() * 0.5f));
        }
    }

    @SubscribeEvent
    public static void onItemUseFinish(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof Player player &&
                hasCurioItem(player, GluttonyPendantItem.class)) {
            applySinfulEffect(player, event.getItem());
        }
    }

    // ==================== 击退免疫系统 ====================
    @SubscribeEvent
    public static void onLivingKnockBack(LivingKnockBackEvent event) {
        if (event.getEntity() instanceof Player player) {
            handleKnockbackImmunity(player, event);
        }
    }

    // ==================== 无限图腾触发逻辑（不消耗任何物品）====================
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player && !player.level().isClientSide) {
            // 检测所有可能的位置
            boolean hasTotem = CuriosUtil.hasCurioItem(player, InfiniteTotemOfUndyingItem.class) ||
                    isHoldingTotem(player);

            if (hasTotem && InfiniteTotemOfUndyingItem.isReady(player)) {
                event.setCanceled(true);

                // 获取任意一个图腾实例（优先Curios栏）
                ItemStack totemStack = CuriosUtil.findCurioItemStack(player, InfiniteTotemOfUndyingItem.class)
                        .orElseGet(() -> findHeldTotem(player));

                InfiniteTotemOfUndyingItem.triggerTotemEffects(player, totemStack);
                InfiniteTotemOfUndyingItem.startCooldown(player);
            }
        }
    }

    // 手持检测辅助方法
    private static boolean isHoldingTotem(Player player) {
        return player.getMainHandItem().getItem() instanceof InfiniteTotemOfUndyingItem ||
                player.getOffhandItem().getItem() instanceof InfiniteTotemOfUndyingItem;
    }

    // 获取手持图腾辅助方法
    private static ItemStack findHeldTotem(Player player) {
        ItemStack mainHand = player.getMainHandItem();
        return mainHand.getItem() instanceof InfiniteTotemOfUndyingItem ?
                mainHand : player.getOffhandItem();
    }


    // ==================== 工具方法 ====================
    private static void handleCriticalHit(Player player) {
        LazyOptional<ICuriosItemHandler> optional = player.getCapability(CuriosCapability.INVENTORY);
        optional.ifPresent(handler -> {
            for (Map.Entry<String, ICurioStacksHandler> entry : handler.getCurios().entrySet()) {
                for (int i = 0; i < entry.getValue().getSlots(); i++) {
                    ItemStack stack = entry.getValue().getStacks().getStackInSlot(i);
                    if (stack.getItem() instanceof WrathPendantItem) {
                        int buffLevel = ModConfig.getWrathPendantBuffLevel();
                        int buffDuration = ModConfig.getWrathPendantBuffDuration();
                        player.addEffect(new MobEffectInstance(
                                ModEffects.SINFUL.get(), buffDuration, buffLevel, false, true));
                        return;
                    }
                }
            }
        });
    }

    private static void handleFireResistance(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        if (isFireDamage(source) && event.getEntity() instanceof Player player) {
            boolean hasProtection = hasShieldInHand(player) ||
                    hasCurioItem(player, ObsidianShieldItem.class) ||
                    hasCurioItem(player, AnkhShieldItem.class);

            if (hasProtection) {
                event.setAmount(event.getAmount() * 0.5f);
            }
        }
    }

    private static void handleKnockbackImmunity(Player player, LivingKnockBackEvent event) {
        boolean isHolding = hasShieldInHand(player);
        boolean hasCurio = hasCurioItem(player, CobaltShieldItem.class) ||
                hasCurioItem(player, ObsidianShieldItem.class) ||
                hasCurioItem(player, AnkhShieldItem.class);
        boolean isBlocking = player.isBlocking() && isHolding;

        if (isHolding || hasCurio || isBlocking) {
            event.setCanceled(true);
            if (isHolding) consumeHandheldShield(player);
        }
    }

    private static boolean hasShieldInHand(Player player) {
        return isShieldItem(player.getMainHandItem()) ||
                isShieldItem(player.getOffhandItem());
    }

    private static boolean isShieldItem(ItemStack stack) {
        return stack.getItem() instanceof CobaltShieldItem ||
                stack.getItem() instanceof ObsidianShieldItem ||
                stack.getItem() instanceof AnkhShieldItem;
    }

    private static void consumeHandheldShield(Player player) {
        if (isShieldItem(player.getMainHandItem())) {
            player.getMainHandItem().hurtAndBreak(1, player,
                    p -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
        } else if (isShieldItem(player.getOffhandItem())) {
            player.getOffhandItem().hurtAndBreak(1, player,
                    p -> p.broadcastBreakEvent(InteractionHand.OFF_HAND));
        }
    }

    public static <T extends Item> boolean hasCurioItem(Player player, Class<T> itemClass) {
        LazyOptional<ICuriosItemHandler> optional = player.getCapability(CuriosCapability.INVENTORY);
        if (!optional.isPresent()) return false;

        ICuriosItemHandler handler = optional.resolve().get();
        for (Map.Entry<String, ICurioStacksHandler> entry : handler.getCurios().entrySet()) {
            ICurioStacksHandler stacksHandler = entry.getValue();
            for (int i = 0; i < stacksHandler.getSlots(); i++) {
                if (itemClass.isInstance(stacksHandler.getStacks().getStackInSlot(i).getItem())) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void applySinfulEffect(Player player, ItemStack foodStack) {
        Optional.ofNullable(foodStack.getItem().getFoodProperties())
                .ifPresent(food -> {
                    int level = calculateSinLevel(food.getNutrition(), food.getSaturationModifier()) - 2;
                    if (level >= 0) {
                        player.addEffect(new MobEffectInstance(
                                ModEffects.SINFUL.get(), 200, level, false, true));
                    }
                });
    }

    // ==================== 辅助方法 ====================
    public static int calculateSinLevel(int hunger, float saturation) {
        return (int) Math.floor((hunger / 4.0) + (saturation / 6.0) + 1);
    }

    private static boolean isFireDamage(DamageSource source) {
        return source.is(DamageTypes.IN_FIRE) ||
                source.is(DamageTypes.FIREBALL) ||
                source.is(DamageTypes.ON_FIRE);
    }
}
