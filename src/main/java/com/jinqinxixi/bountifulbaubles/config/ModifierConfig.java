package com.jinqinxixi.bountifulbaubles.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jinqinxixi.bountifulbaubles.BountifulBaublesMod;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ModifierConfig {
//    private static final String CONFIG_FILE = "config/bountifulbaubles-modifiers.json";
//    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
//
//    private Map<String, ReforgeConfig> modifiableItems;
//
//    public static class ReforgeConfig {
//        public String requiredItem; // 重铸所需物品的注册名
//        public int experienceCost;  // 所需经验等级
//        public int materialCost;    // 所需材料数量
//
//        public ReforgeConfig(String requiredItem, int experienceCost, int materialCost) {
//            this.requiredItem = requiredItem;
//            this.experienceCost = experienceCost;
//            this.materialCost = materialCost;
//        }
//    }
//
//    public ModifierConfig() {
//        this.modifiableItems = new HashMap<>();
//    }
//
//    public static ModifierConfig load() {
//        File configFile = new File(CONFIG_FILE);
//        if (!configFile.exists()) {
//            ModifierConfig defaultConfig = createDefaultConfig();
//            defaultConfig.save();
//            return defaultConfig;
//        }
//
//        try (Reader reader = new FileReader(configFile)) {
//            return GSON.fromJson(reader, ModifierConfig.class);
//        } catch (IOException e) {
//            BountifulBaublesMod.LOGGER.error("Failed to load modifier config: {}", e.getMessage());
//            return createDefaultConfig();
//        }
//    }
//
//    private static ModifierConfig createDefaultConfig() {
//        ModifierConfig config = new ModifierConfig();
//        // 添加默认配置
//        config.modifiableItems.put(
//                "artifacts:feral_claws",
//                new ReforgeConfig("bountifulbaubles:resplendent_token", 5, 1)
//        );
//
//        // 添加 cross_necklace 配置
//        config.modifiableItems.put(
//                "artifacts:cross_necklace",
//                new ReforgeConfig("bountifulbaubles:resplendent_token", 5, 1)
//        );
//
//        return config;
//    }
//
//    public void save() {
//        try {
//            File configFile = new File(CONFIG_FILE);
//            configFile.getParentFile().mkdirs();
//
//            try (Writer writer = new FileWriter(configFile)) {
//                GSON.toJson(this, writer);
//            }
//        } catch (IOException e) {
//            BountifulBaublesMod.LOGGER.error("Failed to save modifier config: {}", e.getMessage());
//        }
//    }
//
//    public boolean isItemModifiable(String itemId) {
//        return modifiableItems.containsKey(itemId);
//    }
//
//    public ReforgeConfig getReforgeConfig(String itemId) {
//        return modifiableItems.get(itemId);
//    }
}