package com.jinqinxixi.bountifulbaubles.item.Baubles;

import com.jinqinxixi.bountifulbaubles.system.modifier.ModifiableBaubleItem;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
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
import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber
public class AppleItem extends ModifiableBaubleItem {
    private static final MobEffect[] BLOCKED_EFFECTS = {MobEffects.HUNGER, MobEffects.CONFUSION};
    private static final ModifiableBaubleItem.Modifier[] MODIFIERS = ModifiableBaubleItem.Modifier.values();

    public AppleItem(Properties properties) {
        super(properties);
    }

    @Override
    public Modifier[] getModifiers() {
        return MODIFIERS;
    }


    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag unused) {
        return CuriosApi.createCurioProvider(new ICurio() {
            @Override
            public ItemStack getStack() {
                return stack;
            }

            @Override
            public List<Component> getAttributesTooltip(List<Component> tooltips) {
                return Collections.emptyList(); // 在父类中统一隐藏属性提示
            }

            @Override
            public void onEquip(SlotContext slotContext, ItemStack prevStack) {
                LivingEntity entity = slotContext.entity();
                if (entity instanceof Player player) {
                    // 父类逻辑：应用属性修正
                    applyModifier(player, stack);
                    // 子类逻辑：清除初始效果
                    clearBlockedEffects(player);
                }
            }

            @Override
            public void onUnequip(SlotContext slotContext, ItemStack newStack) {
                LivingEntity entity = slotContext.entity();
                if (entity instanceof Player player) {
                    // 父类逻辑：移除属性修正
                    removeModifier(player, stack);
                    // 子类逻辑：确保效果清除
                    clearBlockedEffects(player);
                }
            }

            @Override
            public boolean canEquip(SlotContext slotContext) {
                return true;
            }
        });
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        if (event.getEntity() instanceof Player player) {
            MobEffectInstance effect = event.getEffectInstance();
            if (effect != null && isBlockedEffect(effect.getEffect())) {
                CuriosApi.getCuriosInventory(player).ifPresent(curios -> {
                    // 修正方法：使用谓词表达式
                    if (curios.findFirstCurio(stack ->
                            stack.getItem() instanceof AppleItem).isPresent()) {
                        event.setResult(net.minecraftforge.eventbus.api.Event.Result.DENY);
                        clearBlockedEffects(player);
                    }
                });
            }
        }
    }

    private static boolean isBlockedEffect(MobEffect effect) {
        for (MobEffect blocked : BLOCKED_EFFECTS) {
            if (effect == blocked) return true;
        }
        return false;
    }

    private static void clearBlockedEffects(Player player) {
        for (MobEffect effect : BLOCKED_EFFECTS) {
            player.removeEffect(effect);
        }
    }


    // ===== 防附魔核心代码 =====
    // 1. 禁止铁砧/指令附魔
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    // 2. 附魔等级设为0（防止附魔台操作）
    @Override
    public int getEnchantmentValue() {
        return 0;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {

        tooltip.add(Component.translatable("tooltip.bountifulbaubles.apple.effects").withStyle(ChatFormatting.BLUE));
        super.appendHoverText(stack, level, tooltip, flag); // 调用基类方法显示修饰符信息
    }
}