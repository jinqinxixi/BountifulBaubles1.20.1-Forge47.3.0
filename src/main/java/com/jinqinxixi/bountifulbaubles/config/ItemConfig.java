package com.jinqinxixi.bountifulbaubles.config;

import com.jinqinxixi.bountifulbaubles.BountifulBaublesMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.toml.TomlFormat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 饰品开关配置（写入独立文件 {@code bountifulbaubles-items.toml}）。
 *
 * <p>设为 false 即禁用该饰品：
 * <ul>
 *   <li>不再从战利品表 / 怪物掉落中生成</li>
  *   <li>物品描述里显示「已禁用」</li>
 * </ul>
 *
 * <p>注意：Forge 的配置在启动时加载，修改后需要重新进入游戏（或重启客户端）才生效。
 */
public final class ItemConfig
{
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    /** 物品 id（不含命名空间） -> 开关，顺序与 ModItems 注册顺序一致 */
    private static final Map<String, ForgeConfigSpec.BooleanValue> ENABLED = new LinkedHashMap<>();

    /** 非 null 时以服务端同步的状态为准（多人服务器客户端专用）。 */
    private static volatile Map<String, Boolean> serverOverride = null;

    /** 物品 id -> 中文名，仅用于生成配置文件里的注释 */
    private static final Map<String, String> NAMES = new LinkedHashMap<>();

    static
    {
        NAMES.put("broken_heart", "破碎的心");
        NAMES.put("magic_mirror", "魔镜");
        NAMES.put("wormhole_mirror", "虫洞镜");
        NAMES.put("disintegration_tablet", "崩解牌");
        NAMES.put("ender_dragon_scale", "末影龙鳞");
        NAMES.put("amulet_sin_empty", "罪恶吊坠 (空)");
        NAMES.put("spectral_silt", "幽冥粉");
        NAMES.put("balloon", "气球");
        NAMES.put("obsidian_skull", "黑曜石头颅");
        NAMES.put("sunglasses", "太阳镜");
        NAMES.put("apple", "禁果");
        NAMES.put("vitamins", "维生素");
        NAMES.put("ring_overclocking", "超频戒指");
        NAMES.put("shulker_heart", "潜影贝之心");
        NAMES.put("ring_free_action", "自由行走之戒");
        NAMES.put("bezoar", "牛黄");
        NAMES.put("black_dragon_scale", "黑龙鳞");
        NAMES.put("mixed_dragon_scale", "混色龙鳞");
        NAMES.put("lucky_horseshoe", "幸运马蹄铁");
        NAMES.put("ankh_charm", "安卡护符");
        NAMES.put("horseshoe_balloon", "马蹄气球");
        NAMES.put("cross_necklace", "十字项链");
        NAMES.put("phylactery_charm", "勾玉护符");
        NAMES.put("pride_pendant", "傲慢吊坠");
        NAMES.put("wrath_pendant", "愤怒吊坠");
        NAMES.put("gluttony_pendant", "贪食吊坠");
        NAMES.put("resplendent_token", "闪耀币");
        NAMES.put("broken_black_dragon_scale", "破裂的黑龙鳞");
        NAMES.put("curious_ring", "奇异戒指");
        NAMES.put("curious_amulet", "奇异护符");
        NAMES.put("curious_crown", "奇异之冠");
        NAMES.put("curious_knuckles", "奇异手指");
        NAMES.put("auto_torch", "自动火把");
        NAMES.put("bottled_cloud", "云朵瓶");
        NAMES.put("endless_pearl", "永恒珍珠");
        NAMES.put("vampiric_glove", "吸血手套");
        NAMES.put("gauntlets_dexterity", "灵巧手套");
        NAMES.put("infinite_totem_of_undying", "无限图腾");
        NAMES.put("cobalt_shield", "钴盾");
        NAMES.put("obsidian_shield", "黑曜石护盾");
        NAMES.put("ankh_shield", "安卡护盾");
        NAMES.put("potion_wormhole", "虫洞药水");
        NAMES.put("potion_recall", "回忆药水");
        NAMES.put("minds_eye", "洞察之眼");
        NAMES.put("luck_coin", "幸运硬币");
        NAMES.put("dragon_breath", "火龙之息");
        NAMES.put("ice_shard", "冰碎片");
        NAMES.put("tha_spider", "小蜘蛛");
        NAMES.put("creepo", "吉吉怪");
        NAMES.put("tha_wizard", "小巫师");
        NAMES.put("book_o_enchanting", "咒术书");
        NAMES.put("warm_void", "友善虚空");
        NAMES.put("golden_melon", "黄金瓜");
        NAMES.put("fire_mind", "燃焱意志");
        NAMES.put("drop_spindle", "永恒纺锤");
        NAMES.put("dark_egg", "暗影之卵");
        NAMES.put("mossy_ring", "覆苔指环");
        NAMES.put("mossy_belt", "覆苔腰带");
        NAMES.put("mad_aura", "炽光环");
        NAMES.put("turtle_shell", "海龟鳞");
        NAMES.put("dark_dagger", "暗影匕首");
        NAMES.put("ember", "余烬之石");
        NAMES.put("wither_nail", "凋零之爪");
        NAMES.put("serpent_tooth", "毒蛇之牙");
        NAMES.put("blaze_heart", "烈焰之心");
        NAMES.put("starfish", "小海星");
        NAMES.put("golden_skull", "黄金头颅");
        NAMES.put("butchers_cleaver", "屠宰镰");
        NAMES.put("karma", "摩羯护符");
        NAMES.put("oxalis", "四叶草");
        NAMES.put("glory_shards", "荣耀碎片");
        NAMES.put("ruby_heart", "红宝石之心");
        NAMES.put("rock_candy", "水晶糖");
        NAMES.put("treasure_bag", "宝藏袋");
    }

