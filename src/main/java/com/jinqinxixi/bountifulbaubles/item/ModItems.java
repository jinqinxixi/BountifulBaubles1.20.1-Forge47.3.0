package com.jinqinxixi.bountifulbaubles.item;

import com.jinqinxixi.bountifulbaubles.BountifulBaublesMod;
import com.jinqinxixi.bountifulbaubles.item.Baubles.*;

import com.jinqinxixi.bountifulbaubles.Potion.RecallPotion;
import com.jinqinxixi.bountifulbaubles.Potion.WormholePotion;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.jinqinxixi.bountifulbaubles.BountifulBaublesMod.MOD_ID;

public class ModItems {
    //物品注册
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    // 效果注册
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, BountifulBaublesMod.MOD_ID);

    // 物品注册
    public static final RegistryObject<Item> BROKEN_HEART = ITEMS.register("broken_heart",
            () -> new BrokenHeartItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> MAGIC_MIRROR = ITEMS.register("magic_mirror",
            () -> new MagicMirrorItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> WORMHOLE_MIRROR = ITEMS.register("wormhole_mirror",
            () -> new WormholeMirrorItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> DISINTEGRATION_TABLET = ITEMS.register("disintegration_tablet",
            () -> new com.jinqinxixi.bountifulbaubles.item.Baubles.DisintegrationTabletItem(
                    new com.jinqinxixi.bountifulbaubles.item.Baubles.DisintegrationTabletItem.TabletProperties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant().setNoConsume().fireResistant()));

    public static final RegistryObject<Item> ENDER_DRAGON_SCALE = ITEMS.register("ender_dragon_scale",
            () -> new EnderDragonScaleItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> AMULET_SIN_EMPTY = ITEMS.register("amulet_sin_empty",
            () -> new AmuletSinEmptyItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> SPECTRAL_SILT = ITEMS.register("spectral_silt",
            () -> new SpectralSiltItem(new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> BALLOON = ITEMS.register("balloon",
            () -> new BalloonItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> OBSIDIAN_SKULL = ITEMS.register("obsidian_skull",
            () -> new ObsidianSkullItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> SUNGLASSES = ITEMS.register("sunglasses",
            () -> new SunglassesItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> APPLE = ITEMS.register("apple",
            () -> new AppleItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> VITAMINS = ITEMS.register("vitamins",
            () -> new VitaminsItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> RING_OVERCLOCKING = ITEMS.register("ring_overclocking",
            () -> new RingOverclockingItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> SHULKER_HEART = ITEMS.register("shulker_heart",
            () -> new ShulkerHeartItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> RING_FREE_ACTION = ITEMS.register("ring_free_action",
            () -> new RingFreeActionItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

 public static final RegistryObject<Item> BEZOAR = ITEMS.register("bezoar",
            () -> new BezoarItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

 public static final RegistryObject<Item> BLACK_DRAGON_SCALE = ITEMS.register("black_dragon_scale",
            () -> new BlackDragonScaleItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

 public static final RegistryObject<Item> MIXED_DRAGON_SCALE = ITEMS.register("mixed_dragon_scale",
            () -> new MixedDragonScaleItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

 public static final RegistryObject<Item> LUCKY_HORSESHOE = ITEMS.register("lucky_horseshoe",
            () -> new LuckyHorseshoeItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

 public static final RegistryObject<Item> ANKH_CHARM = ITEMS.register("ankh_charm",
            () -> new AnkhCharmItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

 public static final RegistryObject<Item> HORSESHOE_BALLOON = ITEMS.register("horseshoe_balloon",
            () -> new HorseshoeBalloonItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

 public static final RegistryObject<Item> CROSS_NECKLACE = ITEMS.register("cross_necklace",
            () -> new CrossNecklaceItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

 public static final RegistryObject<Item> PHYLACTERY_CHARM = ITEMS.register("phylactery_charm",
            () -> new PhylacteryCharmItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

public static final RegistryObject<Item> PRIDE_PENDANT = ITEMS.register("pride_pendant",
            () -> new PridePendantItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

public static final RegistryObject<Item> WRATH_PENDANT = ITEMS.register("wrath_pendant",
            () -> new WrathPendantItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

public static final RegistryObject<Item> GLUTTONY_PENDANT = ITEMS.register("gluttony_pendant",
            () -> new GluttonyPendantItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

public static final RegistryObject<Item> RESPLENDENT_TOKEN = ITEMS.register("resplendent_token",
            () -> new ResplendentTokenItem(new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON).fireResistant()));

public static final RegistryObject<Item> BROKEN_BLACK_DRAGON_SCALE = ITEMS.register("broken_black_dragon_scale",
            () -> new BrokenBlackDragonScaleItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> CURIOUS_RING = ITEMS.register("curious_ring",
            () -> new CuriousRingItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> CURIOUS_AMULET = ITEMS.register("curious_amulet",
            () -> new CuriousAmuletItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> CURIOUS_CROWN = ITEMS.register("curious_crown",
            () -> new CuriousCrownItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> CURIOUS_KNUCKLES = ITEMS.register("curious_knuckles",
            () -> new CuriousKnucklesItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> AUTO_TORCH = ITEMS.register("auto_torch",
            () -> new AutoTorchItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> BOTTLED_CLOUD = ITEMS.register("bottled_cloud",
            () -> new BottledCloudItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> ENDLESS_PEARL = ITEMS.register("endless_pearl",
            () -> new EndlessPearlItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> VAMPIRIC_GLOVE = ITEMS.register("vampiric_glove",
            () -> new VampiricGloveItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> GAUNTLETS_DEXTERITY = ITEMS.register("gauntlets_dexterity",
            () -> new GauntletsOfDexterityItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> INFINITE_TOTEM = ITEMS.register(
            "infinite_totem_of_undying",
            () -> new com.jinqinxixi.bountifulbaubles.item.Baubles.InfiniteTotemOfUndyingItem(
                    new Item.Properties().stacksTo(1).durability(100).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> COBALT_SHIELD = ITEMS.register("cobalt_shield",
            () -> new CobaltShieldItem(new Item.Properties().stacksTo(1).durability(1008).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> OBSIDIAN_SHIELD = ITEMS.register("obsidian_shield",
            () -> new ObsidianShieldItem());

    public static final RegistryObject<Item> ANKH_SHIELD = ITEMS.register("ankh_shield",
            () -> new AnkhShieldItem());

    public static final RegistryObject<Item> POTION_WORMHOLE = ITEMS.register(
            "potion_wormhole",
            WormholePotion::new
    );

    public static final RegistryObject<Item> POTION_RECALL = ITEMS.register(
            "potion_recall",
            RecallPotion::new
    );

    public static final RegistryObject<Item> MINDS_EYE = ITEMS.register("minds_eye",
            () -> new MindsEyeItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> LUCK_COIN = ITEMS.register("luck_coin",
            () -> new LuckCoinItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> DRAGON_BREATH = ITEMS.register("dragon_breath",
            () -> new DragonBreathItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> ICE_SHARD = ITEMS.register("ice_shard",
            () -> new IceShardItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> THA_SPIDER = ITEMS.register("tha_spider",
            () -> new ThaSpiderItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> CREEPO = ITEMS.register("creepo",
            () -> new CreepoItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> THA_WIZARD = ITEMS.register("tha_wizard",
            () -> new ThaWizardItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> BOOK_O_ENCHANTINGITEM = ITEMS.register("book_o_enchanting",
            () -> new BookOEnchantingItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> WARM_VOID = ITEMS.register("warm_void",
            () -> new WarmVoidItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> GOLDEN_MELON = ITEMS.register("golden_melon",
            () -> new GoldenMelonItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> FIRE_MIND  = ITEMS.register("fire_mind",
            () -> new FireMindItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> DROP_SPINDLE  = ITEMS.register("drop_spindle",
            () -> new DropSpindleItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> DARK_EGG  = ITEMS.register("dark_egg",
            () -> new DarkEggItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> MOSSY_RING  = ITEMS.register("mossy_ring",
            () -> new MossyRingItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> MOSSY_BELT  = ITEMS.register("mossy_belt",
            () -> new MossyBeltItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> MAD_AURA  = ITEMS.register("mad_aura",
            () -> new MadAuraItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> TURTLE_SHELL  = ITEMS.register("turtle_shell",
            () -> new TurtleShellItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> DARK_DAGGER  = ITEMS.register("dark_dagger",
            () -> new DarkDaggerItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> EMBER   = ITEMS.register("ember",
            () -> new EmberItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> WITHER_NAIL    = ITEMS.register("wither_nail",
            () -> new WitherNailItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> SERPENT_TOOTH  = ITEMS.register("serpent_tooth",
            () -> new SerpentToothItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> BLAZE_HEART  = ITEMS.register("blaze_heart",
            () -> new BlazeHeartItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> STARFISH  = ITEMS.register("starfish",
            () -> new StarfishItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> GOLDEN_SKULL  = ITEMS.register("golden_skull",
            () -> new GoldenSkullItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> BUTCHERS_CLEAVER  = ITEMS.register("butchers_cleaver",
            () -> new ButchersCleaverItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> KARMA  = ITEMS.register("karma",
            () -> new KarmaItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> OXALIS  = ITEMS.register("oxalis",
            () -> new OxalisItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> GLORY_SHARDS= ITEMS.register("glory_shards",
            () -> new GloryShardsItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> RUBY_HEART= ITEMS.register("ruby_heart",
            () -> new RubyHeartItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> ROCK_CANDY= ITEMS.register("rock_candy",
            () -> new RockCandyItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant()));

    public static final RegistryObject<Item> TREASURE_BAG  = ITEMS.register("treasure_bag",
            () -> new TreasureBagItem(new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON).fireResistant()));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
        EFFECTS.register(eventBus);
    }

}
