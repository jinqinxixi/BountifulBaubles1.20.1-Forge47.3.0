package com.jinqinxixi.bountifulbaubles.system.recast;


import com.jinqinxixi.bountifulbaubles.BountifulBaublesMod;
import com.jinqinxixi.bountifulbaubles.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import java.util.function.Consumer;

public class RecastRegistry extends RecipeProvider {
    public RecastRegistry(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        // 配方1: 超频戒指 + 潜影心 → 自由行动戒指
        createSmithingRecipe(consumer,
                Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE,
                ModItems.RING_OVERCLOCKING.get(),
                ModItems.SHULKER_HEART.get(),
                ModItems.RING_FREE_ACTION.get(),
                "free_action_ring");

        // 配方2: 钴盾 + 黑曜石头骨 → 黑曜石盾
        createSmithingRecipe(consumer,
                Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE,
                ModItems.COBALT_SHIELD.get(),
                ModItems.OBSIDIAN_SKULL.get(),
                ModItems.OBSIDIAN_SHIELD.get(),
                "obsidian_shield");

        createSmithingRecipe(consumer,
                Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE,
                ModItems.BEZOAR.get(),
                ModItems.BLACK_DRAGON_SCALE.get(),
                ModItems.MIXED_DRAGON_SCALE.get(),
                "mixed_dragon_scale");

        createSmithingRecipe(consumer,
                Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE,
                ModItems.BALLOON.get(),
                ModItems.LUCKY_HORSESHOE.get(),
                ModItems.HORSESHOE_BALLOON.get(),
                "horseshoe_balloon");

        createSmithingRecipe(consumer,
                Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE,
                ModItems.OBSIDIAN_SHIELD.get(),
                ModItems.ANKH_CHARM.get(),
                ModItems.ANKH_SHIELD.get(),
                "ankh_shield");
    }

    private void createSmithingRecipe(Consumer<FinishedRecipe> consumer,
                                      Item template,
                                      Item baseItem,
                                      Item additionItem,
                                      Item resultItem,
                                      String recipeName) {
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(template),
                        Ingredient.of(baseItem),
                        Ingredient.of(additionItem),
                        RecipeCategory.COMBAT,
                        resultItem)
                .unlocks("has_smithing_material", has(additionItem))
                .save(consumer, new ResourceLocation(BountifulBaublesMod.MOD_ID, recipeName));
    }
}
