package com.jinqinxixi.bountifulbaubles.system.recast;

import com.jinqinxixi.bountifulbaubles.config.ModConfig;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber
public class AnvilRecastHandler {
    private static final Map<Item, RecastRecipe> RECIPES = new HashMap<>();

    // 通过方法获取当前配置值
    private static int getExpCost() {
        return ModConfig.ANVIL_RECAST_EXP_COST.get();
    }

    private static int getMaterialCost() {
        return ModConfig.ANVIL_RECAST_MATERIAL_COST.get();
    }

    public static void registerRecipe(Item baseItem, Item tokenItem, Item resultItem) {
        RECIPES.put(baseItem, new RecastRecipe(tokenItem, resultItem));
    }

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();

        if (!left.isEmpty() && !right.isEmpty()) {
            RecastRecipe recipe = RECIPES.get(left.getItem());
            if (recipe != null && recipe.matches(right)) {
                event.setOutput(recipe.getResult(left));
                event.setCost(getExpCost());
                event.setMaterialCost(getMaterialCost());
            }
        }
    }

    public static class RecastRecipe {
        private final Item tokenItem;
        private final Item resultItem;

        public RecastRecipe(Item tokenItem, Item resultItem) {
            this.tokenItem = tokenItem;
            this.resultItem = resultItem;
        }

        public boolean matches(ItemStack rightStack) {
            return rightStack.getItem() == tokenItem;
        }

        public ItemStack getResult(ItemStack input) {
            return new ItemStack(resultItem, input.getCount());
        }
    }
}
