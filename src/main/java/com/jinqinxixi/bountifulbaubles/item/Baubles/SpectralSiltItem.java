package com.jinqinxixi.bountifulbaubles.item.Baubles;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class SpectralSiltItem extends Item {
    public SpectralSiltItem(Properties pProperties) {
        super(pProperties);
    }
    @Override
    public boolean isFoil(ItemStack stack) {
        return true; // 启用发光效果
    }
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.spectral_silt.effect")
                .withStyle(ChatFormatting.BLUE));
    }
    @Override
    public int getEnchantmentValue() {
        return 0; // 附魔等级为0
    }

    // 禁止任何形式的附魔（包括铁砧）
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }
}
