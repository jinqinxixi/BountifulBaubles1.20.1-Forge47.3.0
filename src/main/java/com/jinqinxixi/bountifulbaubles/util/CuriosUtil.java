package com.jinqinxixi.bountifulbaubles.util;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.Map;
import java.util.Optional;

public class CuriosUtil {
    public static <T extends Item> Optional<ItemStack> findCurioItemStack(Player player, Class<T> itemClass) {
        LazyOptional<ICuriosItemHandler> optional = player.getCapability(CuriosCapability.INVENTORY);
        if (!optional.isPresent()) return Optional.empty();

        ICuriosItemHandler handler = optional.resolve().get();
        for (Map.Entry<String, ICurioStacksHandler> entry : handler.getCurios().entrySet()) {
            ICurioStacksHandler stacksHandler = entry.getValue();
            for (int i = 0; i < stacksHandler.getSlots(); i++) {
                ItemStack stack = stacksHandler.getStacks().getStackInSlot(i);
                if (itemClass.isInstance(stack.getItem())) {
                    return Optional.of(stack);
                }
            }
        }
        return Optional.empty();
    }

    public static <T extends Item> boolean hasCurioItem(Player player, Class<T> itemClass) {
        return findCurioItemStack(player, itemClass).isPresent();
    }
}