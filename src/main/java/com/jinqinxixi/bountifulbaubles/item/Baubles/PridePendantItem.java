package com.jinqinxixi.bountifulbaubles.item.Baubles;

import com.jinqinxixi.bountifulbaubles.system.effect.ModEffects;
import com.jinqinxixi.bountifulbaubles.system.modifier.ModifiableBaubleItem;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class PridePendantItem extends ModifiableBaubleItem {
    private static final double STEP_HEIGHT_BONUS = 0.6;

    private static final ModifiableBaubleItem.Modifier[] MODIFIERS = ModifiableBaubleItem.Modifier.values();

    @Override
    public ModifiableBaubleItem.Modifier[] getModifiers() {
        return MODIFIERS;
    }

    public PridePendantItem(Properties properties) {
        super(properties);
    }

    // ========== 唯一需要修改的部分 ==========
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

            // 新增父类方法调用
            @Override
            public void onEquip(SlotContext slotContext, ItemStack prevStack) {
                if (slotContext.entity() instanceof Player player) {
                    applyModifier(player, stack); // 调用父类方法
                    applyStepHeight(player);
                    checkSinfulEffect(player);
                }
            }

            @Override
            public void onUnequip(SlotContext slotContext, ItemStack newStack) {
                if (slotContext.entity() instanceof Player player) {
                    removeModifier(player, stack);
                    removeStepHeight(player);
                    player.removeEffect(ModEffects.SINFUL.get());
                }
            }


            @Override
            public void curioTick(SlotContext slotContext) {
                if (slotContext.entity() instanceof Player player) {
                    handleSinfulEffect(player);
                }
            }

            @Override
            public boolean canEquip(SlotContext slotContext) {
                return true;
            }

            @Override
            public boolean canUnequip(SlotContext slotContext) {
                return true;
            }
        });
    }

    // ========== 原有功能保持不变 ==========
    private void handleSinfulEffect(Player player) {
        if (player.getHealth() >= player.getMaxHealth()) {
            player.addEffect(new MobEffectInstance(ModEffects.SINFUL.get(), 40, 0, false, true, true));
        } else {
            player.removeEffect(ModEffects.SINFUL.get());
        }
    }

    private void checkSinfulEffect(Player player) {
        if (player.getHealth() >= player.getMaxHealth()) {
            player.addEffect(new MobEffectInstance(ModEffects.SINFUL.get(), 40, 0, false, true, true));
        }
    }

    private void applyStepHeight(Player player) {
        AttributeInstance stepHeight = player.getAttribute(ForgeMod.STEP_HEIGHT_ADDITION.get());
        if (stepHeight != null) {
            stepHeight.setBaseValue(STEP_HEIGHT_BONUS);
        }
    }

    private void removeStepHeight(Player player) {
        AttributeInstance stepHeight = player.getAttribute(ForgeMod.STEP_HEIGHT_ADDITION.get());
        if (stepHeight != null) {
            stepHeight.setBaseValue(0.0);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.pride_pendant.effect")
                .withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.pride_pendant.effect1")
                .withStyle(ChatFormatting.BLUE));
        super.appendHoverText(stack, level, tooltip, flag); // 调用基类方法显示修饰符信息
    }

    @Override
    public int getEnchantmentValue() {
        return 0;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }
}
