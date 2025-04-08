package com.jinqinxixi.bountifulbaubles.item.Baubles;

import com.jinqinxixi.bountifulbaubles.config.ModConfig;
import com.jinqinxixi.bountifulbaubles.system.modifier.ModifiableBaubleItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;


@Mod.EventBusSubscriber
public class BlazeHeartItem extends ModifiableBaubleItem {


    private static final ModifiableBaubleItem.Modifier[] MODIFIERS = ModifiableBaubleItem.Modifier.values();


    public BlazeHeartItem(Properties properties) {
        super(properties);
    }

    @Override
    public ModifiableBaubleItem.Modifier[] getModifiers() {
        return MODIFIERS;
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        // 调用父类的onEquip方法，它会处理修饰符的比较和应用
        super.onEquip(slotContext, prevStack, stack);
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        // 调用父类的onUnequip方法，它会处理修饰符的移除
        super.onUnequip(slotContext, newStack, stack);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        // 检查实体是否为玩家
        if (!(slotContext.entity() instanceof Player player)) {
            return;
        }

        // 计算当前血量百分比
        float healthPercent = (player.getHealth() / player.getMaxHealth()) * 100;

        // 根据血量百分比决定效果等级（0-based）
        int newRegenLevel;
        if (healthPercent <= 25) {
            newRegenLevel = 3; // 生命恢复 IV (显示为IV，实际是3)
        } else if (healthPercent <= 50) {
            newRegenLevel = 2; // 生命恢复 III (显示为III，实际是2)
        } else if (healthPercent <= 75) {
            newRegenLevel = 1; // 生命恢复 II (显示为II，实际是1)
        } else if (healthPercent < 100) {
            newRegenLevel = 0; // 生命恢复 I (显示为I，实际是0)
        } else {
            // 血量满时不做任何操作，让效果自然结束
            return;
        }

        // 获取当前的生命恢复效果
        MobEffectInstance currentEffect = player.getEffect(MobEffects.REGENERATION);

        if (currentEffect == null || currentEffect.getAmplifier() < newRegenLevel) {
            // 添加新的生命恢复效果
            player.addEffect(new MobEffectInstance(
                    MobEffects.REGENERATION,
                    ModConfig.getBlazeHeartDuration(),
                    newRegenLevel,
                    false, // 是否显示粒子
                    true   // 是否显示图标
            ));
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.blaze_heart.effect1")
                .withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.blaze_heart.effect2")
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