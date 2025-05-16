package com.jinqinxixi.bountifulbaubles.item.Baubles;

import com.jinqinxixi.bountifulbaubles.system.modifier.ModifiableBaubleItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;

public class TurtleShellItem extends ModifiableBaubleItem {
    private static final ModifiableBaubleItem.Modifier[] MODIFIERS = ModifiableBaubleItem.Modifier.values();

    public TurtleShellItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public ModifiableBaubleItem.Modifier[] getModifiers() {
        return MODIFIERS;
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (slotContext.entity() instanceof Player player && !player.level().isClientSide) {
            // 给予潮涌能量效果
            player.addEffect(new MobEffectInstance(
                    MobEffects.CONDUIT_POWER,
                    Integer.MAX_VALUE,
                    0,
                    true,
                    false
            ));

            // 给予海豚的恩惠效果
            player.addEffect(new MobEffectInstance(
                    MobEffects.DOLPHINS_GRACE,
                    Integer.MAX_VALUE,
                    0,
                    true,
                    false
            ));
        }
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        // 调用父类的onEquip方法，处理修饰符
        super.onEquip(slotContext, prevStack, stack);

    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        // 调用父类的onUnequip方法，处理修饰符
        super.onUnequip(slotContext, newStack, stack);

        if (slotContext.entity() instanceof Player player) {
            // 移除效果
            player.removeEffect(MobEffects.CONDUIT_POWER);
            player.removeEffect(MobEffects.DOLPHINS_GRACE);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.turtle_shell.effect")
                .withStyle(ChatFormatting.BLUE));
        super.appendHoverText(stack, level, tooltip, flag);
    }
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
}