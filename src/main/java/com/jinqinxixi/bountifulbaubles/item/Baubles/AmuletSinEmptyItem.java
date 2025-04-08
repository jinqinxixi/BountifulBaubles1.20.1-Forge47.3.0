package com.jinqinxixi.bountifulbaubles.item.Baubles;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class AmuletSinEmptyItem extends Item {
    public AmuletSinEmptyItem(Properties pProperties) {
        super(pProperties);
    }
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.amulet_sin_empty.component").withStyle(ChatFormatting.BLUE));
    }
}
