package com.jinqinxixi.bountifulbaubles.config;

import com.jinqinxixi.bountifulbaubles.BountifulBaublesMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.*;

public class ModConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    // 生物掉落配置组
    public static final ForgeConfigSpec.DoubleValue HUSK_APPLE_CHANCE;
    public static final ForgeConfigSpec.DoubleValue STRAY_RING_CHANCE;
    public static final ForgeConfigSpec.DoubleValue SHULKER_HEART_CHANCE;
    public static final ForgeConfigSpec.DoubleValue CAVE_SPIDER_BEZOAR_CHANCE;
    public static final ForgeConfigSpec.DoubleValue EVOKER_TOTEM_CHANCE;
    public static final ForgeConfigSpec.DoubleValue ELDER_GUARDIAN_VITAMIN_CHANCE;
    public static final ForgeConfigSpec.DoubleValue DRAGON_SCALE_CHANCE;
    public static final ForgeConfigSpec.IntValue DRAGON_SCALE_MIN;
    public static final ForgeConfigSpec.IntValue DRAGON_SCALE_MAX;


    // === 原有配置项 ===
    public static final ForgeConfigSpec.IntValue ANVIL_RECAST_EXP_COST;
    public static final ForgeConfigSpec.IntValue ANVIL_RECAST_MATERIAL_COST;
    public static final ForgeConfigSpec.DoubleValue SPRINT_JUMP_VERTICAL;
    public static final ForgeConfigSpec.DoubleValue SPRINT_JUMP_HORIZONTAL;
    public static final ForgeConfigSpec.IntValue COOLDOWN_TICKS;
    public static final ForgeConfigSpec.BooleanValue ENABLE_CURIOS;
    public static final ForgeConfigSpec.DoubleValue VAMPIRIC_PERCENT;
    public static final ForgeConfigSpec.DoubleValue VAMPIRIC_MAX;
    public static final ForgeConfigSpec.DoubleValue GAUNTLETS_ATTACK_SPEED;
    public static final ForgeConfigSpec.BooleanValue MODIFIER_ENABLED;

    public static final ForgeConfigSpec.IntValue TACTICAL_SCAN_INTERVAL;
    public static final ForgeConfigSpec.IntValue TACTICAL_MARK_DURATION;
    public static final ForgeConfigSpec.DoubleValue TACTICAL_SCAN_RADIUS;
    public static final ForgeConfigSpec.DoubleValue TACTICAL_UP_RANGE;
    public static final ForgeConfigSpec.DoubleValue TACTICAL_DOWN_RANGE;
    public static final ForgeConfigSpec.DoubleValue TACTICAL_DAMAGE_MULTIPLIER;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Double>> LUCK_COIN_DAMAGE_BONUSES;
    public static final ForgeConfigSpec.ConfigValue<List<? extends Double>> LUCK_COIN_PROBABILITIES;
    public static final ForgeConfigSpec.DoubleValue THA_WIZARD_DURATION_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue DROP_SPINDLE_REPAIR_CHANCE;
    public static final ForgeConfigSpec.IntValue DROP_SPINDLE_REPAIR_AMOUNT;
    public static final ForgeConfigSpec.IntValue DARK_EGG_COOLDOWN;
    public static final ForgeConfigSpec.IntValue DARK_EGG_VEX_COUNT;
    public static final ForgeConfigSpec.IntValue DARK_EGG_VEX_LIFETIME;
    public static final ForgeConfigSpec.IntValue MOSSY_RING_REPAIR_INTERVAL;
    public static final ForgeConfigSpec.IntValue MOSSY_RING_REPAIR_AMOUNT;
    public static final ForgeConfigSpec.IntValue MOSSY_BELT_REPAIR_INTERVAL;
    public static final ForgeConfigSpec.IntValue MOSSY_BELT_REPAIR_AMOUNT;
    public static final ForgeConfigSpec.IntValue MAD_AURA_COOLDOWN;
    public static final ForgeConfigSpec.DoubleValue DARK_DAGGER_EXECUTE_THRESHOLD;
    public static final ForgeConfigSpec.DoubleValue DARK_DAGGER_EXECUTE_DAMAGE;
    public static final ForgeConfigSpec.IntValue BLAZE_HEART_DURATION;
    public static final ForgeConfigSpec.IntValue WRATH_PENDANT_BUFF_DURATION;
    public static final ForgeConfigSpec.IntValue WRATH_PENDANT_BUFF_LEVEL;





    // === 战利品配置项 ===
    public static final ForgeConfigSpec.ConfigValue<String> DESERT_PYRAMID_LOOT;
    public static final ForgeConfigSpec.ConfigValue<String> JUNGLE_TEMPLE_LOOT;
    public static final ForgeConfigSpec.ConfigValue<String> ABANDONED_MINESHAFT_LOOT;
    public static final ForgeConfigSpec.ConfigValue<String> STRONGHOLD_LIBRARY_LOOT;
    public static final ForgeConfigSpec.ConfigValue<String> WOODLAND_MANSION_LOOT;
    public static final ForgeConfigSpec.ConfigValue<String> VILLAGE_TEMPLE_LOOT;
    public static final ForgeConfigSpec.ConfigValue<String> SHIPWRECK_TREASURE_LOOT;
    public static final ForgeConfigSpec.ConfigValue<String> PILLAGER_OUTPOST_LOOT;
    public static final ForgeConfigSpec.ConfigValue<String> BURIED_TREASURE_LOOT;
    public static final ForgeConfigSpec.ConfigValue<String> UNDERWATER_RUIN_BIG_LOOT;
    public static final ForgeConfigSpec.ConfigValue<String> UNDERWATER_RUIN_SMALL_LOOT;
    public static final ForgeConfigSpec.ConfigValue<String> IGLOO_CHEST_LOOT;
    public static final ForgeConfigSpec.ConfigValue<String> BASTION_TREASURE_LOOT;
    public static final ForgeConfigSpec.ConfigValue<String> BASTION_BRIDGE_LOOT;
    public static final ForgeConfigSpec.ConfigValue<String> BASTION_HOUSING_LOOT;
    public static final ForgeConfigSpec.ConfigValue<String> BASTION_OTHER_LOOT;
    public static final ForgeConfigSpec.ConfigValue<String> RUINED_PORTAL_LOOT;
    public static final ForgeConfigSpec.ConfigValue<String> END_CITY_LOOT;
    public static final ForgeConfigSpec.ConfigValue<String> ANCIENT_CITY_LOOT;
    public static final ForgeConfigSpec.ConfigValue<String> SIMPLE_DUNGEON_LOOT;
    public static final ForgeConfigSpec.ConfigValue<String> STRONGHOLD_CORRIDOR_LOOT;
    public static final ForgeConfigSpec.ConfigValue<String> FOSSIL_DINOSAUR_LOOT;
    public static final ForgeConfigSpec.ConfigValue<String> FOSSIL_MAMMAL_LOOT;
    public static final ForgeConfigSpec.ConfigValue<String> SHIPWRECK_SUPPLY_LOOT;
    public static final ForgeConfigSpec.ConfigValue<String> WOODLAND_CARTOGRAPHY_LOOT;
    public static final ForgeConfigSpec.ConfigValue<String> STRONGHOLD_CROSSING_LOOT;
    public static final ForgeConfigSpec.ConfigValue<String> SHIPWRECK_MAP_LOOT;
    public static final ForgeConfigSpec.DoubleValue CROSS_NECKLACE_INVULNERABILITY;
    public static final ForgeConfigSpec.BooleanValue CROSS_NECKLACE_PLAYERS_ONLY;


    // === 村庄职业配置 ===
    public static final ForgeConfigSpec.ConfigValue<String> VILLAGE_ARMORER_LOOT;
    public static final ForgeConfigSpec.ConfigValue<String> VILLAGE_BUTCHER_LOOT;
    public static final ForgeConfigSpec.ConfigValue<String> VILLAGE_CARTOGRAPHER_LOOT;
    public static final ForgeConfigSpec.ConfigValue<String> VILLAGE_PLAINS_HOUSE_LOOT;
    public static final ForgeConfigSpec.ConfigValue<String> VILLAGE_FISHER_LOOT;
    public static final ForgeConfigSpec.ConfigValue<String> VILLAGE_FLETCHER_LOOT;
    public static final ForgeConfigSpec.ConfigValue<String> VILLAGE_TANNERY_LOOT;
    public static final ForgeConfigSpec.ConfigValue<String> VILLAGE_LIBRARY_LOOT;
    public static final ForgeConfigSpec.ConfigValue<String> VILLAGE_MASON_LOOT;
    public static final ForgeConfigSpec.ConfigValue<String> VILLAGE_SHEPHERD_LOOT;
    public static final ForgeConfigSpec.ConfigValue<String> VILLAGE_TOOLSMITH_LOOT;
    public static final ForgeConfigSpec.ConfigValue<String> VILLAGE_WEAPONSMITH_LOOT;
    public static final ForgeConfigSpec.ConfigValue<String> VILLAGE_DESERT_HOUSE_LOOT;
    public static final ForgeConfigSpec.ConfigValue<String> VILLAGE_SNOWY_HOUSE_LOOT;
    public static final ForgeConfigSpec.ConfigValue<String> VILLAGE_SAVANNA_HOUSE_LOOT;
    public static final ForgeConfigSpec.ConfigValue<String> VILLAGE_TAIGA_HOUSE_LOOT;


    static {

        BUILDER.push("Modifier Settings")
                .comment("饰品修饰系统");
        MODIFIER_ENABLED = BUILDER
                .comment("是否启用饰品修饰器系统(true/false)")
                .define("modifierEnabled", true);
        BUILDER.pop();


        // === 配置组===
        BUILDER.push("Anvil Recast Settings");
        ANVIL_RECAST_EXP_COST = BUILDER
                .comment("全局重铸经验消耗 (0-100)")
                .defineInRange("recastExpCost", 3, 0, 100);
        ANVIL_RECAST_MATERIAL_COST = BUILDER
                .comment("全局重铸材料消耗 (1-64)")
                .defineInRange("recastMaterialCost", 1, 1, 64);
        BUILDER.pop();

        BUILDER.push("Cloud in a Bottle Settings");
        SPRINT_JUMP_VERTICAL = BUILDER
                .comment("疾跑跳跃垂直加成 (默认0.5)")
                .defineInRange("sprintVertical", 0.5D, 0.0D, 2.0D);
        SPRINT_JUMP_HORIZONTAL = BUILDER
                .comment("疾跑跳跃水平加成 (默认0.5)")
                .defineInRange("sprintHorizontal", 0.5D, 0.0D, 2.0D);
        BUILDER.pop();

        BUILDER.push("Totem Settings");
        COOLDOWN_TICKS = BUILDER
                .comment("冷却时间(单位tick, 20tick=1秒)")
                .defineInRange("cooldown", 6000, 20, 72000);
        ENABLE_CURIOS = BUILDER
                .comment("启用Curios支持")
                .define("enableCurios", true);
        BUILDER.pop();

        BUILDER.push("Vampiric Glove Settings");
        VAMPIRIC_PERCENT = BUILDER
                .comment("吸血百分比 (0.0-1.0)")
                .defineInRange("vampiricPercent", 0.2D, 0.0D, 1.0D);
        VAMPIRIC_MAX = BUILDER
                .comment("单次最大吸血量")
                .defineInRange("vampiricMax", 6.0D, 0.0D, 100.0D);
        BUILDER.pop();

        BUILDER.push("Gauntlets Settings");
        GAUNTLETS_ATTACK_SPEED = BUILDER
                .comment("攻击速度加成倍率 (0.6=+60%)")
                .defineInRange("attackSpeed", 0.6D, 0.0D, 2.0D);
        BUILDER.pop();

        BUILDER.push("Cross Necklace Settings")
                .comment("十字项链相关配置");

        CROSS_NECKLACE_INVULNERABILITY = BUILDER
                .comment("无敌时间延长倍率 (0.8=延长80%，设为0禁用效果)")
                .defineInRange("crossNecklaceBoost", 0.8D, 0.0D, 5.0D);

        CROSS_NECKLACE_PLAYERS_ONLY = BUILDER
                .comment("是否仅对玩家生效")
                .define("crossNecklacePlayersOnly", true);

        BUILDER.pop();

        BUILDER.push("Wrath Pendant Settings")
                .comment("愤怒吊坠相关配置");

        WRATH_PENDANT_BUFF_LEVEL = BUILDER
                .comment("愤怒吊坠的BUFF等级 (默认3)")
                .defineInRange("wrathPendantBuffLevel", 3, 1, 10);

        WRATH_PENDANT_BUFF_DURATION = BUILDER
                .comment("愤怒吊坠的BUFF持续时间 (单位：tick，20tick=1秒，默认100)")
                .defineInRange("wrathPendantBuffDuration", 100, 20, 1200);

        BUILDER.pop();

        BUILDER.push("Tactical Goggles Settings")
                .comment("洞察之眼相关配置");

        TACTICAL_SCAN_INTERVAL = BUILDER
                .comment("扫描间隔（单位：tick，20tick = 1秒）")
                .defineInRange("scanInterval", 300, 1, 1200);

        TACTICAL_MARK_DURATION = BUILDER
                .comment("标记持续时间（单位：tick）")
                .defineInRange("markDuration", 100, 1, 600);

        TACTICAL_SCAN_RADIUS = BUILDER
                .comment("水平扫描半径")
                .defineInRange("scanRadius", 10.0D, 1.0D, 32.0D);

        TACTICAL_UP_RANGE = BUILDER
                .comment("向上扫描范围")
                .defineInRange("upRange", 5.0D, 1.0D, 16.0D);

        TACTICAL_DOWN_RANGE = BUILDER
                .comment("向下扫描范围")
                .defineInRange("downRange", 5.0D, 1.0D, 16.0D);

        TACTICAL_DAMAGE_MULTIPLIER = BUILDER
                .comment("对标记目标的伤害倍率")
                .defineInRange("damageMultiplier", 1.5D, 1.0D, 5.0D);

        BUILDER.pop();

        BUILDER.push("Lucky Coin Settings")
                .comment("幸运硬币相关配置");

        LUCK_COIN_DAMAGE_BONUSES = BUILDER
                .comment("伤害加成列表（0.05表示+5%伤害）")
                .defineList("damageBonus",
                        Arrays.asList(0.05, 0.10, 0.30),
                        o -> o instanceof Double && (Double) o >= 0.0 && (Double) o <= 5.0);

        LUCK_COIN_PROBABILITIES = BUILDER
                .comment("每个伤害加成对应的触发概率（必须和伤害加成数组长度相同，且概率和必须等于1.0）")
                .defineList("probability",
                        Arrays.asList(0.70, 0.20, 0.10),
                        o -> o instanceof Double && (Double) o >= 0.0 && (Double) o <= 1.0);

        BUILDER.pop();

        BUILDER.push("Tha Wizard Settings")
                .comment("小巫师饰品相关配置");

        THA_WIZARD_DURATION_MULTIPLIER = BUILDER
                .comment("药水效果持续时间倍率 (1.5 = 延长50%)")
                .defineInRange("durationMultiplier", 1.5D, 1.0D, 5.0D);

        BUILDER.pop();

        BUILDER.push("Drop Spindle Settings")
                .comment("纺锤相关配置");

        DROP_SPINDLE_REPAIR_CHANCE = BUILDER
                .comment("每次攻击时触发修复的概率 (0.25 = 25%)")
                .defineInRange("repairChance", 0.25D, 0.0D, 1.0D);

        DROP_SPINDLE_REPAIR_AMOUNT = BUILDER
                .comment("每次修复的耐久度")
                .defineInRange("repairAmount", 1, 1, 64);

        BUILDER.pop();

        BUILDER.push("Dark Egg Settings")
                .comment("暗影之卵相关配置");

        DARK_EGG_COOLDOWN = BUILDER
                .comment("技能冷却时间（单位：tick，20tick = 1秒）")
                .defineInRange("cooldown", 1200, 200, 6000);

        DARK_EGG_VEX_COUNT = BUILDER
                .comment("每次召唤的恼鬼数量")
                .defineInRange("vexCount", 3, 1, 10);

        DARK_EGG_VEX_LIFETIME = BUILDER
                .comment("恼鬼存活时间（单位：tick）")
                .defineInRange("vexLifetime", 600, 100, 2400);

        BUILDER.pop();

        BUILDER.push("Mossy Ring Settings")
                .comment("苔藓戒指相关配置");

        MOSSY_RING_REPAIR_INTERVAL = BUILDER
                .comment("修复间隔（单位：tick，20tick = 1秒）")
                .defineInRange("repairInterval", 60, 20, 200);

        MOSSY_RING_REPAIR_AMOUNT = BUILDER
                .comment("每次修复的耐久度")
                .defineInRange("repairAmount", 1, 1, 10);

        BUILDER.pop();

        BUILDER.push("Mossy Belt Settings")
                .comment("苔藓腰带相关配置");

        MOSSY_BELT_REPAIR_INTERVAL = BUILDER
                .comment("修复间隔（单位：tick，20tick = 1秒）")
                .defineInRange("repairInterval", 60, 20, 200);

        MOSSY_BELT_REPAIR_AMOUNT = BUILDER
                .comment("每次修复的耐久度")
                .defineInRange("repairAmount", 1, 1, 10);

        BUILDER.pop();

        BUILDER.push("Mad Aura Settings")
                .comment("炽光环相关配置");

        MAD_AURA_COOLDOWN = BUILDER
                .comment("技能冷却时间（单位：tick，20tick = 1秒）")
                .defineInRange("cooldown", 600, 200, 2400);

        BUILDER.pop();

        BUILDER.push("Dark Dagger Settings")
                .comment("暗影匕首相关配置");

        DARK_DAGGER_EXECUTE_THRESHOLD = BUILDER
                .comment("斩杀阈值（目标剩余生命值百分比，0.2 = 20%）")
                .defineInRange("executeThreshold", 0.20, 0.05, 0.50);

        DARK_DAGGER_EXECUTE_DAMAGE = BUILDER
                .comment("斩杀伤害值")
                .defineInRange("executeDamage", 999999.0, 1000.0, 9999999.0);

        BUILDER.pop();

        BUILDER.push("Blaze Heart Settings")
                .comment("烈焰之心相关配置");

        BLAZE_HEART_DURATION = BUILDER
                .comment("生命恢复效果持续时间（单位：tick，20tick = 1秒）")
                .defineInRange("regenerationDuration", 900, 20, 2400); // 默认45秒=900tick

        BUILDER.pop();

        //生物掉落概率
        BUILDER.push("Mob Drop Settings")
                .comment("所有概率值为0.0-1.0之间的浮点数，1.0=100%");

        HUSK_APPLE_CHANCE = BUILDER
                .comment("尸壳掉落禁果概率 (默认2.5%)")
                .defineInRange("huskAppleChance", 0.025D, 0.0D, 1.0D);

        STRAY_RING_CHANCE = BUILDER
                .comment("流浪者掉落超频戒指概率 (默认3%)")
                .defineInRange("strayRingChance", 0.03D, 0.0D, 1.0D);

        SHULKER_HEART_CHANCE = BUILDER
                .comment("潜影贝掉落潜影之心概率 (默认10%)")
                .defineInRange("shulkerHeartChance", 0.10D, 0.0D, 1.0D);

        CAVE_SPIDER_BEZOAR_CHANCE = BUILDER
                .comment("洞穴蜘蛛掉落牛黄概率 (默认5%)")
                .defineInRange("caveSpiderBezoarChance", 0.05D, 0.0D, 1.0D);

        EVOKER_TOTEM_CHANCE = BUILDER
                .comment("唤魔者掉落无限图腾概率 (默认10%)")
                .defineInRange("evokerTotemChance", 0.10D, 0.0D, 1.0D);

        ELDER_GUARDIAN_VITAMIN_CHANCE = BUILDER
                .comment("远古守卫掉落维生素概率 (默认100%)")
                .defineInRange("elderGuardianVitaminChance", 1.0D, 0.0D, 1.0D);

        DRAGON_SCALE_CHANCE = BUILDER
                .comment("末影龙掉落鳞片的概率 (默认100%)")
                .defineInRange("dragonScaleChance", 1.0D, 0.0D, 1.0D);

        DRAGON_SCALE_MIN = BUILDER
                .comment("鳞片最小掉落数量 (默认3)")
                .defineInRange("dragonScaleMin", 3, 0, 64);

        DRAGON_SCALE_MAX = BUILDER
                .comment("鳞片最大掉落数量 (默认6)")
                .defineInRange("dragonScaleMax", 6, 0, 64);

        BUILDER.pop();

        // === 战利品配置组 ===
        BUILDER.push("Loot Settings")
                .comment("配置格式：物品ID,权重,最小数量,最大数量;多个物品用分号分隔");

        // ================= 主世界遗迹 =================
        DESERT_PYRAMID_LOOT = defineLoot("desertPyramid",
                "minecraft:chests/desert_pyramid",
                "沙漠神殿战利品",
                "bountifulbaubles:potion_wormhole,6,1,2;bountifulbaubles:potion_recall,7,1,3;bountifulbaubles:magic_mirror,2,1,2;bountifulbaubles:broken_black_dragon_scale,2,1,2;bountifulbaubles:spectral_silt,8,1,7;bountifulbaubles:broken_heart,1,1,2;bountifulbaubles:lucky_horseshoe,1,1,2;bountifulbaubles:bottled_cloud,1,1,2;bountifulbaubles:endless_pearl,1,1,2;bountifulbaubles:treasure_bag,2,1,2");

        JUNGLE_TEMPLE_LOOT = defineLoot("jungleTemple",
                "minecraft:chests/jungle_temple",
                "丛林神庙战利品",
                "bountifulbaubles:potion_wormhole,6,1,2;bountifulbaubles:potion_recall,7,1,2;bountifulbaubles:magic_mirror,2,1,2;bountifulbaubles:broken_black_dragon_scale,2,1,2;bountifulbaubles:spectral_silt,8,1,7;bountifulbaubles:curious_amulet,3,1,2;bountifulbaubles:curious_ring,3,1,2;bountifulbaubles:vampiric_glove,1,1,2;bountifulbaubles:gauntlets_dexterity,2,1,2;bountifulbaubles:treasure_bag,2,1,2");

        ABANDONED_MINESHAFT_LOOT = defineLoot("abandonedMineshaft",
                "minecraft:chests/abandoned_mineshaft",
                "废弃矿井战利品",
                "bountifulbaubles:potion_wormhole,6,1,2;bountifulbaubles:potion_recall,7,1,2;bountifulbaubles:magic_mirror,2,1,2;bountifulbaubles:broken_black_dragon_scale,2,1,2;bountifulbaubles:spectral_silt,8,1,7;bountifulbaubles:sunglasses,3,1,2;bountifulbaubles:curious_crown,3,1,2;bountifulbaubles:auto_torch,3,1,2;bountifulbaubles:treasure_bag,2,1,2");

        STRONGHOLD_LIBRARY_LOOT = defineLoot("strongholdLibrary",
                "minecraft:chests/stronghold_library",
                "要塞图书馆战利品",
                "bountifulbaubles:potion_wormhole,6,1,2;bountifulbaubles:potion_recall,7,1,2;bountifulbaubles:magic_mirror,2,1,2;bountifulbaubles:broken_black_dragon_scale,2,1,2;bountifulbaubles:spectral_silt,8,1,7;bountifulbaubles:treasure_bag,2,1,2");

        WOODLAND_MANSION_LOOT = defineLoot("woodlandMansion",
                "minecraft:chests/woodland_mansion",
                "林地府邸战利品",
                "bountifulbaubles:potion_wormhole,6,1,2;bountifulbaubles:potion_recall,7,1,2;bountifulbaubles:magic_mirror,2,1,2;bountifulbaubles:broken_black_dragon_scale,2,1,2;bountifulbaubles:spectral_silt,8,1,7;bountifulbaubles:treasure_bag,2,1,2");

        VILLAGE_TEMPLE_LOOT = defineLoot("villageTemple",
                "minecraft:chests/village/village_temple",
                "村庄教堂战利品",
                "bountifulbaubles:potion_wormhole,6,1,2;bountifulbaubles:potion_recall,7,1,2;bountifulbaubles:magic_mirror,2,1,2;bountifulbaubles:broken_black_dragon_scale,2,1,2;bountifulbaubles:spectral_silt,8,1,7;bountifulbaubles:treasure_bag,2,1,2");

        SHIPWRECK_TREASURE_LOOT = defineLoot("shipwreckTreasure",
                "minecraft:chests/shipwreck_treasure",
                "沉船宝箱战利品",
                "bountifulbaubles:potion_wormhole,6,1,2;bountifulbaubles:potion_recall,7,1,2;bountifulbaubles:magic_mirror,2,1,2;bountifulbaubles:broken_black_dragon_scale,2,1,2;bountifulbaubles:spectral_silt,8,1,7;bountifulbaubles:treasure_bag,2,1,2");

        PILLAGER_OUTPOST_LOOT = defineLoot("pillagerOutpost",
                "minecraft:chests/pillager_outpost",
                "掠夺者前哨站战利品",
                "bountifulbaubles:potion_wormhole,6,1,2;bountifulbaubles:potion_recall,7,1,2;bountifulbaubles:magic_mirror,2,1,2;bountifulbaubles:broken_black_dragon_scale,2,1,2;bountifulbaubles:spectral_silt,8,1,7;bountifulbaubles:black_dragon_scale,3,1,2;bountifulbaubles:phylactery_charm,1,1,2;bountifulbaubles:treasure_bag,2,1,2");

        BURIED_TREASURE_LOOT = defineLoot("buriedTreasure",
                "minecraft:chests/buried_treasure",
                "埋藏的宝藏战利品",
                "bountifulbaubles:potion_wormhole,6,1,2;bountifulbaubles:potion_recall,6,1,2;bountifulbaubles:wormhole_mirror,1,1,2;bountifulbaubles:broken_black_dragon_scale,2,1,2;bountifulbaubles:spectral_silt,8,1,7;bountifulbaubles:treasure_bag,2,1,2");

        UNDERWATER_RUIN_BIG_LOOT = defineLoot("underwaterRuinBig",
                "minecraft:chests/underwater_ruin_big",
                "大型海底废墟战利品",
                "bountifulbaubles:potion_wormhole,6,1,2;bountifulbaubles:potion_recall,3,1,2;bountifulbaubles:magic_mirror,2,1,2;bountifulbaubles:broken_black_dragon_scale,2,1,2;bountifulbaubles:spectral_silt,8,1,7;bountifulbaubles:treasure_bag,2,1,2");

        UNDERWATER_RUIN_SMALL_LOOT = defineLoot("underwaterRuinSmall",
                "minecraft:chests/underwater_ruin_small",
                "小型海底废墟战利品",
                "bountifulbaubles:potion_wormhole,6,1,2;bountifulbaubles:potion_recall,7,1,2;bountifulbaubles:magic_mirror,2,1,2;bountifulbaubles:broken_black_dragon_scale,2,1,2;bountifulbaubles:spectral_silt,8,1,7;bountifulbaubles:treasure_bag,2,1,2");

        IGLOO_CHEST_LOOT = defineLoot("iglooChest",
                "minecraft:chests/igloo_chest",
                "雪屋地下室战利品",
                "bountifulbaubles:potion_wormhole,6,1,2;bountifulbaubles:potion_recall,7,1,2;bountifulbaubles:magic_mirror,2,1,2;bountifulbaubles:broken_black_dragon_scale,2,1,2;bountifulbaubles:spectral_silt,8,1,7;bountifulbaubles:treasure_bag,2,1,2");

        SIMPLE_DUNGEON_LOOT = defineLoot("simpleDungeon",
                "minecraft:chests/simple_dungeon",
                "小型地牢战利品",
                "bountifulbaubles:potion_wormhole,6,1,2;bountifulbaubles:potion_recall,7,1,2;bountifulbaubles:magic_mirror,2,1,2;bountifulbaubles:broken_black_dragon_scale,2,1,2;bountifulbaubles:spectral_silt,8,1,7;bountifulbaubles:balloon,3,1,1;bountifulbaubles:cobalt_shield,3,1,1;bountifulbaubles:auto_torch,3,1,2;bountifulbaubles:treasure_bag,2,1,2");

        STRONGHOLD_CORRIDOR_LOOT = defineLoot("strongholdCorridor",
                "minecraft:chests/stronghold_corridor",
                "要塞走廊战利品",
                "bountifulbaubles:potion_wormhole,6,1,2;bountifulbaubles:potion_recall,7,1,2;bountifulbaubles:magic_mirror,2,1,2;bountifulbaubles:broken_black_dragon_scale,2,1,2;bountifulbaubles:spectral_silt,8,1,7;bountifulbaubles:treasure_bag,2,1,2");


        FOSSIL_DINOSAUR_LOOT = defineLoot("fossilDinosaur",
                "minecraft:chests/fossil_dinosaur",
                "恐龙化石战利品",
                "");


        FOSSIL_MAMMAL_LOOT = defineLoot("fossilMammal",
                "minecraft:chests/fossil_mammal",
                "哺乳动物化石战利品",
                "");


        SHIPWRECK_SUPPLY_LOOT = defineLoot("shipwreckSupply",
                "minecraft:chests/shipwreck_supply",
                "沉船补给箱战利品",
                "bountifulbaubles:potion_wormhole,3,1,2;bountifulbaubles:potion_recall,3,1,2;bountifulbaubles:magic_mirror,2,1,2;bountifulbaubles:broken_black_dragon_scale,2,1,2;bountifulbaubles:spectral_silt,8,1,7;bountifulbaubles:treasure_bag,2,1,2");


        WOODLAND_CARTOGRAPHY_LOOT = defineLoot("woodlandCartography",
                "minecraft:chests/woodland_mansion_cartography",
                "林地府邸制图室战利品",
                "bountifulbaubles:potion_wormhole,3,1,2;bountifulbaubles:potion_recall,3,1,2;bountifulbaubles:magic_mirror,2,1,2;bountifulbaubles:broken_black_dragon_scale,2,1,2;bountifulbaubles:spectral_silt,8,1,7;bountifulbaubles:treasure_bag,2,1,2");

        STRONGHOLD_CROSSING_LOOT = defineLoot("strongholdCrossing",
                "minecraft:chests/stronghold_crossing",
                "要塞十字路口战利品",
                "bountifulbaubles:potion_wormhole,3,1,2;bountifulbaubles:potion_recall,3,1,2;bountifulbaubles:magic_mirror,2,1,2;bountifulbaubles:broken_black_dragon_scale,2,1,2;bountifulbaubles:spectral_silt,8,1,7;bountifulbaubles:treasure_bag,2,1,2");

        SHIPWRECK_MAP_LOOT = defineLoot("shipwreckMap",
                "minecraft:chests/shipwreck_map",
                "沉船地图箱战利品",
                "bountifulbaubles:potion_wormhole,3,1,2;bountifulbaubles:potion_recall,3,1,2;bountifulbaubles:magic_mirror,2,1,2;bountifulbaubles:broken_black_dragon_scale,2,1,2;bountifulbaubles:spectral_silt,8,1,7;bountifulbaubles:treasure_bag,2,1,2");


        // ================= 下界遗迹 =================
        BASTION_TREASURE_LOOT = defineLoot("bastionTreasure",
                "minecraft:chests/bastion_treasure",
                "堡垒遗迹宝藏室战利品",
                "bountifulbaubles:potion_wormhole,6,1,2;bountifulbaubles:potion_recall,7,1,2;bountifulbaubles:magic_mirror,2,1,2;bountifulbaubles:broken_black_dragon_scale,2,1,2;bountifulbaubles:spectral_silt,8,1,7;bountifulbaubles:amulet_sin_empty,8,1,7;bountifulbaubles:curious_knuckles,2,1,1;bountifulbaubles:treasure_bag,2,1,2");

        BASTION_BRIDGE_LOOT = defineLoot("bastionBridge",
                "minecraft:chests/bastion_bridge",
                "堡垒遗迹桥梁战利品",
                "bountifulbaubles:potion_wormhole,6,1,2;bountifulbaubles:potion_recall,7,1,2;bountifulbaubles:magic_mirror,2,1,2;bountifulbaubles:broken_black_dragon_scale,2,1,2;bountifulbaubles:spectral_silt,8,1,7;bountifulbaubles:amulet_sin_empty,5,1,3;bountifulbaubles:curious_knuckles,2,1,1;bountifulbaubles:treasure_bag,2,1,2");

        BASTION_HOUSING_LOOT = defineLoot("bastionHousing",
                "minecraft:chests/bastion_housing",
                "堡垒遗迹居住区战利品",
                "bountifulbaubles:potion_wormhole,6,1,2;bountifulbaubles:potion_recall,7,1,2;bountifulbaubles:magic_mirror,2,1,2;bountifulbaubles:broken_black_dragon_scale,2,1,2;bountifulbaubles:spectral_silt,8,1,7;bountifulbaubles:amulet_sin_empty,5,1,3;bountifulbaubles:curious_knuckles,2,1,1;bountifulbaubles:treasure_bag,2,1,2");

        BASTION_OTHER_LOOT = defineLoot("bastionOther",
                "minecraft:chests/bastion_other",
                "堡垒遗迹杂项战利品",
                "bountifulbaubles:potion_wormhole,6,1,2;bountifulbaubles:potion_recall,7,1,2;bountifulbaubles:magic_mirror,2,1,2;bountifulbaubles:broken_black_dragon_scale,2,1,2;bountifulbaubles:spectral_silt,8,1,7;bountifulbaubles:amulet_sin_empty,5,1,3;bountifulbaubles:curious_knuckles,2,1,1;bountifulbaubles:treasure_bag,2,1,2");

        RUINED_PORTAL_LOOT = defineLoot("ruinedPortal",
                "minecraft:chests/ruined_portal",
                "废弃传送门战利品",
                "bountifulbaubles:potion_wormhole,6,1,2;bountifulbaubles:potion_recall,7,1,2;bountifulbaubles:magic_mirror,2,1,2;bountifulbaubles:broken_black_dragon_scale,2,1,2;bountifulbaubles:spectral_silt,8,1,7;bountifulbaubles:obsidian_skull,3,1,2;bountifulbaubles:auto_torch,3,1,2;bountifulbaubles:treasure_bag,2,1,2");

        // ================= 末地遗迹 =================
        END_CITY_LOOT = defineLoot("endCity",
                "minecraft:chests/end_city_treasure",
                "末地城战利品",
                "bountifulbaubles:wrath_pendant,5,1,3;bountifulbaubles:gluttony_pendant,3,1,2;bountifulbaubles:pride_pendant,3,1,2;bountifulbaubles:treasure_bag,2,1,2");

        // ================= 其他结构 =================
        ANCIENT_CITY_LOOT = defineLoot("ancientCity",
                "minecraft:chests/ancient_city",
                "远古城市战利品",
                "bountifulbaubles:wrath_pendant,5,1,3;bountifulbaubles:gluttony_pendant,3,1,2;bountifulbaubles:pride_pendant,3,1,2;bountifulbaubles:cross_necklace,3,1,2;bountifulbaubles:treasure_bag,2,1,2");


        // === 职业宝箱 ===
        VILLAGE_ARMORER_LOOT = defineLoot("villageArmorer",
                "minecraft:chests/village/village_armorer",
                "盔甲匠小屋 (盔甲匠)",
                "");

        VILLAGE_BUTCHER_LOOT = defineLoot("villageButcher",
                "minecraft:chests/village/village_butcher",
                "屠夫商店 (屠夫)",
                "");

        VILLAGE_CARTOGRAPHER_LOOT = defineLoot("villageCartographer",
                "minecraft:chests/village/village_cartographer",
                "制图师小屋 (制图师)",
                "");

        VILLAGE_PLAINS_HOUSE_LOOT = defineLoot("villagePlainsHouse",
                "minecraft:chests/village/village_plains_house",
                "平原农舍 (农民)",
                "");

        VILLAGE_FISHER_LOOT = defineLoot("villageFisher",
                "minecraft:chests/village/village_fisher",
                "渔夫小屋 (渔夫)",
                "");

        VILLAGE_FLETCHER_LOOT = defineLoot("villageFletcher",
                "minecraft:chests/village/village_fletcher",
                "制箭师小屋 (制箭师)",
                "");

        VILLAGE_TANNERY_LOOT = defineLoot("villageTannery",
                "minecraft:chests/village/village_tannery",
                "制革厂 (皮匠)",
                "");

        VILLAGE_LIBRARY_LOOT = defineLoot("villageLibrary",
                "minecraft:chests/village/village_library",
                "图书馆 (图书管理员)",
                "");

        VILLAGE_MASON_LOOT = defineLoot("villageMason",
                "minecraft:chests/village/village_mason",
                "石匠小屋 (石匠)",
                "");

        VILLAGE_SHEPHERD_LOOT = defineLoot("villageShepherd",
                "minecraft:chests/village/village_shepherd",
                "牧羊人小屋 (牧羊人)",
                "");

        VILLAGE_TOOLSMITH_LOOT = defineLoot("villageToolsmith",
                "minecraft:chests/village/village_toolsmith",
                "工具匠小屋 (工具匠)",
                "");

        VILLAGE_WEAPONSMITH_LOOT = defineLoot("villageWeaponsmith",
                "minecraft:chests/village/village_weaponsmith",
                "武器匠小屋 (武器匠)",
                "");

        // === 特殊房屋 ===
        VILLAGE_DESERT_HOUSE_LOOT = defineLoot("villageDesertHouse",
                "minecraft:chests/village/village_desert_house",
                "沙漠村庄房屋 (失业村民)",
                "");

        VILLAGE_SNOWY_HOUSE_LOOT = defineLoot("villageSnowyHouse",
                "minecraft:chests/village/village_snowy_house",
                "雪原村庄房屋",
                "");

        VILLAGE_SAVANNA_HOUSE_LOOT = defineLoot("villageSavannaHouse",
                "minecraft:chests/village/village_savanna_house",
                "热带草原村庄房屋",
                "");

        VILLAGE_TAIGA_HOUSE_LOOT = defineLoot("villageTaigaHouse",
                "minecraft:chests/village/village_taiga_house",
                "针叶林村庄房屋",
                "");

        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    // 辅助方法：统一创建配置项
    private static ForgeConfigSpec.ConfigValue<String> defineLoot(
            String configKey,
            String structureId,
            String comment,
            String defaultValue
    ) {
        return BUILDER
                .comment(
                        comment + "\n" +
                                "结构ID: " + structureId
                )
                .define(configKey + "Loot", defaultValue);
    }

    // ===================== 战利品加载逻辑 =====================
    private static final Map<ResourceLocation, ForgeConfigSpec.ConfigValue<String>> LOOT_MAPPING = new HashMap<>();

    static {
        // 初始化映射表
        LOOT_MAPPING.put(new ResourceLocation("minecraft:chests/desert_pyramid"), DESERT_PYRAMID_LOOT);
        LOOT_MAPPING.put(new ResourceLocation("minecraft:chests/jungle_temple"), JUNGLE_TEMPLE_LOOT);
        LOOT_MAPPING.put(new ResourceLocation("minecraft:chests/abandoned_mineshaft"), ABANDONED_MINESHAFT_LOOT);
        LOOT_MAPPING.put(new ResourceLocation("minecraft:chests/stronghold_library"), STRONGHOLD_LIBRARY_LOOT);
        LOOT_MAPPING.put(new ResourceLocation("minecraft:chests/woodland_mansion"), WOODLAND_MANSION_LOOT);
        LOOT_MAPPING.put(new ResourceLocation("minecraft:chests/village/village_temple"), VILLAGE_TEMPLE_LOOT);
        LOOT_MAPPING.put(new ResourceLocation("minecraft:chests/shipwreck_treasure"), SHIPWRECK_TREASURE_LOOT);
        LOOT_MAPPING.put(new ResourceLocation("minecraft:chests/pillager_outpost"), PILLAGER_OUTPOST_LOOT);
        LOOT_MAPPING.put(new ResourceLocation("minecraft:chests/buried_treasure"), BURIED_TREASURE_LOOT);
        LOOT_MAPPING.put(new ResourceLocation("minecraft:chests/underwater_ruin_big"), UNDERWATER_RUIN_BIG_LOOT);
        LOOT_MAPPING.put(new ResourceLocation("minecraft:chests/underwater_ruin_small"), UNDERWATER_RUIN_SMALL_LOOT);
        LOOT_MAPPING.put(new ResourceLocation("minecraft:chests/igloo_chest"), IGLOO_CHEST_LOOT);
        LOOT_MAPPING.put(new ResourceLocation("minecraft:chests/bastion_treasure"), BASTION_TREASURE_LOOT);
        LOOT_MAPPING.put(new ResourceLocation("minecraft:chests/bastion_bridge"), BASTION_BRIDGE_LOOT);
        LOOT_MAPPING.put(new ResourceLocation("minecraft:chests/bastion_housing"), BASTION_HOUSING_LOOT);
        LOOT_MAPPING.put(new ResourceLocation("minecraft:chests/bastion_other"), BASTION_OTHER_LOOT);
        LOOT_MAPPING.put(new ResourceLocation("minecraft:chests/ruined_portal"), RUINED_PORTAL_LOOT);
        LOOT_MAPPING.put(new ResourceLocation("minecraft:chests/end_city_treasure"), END_CITY_LOOT);
        LOOT_MAPPING.put(new ResourceLocation("minecraft:chests/ancient_city"), ANCIENT_CITY_LOOT);
        LOOT_MAPPING.put(new ResourceLocation("minecraft:chests/simple_dungeon"), SIMPLE_DUNGEON_LOOT);
        LOOT_MAPPING.put(new ResourceLocation("minecraft:chests/stronghold_corridor"), STRONGHOLD_CORRIDOR_LOOT);
        LOOT_MAPPING.put(new ResourceLocation("minecraft:chests/fossil_dinosaur"), FOSSIL_DINOSAUR_LOOT);
        LOOT_MAPPING.put(new ResourceLocation("minecraft:chests/fossil_mammal"), FOSSIL_MAMMAL_LOOT);
        LOOT_MAPPING.put(new ResourceLocation("minecraft:chests/shipwreck_supply"), SHIPWRECK_SUPPLY_LOOT);
        LOOT_MAPPING.put(new ResourceLocation("minecraft:chests/woodland_mansion_cartography"), WOODLAND_CARTOGRAPHY_LOOT);
        LOOT_MAPPING.put(new ResourceLocation("minecraft:chests/stronghold_crossing"), STRONGHOLD_CROSSING_LOOT);
        LOOT_MAPPING.put(new ResourceLocation("minecraft:chests/shipwreck_map"), SHIPWRECK_MAP_LOOT);
        // 村庄职业
        LOOT_MAPPING.put(new ResourceLocation("minecraft:chests/village/village_armorer"), VILLAGE_ARMORER_LOOT);
        LOOT_MAPPING.put(new ResourceLocation("minecraft:chests/village/village_butcher"), VILLAGE_BUTCHER_LOOT);
        LOOT_MAPPING.put(new ResourceLocation("minecraft:chests/village/village_cartographer"), VILLAGE_CARTOGRAPHER_LOOT);
        LOOT_MAPPING.put(new ResourceLocation("minecraft:chests/village/village_plains_house"), VILLAGE_PLAINS_HOUSE_LOOT);
        LOOT_MAPPING.put(new ResourceLocation("minecraft:chests/village/village_fisher"), VILLAGE_FISHER_LOOT);
        LOOT_MAPPING.put(new ResourceLocation("minecraft:chests/village/village_fletcher"), VILLAGE_FLETCHER_LOOT);
        LOOT_MAPPING.put(new ResourceLocation("minecraft:chests/village/village_tannery"), VILLAGE_TANNERY_LOOT);
        LOOT_MAPPING.put(new ResourceLocation("minecraft:chests/village/village_library"), VILLAGE_LIBRARY_LOOT);
        LOOT_MAPPING.put(new ResourceLocation("minecraft:chests/village/village_mason"), VILLAGE_MASON_LOOT);
        LOOT_MAPPING.put(new ResourceLocation("minecraft:chests/village/village_shepherd"), VILLAGE_SHEPHERD_LOOT);
        LOOT_MAPPING.put(new ResourceLocation("minecraft:chests/village/village_toolsmith"), VILLAGE_TOOLSMITH_LOOT);
        LOOT_MAPPING.put(new ResourceLocation("minecraft:chests/village/village_weaponsmith"), VILLAGE_WEAPONSMITH_LOOT);

        // 村庄房屋
        LOOT_MAPPING.put(new ResourceLocation("minecraft:chests/village/village_desert_house"), VILLAGE_DESERT_HOUSE_LOOT);
        LOOT_MAPPING.put(new ResourceLocation("minecraft:chests/village/village_snowy_house"), VILLAGE_SNOWY_HOUSE_LOOT);
        LOOT_MAPPING.put(new ResourceLocation("minecraft:chests/village/village_savanna_house"), VILLAGE_SAVANNA_HOUSE_LOOT);
        LOOT_MAPPING.put(new ResourceLocation("minecraft:chests/village/village_taiga_house"), VILLAGE_TAIGA_HOUSE_LOOT);


    }

    public static float getHuskAppleChance() {
        return HUSK_APPLE_CHANCE.get().floatValue();
    }

    public static float getStrayRingChance() {
        return STRAY_RING_CHANCE.get().floatValue();}

    public static float getElderGuardianVitaminChance() {
        return ELDER_GUARDIAN_VITAMIN_CHANCE.get().floatValue();
    }

    public static float getDragonScaleChance() {
        return DRAGON_SCALE_CHANCE.get().floatValue();
    }

    public static int getDragonScaleMin() {
        return DRAGON_SCALE_MIN.get();
    }

    public static int getDragonScaleMax() {
        return DRAGON_SCALE_MAX.get();
    }

    public static float getShulkerHeartChance() {
        return SHULKER_HEART_CHANCE.get().floatValue();
    }

    public static float getCaveSpiderBezoarChance() {
        return CAVE_SPIDER_BEZOAR_CHANCE.get().floatValue();
    }

    public static float getEvokerTotemChance() {
        return EVOKER_TOTEM_CHANCE.get().floatValue();
    }

    public static void loadLootConfig() {
        lootConfig.clear();
        LOOT_MAPPING.forEach((tableId, config) -> {
            String value = config.get();
            if (value.isEmpty()) return;

            List<LootEntry> entries = parseLootEntries(value);
            if (!entries.isEmpty()) {
                lootConfig.put(tableId, entries);
            }
        });
    }

    public static Map<ResourceLocation, List<LootEntry>> lootConfig = new HashMap<>();

    // === LootEntry 内部类 ===
    public static class LootEntry {
        public final ResourceLocation itemId;
        public final int weight;
        public final int minRolls;
        public final int maxRolls;

        public LootEntry(ResourceLocation itemId, int weight, int minRolls, int maxRolls) {
            this.itemId = itemId;
            this.weight = weight;
            this.minRolls = minRolls;
            this.maxRolls = maxRolls;
        }
    }

    private static List<LootEntry> parseLootEntries(String configValue) {
        List<LootEntry> entries = new ArrayList<>();
        for (String entry : configValue.split(";")) {
            String[] parts = entry.split(",");
            if (parts.length != 4) continue;

            try {
                entries.add(new LootEntry(
                        new ResourceLocation(parts[0]),
                        Integer.parseInt(parts[1]),
                        Integer.parseInt(parts[2]),
                        Integer.parseInt(parts[3])
                ));
            } catch (Exception e) {
                BountifulBaublesMod.LOGGER.error("Invalid loot entry: {}", entry);
            }
        }
        return entries;
    }

    public static double getCrossNecklaceBoost() {
        return CROSS_NECKLACE_INVULNERABILITY.get();
    }

    public static boolean crossNecklacePlayersOnly() {
        return CROSS_NECKLACE_PLAYERS_ONLY.get();
    }

    // ===================== 原有获取方法保持不变 =====================
    public static int getRecastExpCost() {
        return ANVIL_RECAST_EXP_COST.get();
    }

    public static int getRecastMaterialCost() {
        return ANVIL_RECAST_MATERIAL_COST.get();
    }

    public static double getSprintVertical() {
        return SPRINT_JUMP_VERTICAL.get();
    }

    public static double getSprintHorizontal() {
        return SPRINT_JUMP_HORIZONTAL.get();
    }

    public static int getCooldownTicks() {
        return COOLDOWN_TICKS.get();
    }

    public static boolean isCuriosEnabled() {
        return ENABLE_CURIOS.get();
    }

    public static double getVampiricPercent() {
        return VAMPIRIC_PERCENT.get();
    }

    public static boolean isModifierEnabled() {
        return MODIFIER_ENABLED.get();
    }

    public static double getVampiricMax() {
        return VAMPIRIC_MAX.get();
    }

    public static double getGauntletsAttackSpeed() {
        return GAUNTLETS_ATTACK_SPEED.get();
    }

    public static int getTacticalScanInterval() {
        return TACTICAL_SCAN_INTERVAL.get();
    }

    public static int getTacticalMarkDuration() {
        return TACTICAL_MARK_DURATION.get();
    }

    public static double getTacticalScanRadius() {
        return TACTICAL_SCAN_RADIUS.get();
    }

    public static double getTacticalUpRange() {
        return TACTICAL_UP_RANGE.get();
    }

    public static double getTacticalDownRange() {
        return TACTICAL_DOWN_RANGE.get();
    }

    public static double getTacticalDamageMultiplier() {
        return TACTICAL_DAMAGE_MULTIPLIER.get();
    }
    public static List<Double> getLuckCoinDamageBonuses() {
        return new ArrayList<>(LUCK_COIN_DAMAGE_BONUSES.get());
    }

    public static List<Double> getLuckCoinProbabilities() {
        return new ArrayList<>(LUCK_COIN_PROBABILITIES.get());
    }
    public static double getThaWizardDurationMultiplier() {
        return THA_WIZARD_DURATION_MULTIPLIER.get();
    }

    public static double getDropSpindleRepairChance() {
        return DROP_SPINDLE_REPAIR_CHANCE.get();
    }

    public static int getDropSpindleRepairAmount() {
        return DROP_SPINDLE_REPAIR_AMOUNT.get();
    }
    public static int getDarkEggCooldown() {
        return DARK_EGG_COOLDOWN.get();
    }

    public static int getDarkEggVexCount() {
        return DARK_EGG_VEX_COUNT.get();
    }

    public static int getDarkEggVexLifetime() {
        return DARK_EGG_VEX_LIFETIME.get();
    }
    public static int getMossyRingRepairInterval() {
        return MOSSY_RING_REPAIR_INTERVAL.get();
    }
    public static int getMossyRingRepairAmount() {
        return MOSSY_RING_REPAIR_AMOUNT.get();
    }
    public static int getMossyBeltRepairInterval() {
        return MOSSY_BELT_REPAIR_INTERVAL.get();
    }

    public static int getMossyBeltRepairAmount() {
        return MOSSY_BELT_REPAIR_AMOUNT.get();
    }
    public static int getMadAuraCooldown() {
        return MAD_AURA_COOLDOWN.get();
    }
    public static double getDarkDaggerExecuteThreshold() {
        return DARK_DAGGER_EXECUTE_THRESHOLD.get();
    }

    public static double getDarkDaggerExecuteDamage() {
        return DARK_DAGGER_EXECUTE_DAMAGE.get();
    }
    public static int getBlazeHeartDuration() {
        return BLAZE_HEART_DURATION.get();
    }
    public static int getWrathPendantBuffLevel() {
        return WRATH_PENDANT_BUFF_LEVEL.get();
    }

    public static int getWrathPendantBuffDuration() {
        return WRATH_PENDANT_BUFF_DURATION.get();
    }
}
