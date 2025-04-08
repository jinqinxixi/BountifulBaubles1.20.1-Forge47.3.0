package com.jinqinxixi.bountifulbaubles.item.Baubles;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class DisintegrationTabletItem extends Item {

    // 自定义属性类（支持完整链式调用）
    public static class TabletProperties extends Item.Properties {
        private boolean noConsume = false;

        public TabletProperties setNoConsume() {
            this.noConsume = true;
            return this;
        }

        // 覆盖所有父类方法以保持链式能力
        @Override
        public TabletProperties stacksTo(int maxStackSize) {
            super.stacksTo(maxStackSize);
            return this;
        }

        @Override
        public TabletProperties rarity(Rarity rarity) {
            super.rarity(rarity);
            return this;
        }

        @Override
        public TabletProperties fireResistant() {
            super.fireResistant();
            return this;
        }
    }

    public DisintegrationTabletItem(TabletProperties properties) {
        super(properties);
    }

    // ===== 核心功能实现 =====
    @Override
    public boolean hasCraftingRemainingItem() {
        return true;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        ItemStack newStack = stack.copy();
        newStack.setCount(1); // 确保只保留一个
        return newStack;
    }
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.disintegration_tablet.effect")
                .withStyle(ChatFormatting.BLUE));
    }
}

