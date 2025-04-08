package com.jinqinxixi.bountifulbaubles.loot;

import com.jinqinxixi.bountifulbaubles.config.ModConfig;
import com.jinqinxixi.bountifulbaubles.item.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Mod.EventBusSubscriber(modid = "bountifulbaubles", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LootHandler {
    private static final Logger log = LoggerFactory.getLogger(LootHandler.class);

    // ===================== 战利品表=====================
    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        ResourceLocation tableId = event.getName();
        List<ModConfig.LootEntry> entries = ModConfig.lootConfig.get(tableId);

        if (entries != null && !entries.isEmpty()) {
            LootPool.Builder poolBuilder = LootPool.lootPool()
                    .name("bountifulbaubles_config_loot");

            for (ModConfig.LootEntry entry : entries) {
                Item item = ForgeRegistries.ITEMS.getValue(entry.itemId);
                if (item != null) {
                    poolBuilder.add(LootItem.lootTableItem(item)
                                    .setWeight(entry.weight))
                            .setRolls(UniformGenerator.between(entry.minRolls, entry.maxRolls));
                } else {
                    log.error("Invalid item in config: {}", entry.itemId);
                }
            }

            event.getTable().addPool(poolBuilder.build());
            log.debug("Added custom loot to {}", tableId);
        }
    }

    // ===================== 实体死亡掉落处理=====================
    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof Player)) return;

        LivingEntity entity = event.getEntity();
        Level world = entity.level();
        if (world.isClientSide()) return;

        RandomSource rand = entity.getRandom();
        handleMobDrops(entity, rand, world);
    }

    private static void handleMobDrops(LivingEntity entity, RandomSource rand, Level world) {
        if (entity instanceof Husk) {
            tryDropItem(entity, rand, world,
                    ModConfig.getHuskAppleChance(), // 使用配置值
                    ModItems.APPLE.get());
        } else if (entity instanceof ElderGuardian) {
            tryDropItem(entity, rand, world,
                    ModConfig.getElderGuardianVitaminChance(), // 使用配置值
                    ModItems.VITAMINS.get());
        } else if (entity instanceof Stray) {
            tryDropItem(entity, rand, world,
                    ModConfig.getStrayRingChance(), // 使用配置值
                    ModItems.RING_OVERCLOCKING.get());
        } else if (entity instanceof Shulker) {
            tryDropItem(entity, rand, world,
                    ModConfig.getShulkerHeartChance(), // 使用配置值
                    ModItems.SHULKER_HEART.get());
        } else if (entity instanceof CaveSpider) {
            tryDropItem(entity, rand, world,
                    ModConfig.getCaveSpiderBezoarChance(), // 使用配置值
                    ModItems.BEZOAR.get());
        } else if (entity instanceof EnderDragon) {
            handleDragonDrops(entity, rand, world);
        } else if (entity instanceof Evoker) {
            tryDropItem(entity, rand, world,
                    ModConfig.getEvokerTotemChance(), // 使用配置值
                    ModItems.INFINITE_TOTEM.get());
        }
    }

    private static void tryDropItem(LivingEntity entity, RandomSource rand, Level world, float chance, Item item) {
        if (rand.nextFloat() < chance) {
            spawnItem(entity, item, 1, world);
        }
    }

    private static void spawnItem(LivingEntity entity, Item item, int count, Level world) {
        ItemStack stack = new ItemStack(item, count);
        world.addFreshEntity(new ItemEntity(world, entity.getX(), entity.getY(), entity.getZ(), stack));
    }

    private static void handleDragonDrops(LivingEntity dragon, RandomSource rand, Level world) {
        if (rand.nextFloat() < ModConfig.getDragonScaleChance()) {
            int count = ModConfig.getDragonScaleMin() + rand.nextInt(ModConfig.getDragonScaleMax() - ModConfig.getDragonScaleMin() + 1);
            for (int i = 0; i < count; i++) {
                ItemEntity item = new ItemEntity(world,
                        dragon.getX(), dragon.getY(), dragon.getZ(),
                        new ItemStack(ModItems.ENDER_DRAGON_SCALE.get()));

                item.setDeltaMovement(
                        (rand.nextDouble() - 0.5) * 0.5,
                        rand.nextDouble() * 0.5 + 0.3,
                        (rand.nextDouble() - 0.5) * 0.5
                );
                world.addFreshEntity(item);
            }
        }
    }

    // ===================== 权重池支持（保留备用）=====================
    private static void trySpawnFromPool(LivingEntity entity, RandomSource rand, Level world, float chance, List<ItemEntry> pool) {
        if (rand.nextFloat() < chance) {
            Item item = selectFromPool(pool, rand);
            if (item != null) {
                spawnItem(entity, item, 1, world);
            }
        }
    }

    private static Item selectFromPool(List<ItemEntry> pool, RandomSource rand) {
        int totalWeight = pool.stream().mapToInt(e -> e.weight).sum();
        int randomValue = rand.nextInt(totalWeight);
        int cumulative = 0;

        for (ItemEntry entry : pool) {
            cumulative += entry.weight;
            if (randomValue < cumulative) {
                return entry.item;
            }
        }
        return null;
    }

    private static class ItemEntry {
        final Item item;
        final int weight;

        ItemEntry(Item item, int weight) {
            this.item = item;
            this.weight = weight;
        }
    }
}