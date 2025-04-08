//package com.jinqinxixi.bountifulbaubles.Config;
//
//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.item.Item;
//import net.minecraft.core.registries.BuiltInRegistries;
//import net.minecraftforge.fml.loading.FMLPaths;
//
//import java.io.*;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.util.HashMap;
//import java.util.Map;
//
//public class ItemConfig {
//    private static final String CONFIG_FILE = "bountifulbaubles-items.json";
//    private static Map<ResourceLocation, String> modifiableItems = new HashMap<>();
//    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
//
//    // 添加铁砧配置字段
//    private static int anvilXpCost = 5;
//    private static int anvilMaterialCost = 1;
//    private static ResourceLocation anvilMaterialItem = new ResourceLocation("bountifulbaubles", "resplendent_token");
//
//    // 配置数据类
//    private static class ConfigData {
//        Map<String, String> items = new HashMap<>();
//        int anvilXpCost = 5;
//        int anvilMaterialCost = 1;
//        String anvilMaterialItem = "bountifulbaubles:resplendent_token";
//    }
//
//    public static void init() {
//        Path configPath = FMLPaths.CONFIGDIR.get().resolve(CONFIG_FILE);
//        if (!Files.exists(configPath)) {
//            createDefaultConfig(configPath);
//        }
//        loadConfig(configPath);
//    }
//
//    private static void createDefaultConfig(Path configPath) {
//        ConfigData configData = new ConfigData();
//
//        // 设置默认物品
//        Map<ResourceLocation, String> defaultItems = new HashMap<>();
//        defaultItems.put(new ResourceLocation("artifacts", "feral_claws"), "hands");
//        defaultItems.put(new ResourceLocation("artifacts", "power_glove"), "hands");
//        defaultItems.put(new ResourceLocation("artifacts", "cross_necklace"), "necklace");
//        defaultItems.forEach((key, value) -> configData.items.put(key.toString(), value));
//
//        try (Writer writer = Files.newBufferedWriter(configPath)) {
//            String jsonComment = """
//            // BountifulBaubles 物品配置文件
//            // 格式说明：
//            // {
//            //   "items": {
//            //     "模组ID:物品ID": "饰品插槽ID",
//            //     "模组ID:物品ID2": "饰品插槽ID2"
//            //   },
//            //   "anvilXpCost": 5,        // 铁砧重铸消耗的经验等级
//            //   "anvilMaterialCost": 1,   // 铁砧重铸消耗的材料数量
//            //   "anvilMaterialItem": "模组ID:物品ID"  // 铁砧重铸所需的材料
//            // }
//            //
//            // 示例:
//            // {
//            //   "items": {
//            //     "artifacts:feral_claws": "hands",     // artifacts模组的feral_claws物品，使用hands插槽
//            //     "artifacts:power_glove": "hands",     // artifacts模组的power_glove物品，使用hands插槽
//            //     "artifacts:cross_necklace": "necklace" // artifacts模组的cross_necklace物品，使用necklace插槽
//            //   },
//            //   "anvilXpCost": 5,        // 铁砧重铸消耗5级经验
//            //   "anvilMaterialCost": 1,   // 铁砧重铸消耗1个材料
//            //   "anvilMaterialItem": "bountifulbaubles:spectral_silt"  // 使用光谱粉尘作为重铸材料
//            // }
//            //
//            // 常用插槽ID:
//            // - "curio"    - 通用插槽
//            // - "hands"    - 手部插槽
//            // - "necklace" - 项链插槽
//            // - "belt"     - 腰带插槽
//            // - "ring"     - 戒指插槽
//            // - "charm"    - 护符插槽
//            // - "head"     - 头部插槽
//            // - "body"     - 身体插槽
//            // - "back"     - 背部插槽
//            //
//            // 注意事项:
//            // 1. 确保物品ID正确，可以在游戏中使用F3+H查看物品的完整ID
//            // 2. 确保使用正确的插槽ID，插槽必须是已经注册的Curios插槽
//            // 3. 修改配置文件后需要重启游戏才能生效
//            // 4. 确保JSON格式正确，每个条目后都要有逗号，最后一个条目不需要逗号
//            // 5. 物品ID格式必须是 "模组ID:物品ID" 的形式
//            // 6. anvilXpCost 必须大于0
//            // 7. anvilMaterialCost 必须大于0
//            // 8. anvilMaterialItem 必须是有效的物品ID
//            //
//            // 以下是默认配置:\n\n""";
//
//            writer.write(jsonComment);
//            GSON.toJson(configData, writer);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static void loadConfig(Path configPath) {
//        try (Reader reader = Files.newBufferedReader(configPath)) {
//            ConfigData configData = GSON.fromJson(reader, ConfigData.class);
//
//            modifiableItems.clear();
//            if (configData != null) {
//                if (configData.items != null) {
//                    configData.items.forEach((key, value) ->
//                            modifiableItems.put(new ResourceLocation(key), value));
//                }
//                // 加载铁砧配置，确保最小值为1
//                anvilXpCost = Math.max(1, configData.anvilXpCost);
//                anvilMaterialCost = Math.max(1, configData.anvilMaterialCost);
//                // 加载重铸材料
//                if (configData.anvilMaterialItem != null && !configData.anvilMaterialItem.isEmpty()) {
//                    anvilMaterialItem = new ResourceLocation(configData.anvilMaterialItem);
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // Getter方法
//    public static int getAnvilXpCost() {
//        return anvilXpCost;
//    }
//
//    public static int getAnvilMaterialCost() {
//        return anvilMaterialCost;
//    }
//
//    public static Item getAnvilMaterial() {
//        return BuiltInRegistries.ITEM.get(anvilMaterialItem);
//    }
//
//    public static boolean isItemModifiable(ResourceLocation itemId) {
//        return modifiableItems.containsKey(itemId);
//    }
//
//    public static String getSlotForItem(ResourceLocation itemId) {
//        return modifiableItems.getOrDefault(itemId, "hands");
//    }
//
//    private static Map<String, String> convertToStringMap(Map<ResourceLocation, String> map) {
//        Map<String, String> stringMap = new HashMap<>();
//        map.forEach((key, value) -> stringMap.put(key.toString(), value));
//        return stringMap;
//    }
//}