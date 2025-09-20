package com.jinqinxixi.bountifulbaubles.item.Baubles;

import com.jinqinxixi.bountifulbaubles.config.ModConfig;
import com.jinqinxixi.bountifulbaubles.system.modifier.ModifiableBaubleItem;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;

public class MossyBeltItem extends ModifiableBaubleItem {
    private static final String REPAIR_TIMER_KEY = "RepairTimer";

    // 装备槽位顺序，从下到上
    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.FEET,    // 靴子
            EquipmentSlot.LEGS,    // 护腿
            EquipmentSlot.CHEST,   // 胸甲
            EquipmentSlot.HEAD     // 头盔
    };

    public MossyBeltItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    private static final ModifiableBaubleItem.Modifier[] MODIFIERS = ModifiableBaubleItem.Modifier.values();

    @Override
    public ModifiableBaubleItem.Modifier[] getModifiers() {
        return MODIFIERS;
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (slotContext.entity() instanceof Player player && !player.level().isClientSide) {
            CompoundTag tag = stack.getOrCreateTag();
            int timer = tag.getInt(REPAIR_TIMER_KEY);

            if (timer >= ModConfig.getMossyBeltRepairInterval()) {
                // 尝试修复装备
                if (repairArmor(player)) {
                    // 如果成功修复了某件装备，重置计时器
                    tag.putInt(REPAIR_TIMER_KEY, 0);
                }
            } else {
                // 增加计时器
                tag.putInt(REPAIR_TIMER_KEY, timer + 1);
            }
        }
    }

    private boolean repairArmor(Player player) {
        // 按照顺序检查并修复装备
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack armorStack = player.getItemBySlot(slot);

            // 检查是否是装备物品且需要修复
            if (canRepair(armorStack)) {
                // 如果当前装备需要修复，则修复它
                repairSingleItem(armorStack);
                return true; // 已修复一件装备，返回
            } else if (armorStack.getItem() instanceof ArmorItem && armorStack.isDamaged()) {
                // 如果当前槽位的装备未修复完成，则不继续检查后续装备
                return false;
            }
            // 如果当前槽位没有装备或装备已完全修复，继续检查下一个槽位
        }
        return false; // 没有需要修复的装备
    }

    private boolean canRepair(ItemStack stack) {
        return !stack.isEmpty() &&
                stack.getItem() instanceof ArmorItem &&
                stack.isDamaged() &&
                stack.getItem().canBeDepleted();
    }

    private void repairSingleItem(ItemStack stack) {
        // 使用配置的修复量
        int repairAmount = Math.min(
                ModConfig.getMossyBeltRepairAmount(),
                stack.getDamageValue()
        );
        stack.setDamageValue(stack.getDamageValue() - repairAmount);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.mossybelt.effects",
                        ModConfig.getMossyBeltRepairInterval() / 20.0f,  // 转换为秒
                        ModConfig.getMossyBeltRepairAmount())  // 修复量
                .withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.mossybelt.description")
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