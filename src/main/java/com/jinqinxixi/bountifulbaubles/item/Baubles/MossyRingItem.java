package com.jinqinxixi.bountifulbaubles.item.Baubles;

import com.jinqinxixi.bountifulbaubles.config.ModConfig;
import com.jinqinxixi.bountifulbaubles.system.modifier.ModifiableBaubleItem;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nullable;
import java.util.List;

public class MossyRingItem extends ModifiableBaubleItem {
    private static final String REPAIR_TIMER_KEY = "RepairTimer";

    public MossyRingItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    private static final ModifiableBaubleItem.Modifier[] MODIFIERS = ModifiableBaubleItem.Modifier.values();

    @Override
    public ModifiableBaubleItem.Modifier[] getModifiers() {
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
            public void onEquip(SlotContext slotContext, ItemStack prevStack) {
                if (slotContext.entity() instanceof Player player) {
                    applyModifier(player, stack);
                }
            }

            @Override
            public void onUnequip(SlotContext slotContext, ItemStack newStack) {
                if (slotContext.entity() instanceof Player player) {
                    removeModifier(player, stack);
                }
            }

            @Override
            public void curioTick(SlotContext slotContext) {
                if (slotContext.entity() instanceof Player player && !player.level().isClientSide) {
                    CompoundTag tag = stack.getOrCreateTag();
                    int timer = tag.getInt(REPAIR_TIMER_KEY);

                    if (timer >= ModConfig.getMossyRingRepairInterval()) {
                        // 尝试修复物品
                        if (repairItem(player)) {
                            // 重置计时器
                            tag.putInt(REPAIR_TIMER_KEY, 0);
                        } else {
                            timer++; // 如果没有物品需要修复，继续计时
                        }
                    } else {
                        // 增加计时器
                        tag.putInt(REPAIR_TIMER_KEY, timer + 1);
                    }
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

    private boolean repairItem(Player player) {
        // 先检查主手
        ItemStack mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (canRepair(mainHand)) {
            repairSingleItem(mainHand, player);
            return true;
        }

        // 如果主手不需要修复，则检查副手
        ItemStack offHand = player.getItemInHand(InteractionHand.OFF_HAND);
        if (canRepair(offHand)) {
            repairSingleItem(offHand, player);
            return true;
        }

        return false; // 没有物品需要修复
    }

    private boolean canRepair(ItemStack stack) {
        return !stack.isEmpty() && stack.isDamaged() && stack.getItem().canBeDepleted();
    }

    private void repairSingleItem(ItemStack stack, Player player) {
        // 使用配置的修复量
        int repairAmount = Math.min(
                ModConfig.getMossyRingRepairAmount(),
                stack.getDamageValue()
        );
        stack.setDamageValue(stack.getDamageValue() - repairAmount);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.mossyring.effects",
                        ModConfig.getMossyRingRepairInterval() / 20.0f,  // 转换为秒
                        ModConfig.getMossyRingRepairAmount())  // 修复量
                .withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.mossyring.description")
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