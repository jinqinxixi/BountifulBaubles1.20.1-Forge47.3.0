package com.jinqinxixi.bountifulbaubles.system.modifier;

import com.jinqinxixi.bountifulbaubles.config.ModifierConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModifierTooltipHelper {
//
//    @SubscribeEvent
//    @OnlyIn(Dist.CLIENT)
//    public static void onItemTooltip(ItemTooltipEvent event) {
//        ItemStack stack = event.getItemStack();
//        List<Component> tooltip = event.getToolTip();
//
//        // 1. 检查物品ID是否在配置文件中
//        String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
//        ModifierConfig config = ModifierConfig.load();
//
//        if (!config.isItemModifiable(itemId)) {
//            return;
//        }
//
//        // 检查铁砧界面
//        boolean isAnvilPreview = false;
//        if (Minecraft.getInstance().screen instanceof AnvilScreen anvilScreen) {
//            Slot hoveredSlot = anvilScreen.getSlotUnderMouse();
//            if (hoveredSlot != null && hoveredSlot.getSlotIndex() == 2) {
//                isAnvilPreview = true;
//            }
//        }
//
//        // 2. 显示修饰符信息
//        CompoundTag tag = stack.getTag();
//        if (tag != null && tag.contains("ModifierType") && !isAnvilPreview) {
//            try {
//                String modifierName = tag.getString("ModifierType");
//                CurioAttributeEvents.Modifier modifier = CurioAttributeEvents.Modifier.valueOf(modifierName);
//
//                String formattedValue;
//                if (modifier.amount == (int) modifier.amount) {
//                    formattedValue = String.valueOf((int) modifier.amount);
//                } else if (modifier.amount >= 1.0) {
//                    formattedValue = String.format("%.1f", modifier.amount);
//                } else {
//                    formattedValue = String.format("%d%%", (int) (modifier.amount * 100));
//                }
//
//                tooltip.add(Component.literal("◆ ")
//                        .withStyle(ChatFormatting.LIGHT_PURPLE)
//                        .append(Component.translatable("modifier.bountifulbaubles." + modifier.name)
//                                .withStyle(ChatFormatting.LIGHT_PURPLE))
//                        .append(Component.literal(" +" + formattedValue)
//                                .withStyle(ChatFormatting.GREEN)));
//            } catch (Exception e) {
//            }
//        } else {
//            tooltip.add(Component.translatable("tooltip.bountifulbaubles.unidentified")
//                    .withStyle(ChatFormatting.GRAY));
//        }
//    }
}