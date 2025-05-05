package com.jinqinxixi.bountifulbaubles.item.Baubles;

import com.jinqinxixi.bountifulbaubles.system.modifier.ModifiableBaubleItem;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;

import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber
public class BlackDragonScaleItem extends ModifiableBaubleItem {

    private static final ModifiableBaubleItem.Modifier[] MODIFIERS = ModifiableBaubleItem.Modifier.values();
    @Override
    public ModifiableBaubleItem.Modifier[] getModifiers() {
        return MODIFIERS;
    }

    private static final MobEffect[] BLOCKED_EFFECTS = {
            MobEffects.WITHER // 凋零效果
    };

    public BlackDragonScaleItem(Properties properties) {
        super(properties);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag unused) {
        return CuriosApi.createCurioProvider(new ICurio() {
            @Override
            public ItemStack getStack() {
                return stack;
            }



            // ===== 关键修复：集成父类逻辑 =====
            @Override
            public void onEquip(SlotContext slotContext, ItemStack prevStack) {
                LivingEntity entity = slotContext.entity();
                if (entity instanceof Player player) {
                    // 先执行父类属性修正
                    applyModifier(player, stack);
                    // 再执行子类效果清除
                    clearBlockedEffects(player);
                }
            }

            @Override
            public void onUnequip(SlotContext slotContext, ItemStack newStack) {
                LivingEntity entity = slotContext.entity();
                if (entity instanceof Player player) {
                    // 先移除父类属性修正
                    removeModifier(player, stack);
                    // 确保效果状态更新
                    updateEffectState(player);
                }
            }

            // ===== 必须实现的方法 =====
            @Override
            public boolean canEquip(SlotContext slotContext) {
                return true;
            }

            private void updateEffectState(Player player) {
                // 使用正确的查询方式
                boolean hasScale = CuriosApi.getCuriosInventory(player)
                        .resolve()
                        .map(curios -> curios.findFirstCurio(stack ->
                                stack.getItem() instanceof BlackDragonScaleItem).isPresent())
                        .orElse(false);

                // 如果没有佩戴且存在凋零效果
                if (!hasScale && player.hasEffect(MobEffects.WITHER)) {
                    MobEffectInstance wither = player.getEffect(MobEffects.WITHER);
                    if (wither != null) {
                        // 重新应用原始效果
                        player.addEffect(new MobEffectInstance(
                                MobEffects.WITHER,
                                wither.getDuration(),
                                wither.getAmplifier(),
                                wither.isAmbient(),
                                wither.isVisible(),
                                wither.showIcon()
                        ));
                    }
                }
            }
        });
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        if (event.getEntity() instanceof Player player) {
            MobEffectInstance effect = event.getEffectInstance();

            // 检测凋零效果
            if (effect != null && isBlockedEffect(effect.getEffect())) {
                CuriosApi.getCuriosInventory(player).ifPresent(curios -> {
                    if (curios.findFirstCurio(stack ->
                            stack.getItem() instanceof BlackDragonScaleItem).isPresent()) {
                        event.setResult(net.minecraftforge.eventbus.api.Event.Result.DENY);
                        clearBlockedEffects(player);
                    }
                });
            }
        }
    }

    // 清除需要免疫的效果
    private static void clearBlockedEffects(Player player) {
        for (MobEffect effect : BLOCKED_EFFECTS) {
            player.removeEffect(effect);
        }
    }

    // 检测是否为需要免疫的效果
    private static boolean isBlockedEffect(MobEffect effect) {
        for (MobEffect blocked : BLOCKED_EFFECTS) {
            if (effect == blocked) {
                return true;
            }
        }
        return false;
    }

    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {

        tooltip.add(Component.translatable("tooltip.bountifulbaubles.black_dragon_scale.effect").withStyle(ChatFormatting.BLUE));
        super.appendHoverText(stack, level, tooltip, flag); // 调用基类方法显示修饰符信息
    }
}