    static
    {
        BUILDER.comment(
                "饰品开关。设为 false 即禁用该饰品：",
                "  - 不再从战利品表或怪物掉落中生成",
                "  - 物品描述中显示「已禁用」",
                "修改后在游戏内执行 /bountifulbaubles reload 即可立即生效，无需重启游戏。"
        ).push("Items");

        for (Map.Entry<String, String> entry : NAMES.entrySet())
        {
            String id = entry.getKey();
            String name = entry.getValue();
            String label = name.isEmpty() ? id : name + " (" + id + ")";
            ENABLED.put(id, BUILDER.comment(label).define("enable_" + id, true));
        }

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    private ItemConfig()
    {
    }

    /** 按物品 id 判断是否启用；不在本模组命名空间或未登记的一律视为启用。 */
    public static boolean isEnabled(Item item)
    {
        if (item == null)
        {
            return true;
        }
        ResourceLocation key = ForgeRegistries.ITEMS.getKey(item);
        if (key == null || !BountifulBaublesMod.MOD_ID.equals(key.getNamespace()))
        {
            return true;
        }
        String path = key.getPath();
        if (serverOverride != null)
        {
            Boolean fromServer = serverOverride.get(path);
            return fromServer == null || fromServer;
        }
        ForgeConfigSpec.BooleanValue value = ENABLED.get(path);
        return value == null || value.get();
    }

    public static boolean isEnabled(ItemStack stack)
    {
        return stack == null || stack.isEmpty() || isEnabled(stack.getItem());
    }

    /** 供 tooltip / 掉落拦截统一调用，同时兼顾配置项尚未加载的情况。 */
    public static boolean isDisabled(ItemStack stack)
    {
        return !isEnabled(stack);
    }

    // ===================== 热重载与客户端同步 =====================

    /**
     * 服务端重载：重新解析 toml 并把新值写回 ForgeConfigSpec 的运行时缓存。
     *
     * @return 实际更新的开关数量
     */
    public static int reload(Path configDir)
    {
        Path file = configDir.resolve("bountifulbaubles-items.toml");
        if (!Files.exists(file))
        {
            // 文件被删了：把内存里的开关全部恢复成默认值
            // 下次启动游戏时 Forge 会重新生成带注释的配置文件
            ENABLED.values().forEach(value -> value.set(value.getDefault()));
            return -2;
        }
        try
        {
            CommentedConfig parsed = TomlFormat.instance().createParser().parse(Files.readString(file));
            SPEC.acceptConfig(parsed);
            return ENABLED.size();
        }
        catch (Exception e)
        {
            return -1;
        }
    }

    /** 服务端：导出当前全部开关状态，用于推送给客户端。 */
    public static Map<String, Boolean> snapshot()
    {
        Map<String, Boolean> map = new LinkedHashMap<>();
        ENABLED.forEach((id, value) -> map.put(id, value.get()));
        return map;
    }

    /** 客户端：收到服务端同步后，以服务端状态为准。 */
    public static void applyServerState(Map<String, Boolean> state)
    {
        serverOverride = (state == null || state.isEmpty()) ? null : new LinkedHashMap<>(state);
    }

    /** 客户端：断开连接或回到主菜单时清掉覆盖，改回读本地配置。 */
    public static void clearServerState()
    {
        serverOverride = null;
    }

    public static boolean hasServerState()
    {
        return serverOverride != null;
    }
}
