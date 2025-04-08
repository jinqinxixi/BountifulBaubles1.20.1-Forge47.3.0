package com.jinqinxixi.bountifulbaubles.system.recast;

import com.jinqinxixi.bountifulbaubles.item.ModItems;
import net.minecraft.world.item.Item;

public class AnvilRecastRegistry {
    public static void registerAllRecipes() {
        register(ModItems.COBALT_SHIELD.get());
        register(ModItems.OBSIDIAN_SHIELD.get());
        register(ModItems.ANKH_SHIELD.get());
        register(ModItems.ANKH_CHARM.get());
        register(ModItems.CROSS_NECKLACE.get());
        register(ModItems.PHYLACTERY_CHARM.get());
        register(ModItems.PRIDE_PENDANT.get());
        register(ModItems.WRATH_PENDANT.get());
        register(ModItems.GLUTTONY_PENDANT.get());
        register(ModItems.VITAMINS.get());
        register(ModItems.AUTO_TORCH.get());
        register(ModItems.INFINITE_TOTEM.get());
        register(ModItems.LUCKY_HORSESHOE.get());
        register(ModItems.HORSESHOE_BALLOON.get());
        register(ModItems.BLACK_DRAGON_SCALE.get());
        register(ModItems.MIXED_DRAGON_SCALE.get());
        register(ModItems.SHULKER_HEART.get());
        register(ModItems.RING_OVERCLOCKING.get());
        register(ModItems.RING_FREE_ACTION.get());
        register(ModItems.CURIOUS_RING.get());
        register(ModItems.CURIOUS_AMULET.get());
        register(ModItems.CURIOUS_CROWN.get());
        register(ModItems.CURIOUS_KNUCKLES.get());
        register(ModItems.BALLOON.get());
        register(ModItems.OBSIDIAN_SKULL.get());
        register(ModItems.BEZOAR.get());
        register(ModItems.SUNGLASSES.get());
        register(ModItems.BOTTLED_CLOUD.get());
        register(ModItems.VAMPIRIC_GLOVE.get());
        register(ModItems.GAUNTLETS_DEXTERITY.get());
        register(ModItems.BROKEN_HEART.get());
        register(ModItems.APPLE.get());
        register(ModItems.MINDS_EYE.get());
        register(ModItems.LUCK_COIN.get());
        register(ModItems.DRAGON_BREATH.get());
        register(ModItems.ICE_SHARD.get());
        register(ModItems.THA_SPIDER.get());
        register(ModItems.CREEPO.get());
        register(ModItems.THA_WIZARD.get());
        register(ModItems.BOOK_O_ENCHANTINGITEM.get());
        register(ModItems.WARM_VOID.get());
        register(ModItems.GOLDEN_MELON.get());
        register(ModItems.FIRE_MIND.get());
        register(ModItems.DROP_SPINDLE.get());
        register(ModItems.DARK_EGG.get());
        register(ModItems.MOSSY_RING.get());
        register(ModItems.MOSSY_BELT.get());
        register(ModItems.MAD_AURA.get());
        register(ModItems.TURTLE_SHELL.get());
        register(ModItems.DARK_DAGGER.get());
        register(ModItems.EMBER.get());
        register(ModItems.WITHER_NAIL.get());
        register(ModItems.SERPENT_TOOTH.get());
        register(ModItems.BLAZE_HEART.get());




    }

    private static void register(Item item) {
        if (item != null) {
            AnvilRecastHandler.registerRecipe(item, ModItems.RESPLENDENT_TOKEN.get(), item);
        }
    }
}
