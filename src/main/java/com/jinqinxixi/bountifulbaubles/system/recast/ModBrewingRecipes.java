package com.jinqinxixi.bountifulbaubles.system.recast;

import com.jinqinxixi.bountifulbaubles.item.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegisterEvent;

public class ModBrewingRecipes {

    public static void register(IEventBus eventBus) {
        eventBus.addListener(ModBrewingRecipes::registerBrewingRecipes);
    }

    private static void registerBrewingRecipes(RegisterEvent event) {
        event.register(BuiltInRegistries.POTION.key(), helper -> {
            // 回忆药水配方：笨拙药水 + 石英 -> 回忆药水
            BrewingRecipeRegistry.addRecipe(
                    new BrewingRecipe(
                            Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.AWKWARD)),
                            Ingredient.of(Items.QUARTZ),
                            ModItems.POTION_RECALL.get().getDefaultInstance()
                    )
            );

            // 虫洞药水配方：回忆药水 + 末影珍珠 -> 虫洞药水
            BrewingRecipeRegistry.addRecipe(
                    new BrewingRecipe(
                            Ingredient.of(ModItems.POTION_RECALL.get()),
                            Ingredient.of(Items.ENDER_PEARL),
                            ModItems.POTION_WORMHOLE.get().getDefaultInstance()
                    )
            );
        });
    }
}