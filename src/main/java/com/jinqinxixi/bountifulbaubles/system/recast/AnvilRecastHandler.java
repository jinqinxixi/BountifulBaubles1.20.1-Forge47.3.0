package com.jinqinxixi.bountifulbaubles.system.recast;

import com.jinqinxixi.bountifulbaubles.config.ModConfig;
import com.jinqinxixi.bountifulbaubles.system.modifier.ModifiableBaubleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber
public class AnvilRecastHandler {
    private static final Map<Item, RecastRecipe> RECIPES = new HashMap<>();

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
        // 检查修饰系统是否启用
        if (!ModConfig.MODIFIER_ENABLED.get()) {
            return;
        }

        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();

        if (!left.isEmpty() && !right.isEmpty()) {
            RecastRecipe recipe = RECIPES.get(left.getItem());
            if (recipe != null && recipe.matches(right)) {
                // 创建输出物品的副本，保留所有NBT数据
                ItemStack output = left.copy();

                // 获取并清除修饰符相关的NBT标签
                CompoundTag tag = output.getTag();
                if (tag != null) {
                    // 移除修饰符标签
                    if (tag.contains(ModifiableBaubleItem.MODIFIER_TAG)) {
                        tag.remove(ModifiableBaubleItem.MODIFIER_TAG);
                    }
                    // 移除已初始化标记
                    if (tag.contains(ModifiableBaubleItem.INITIALIZED_TAG)) {
                        tag.remove(ModifiableBaubleItem.INITIALIZED_TAG);
                    }
                }

                event.setOutput(output);
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
    }
}