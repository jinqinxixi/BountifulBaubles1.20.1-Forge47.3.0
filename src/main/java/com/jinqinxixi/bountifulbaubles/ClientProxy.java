package com.jinqinxixi.bountifulbaubles;

import com.jinqinxixi.bountifulbaubles.client.handler.BottledCloudInputHandler;
import com.jinqinxixi.bountifulbaubles.client.model.AmuletModel;
import com.jinqinxixi.bountifulbaubles.client.model.CrownGoldModel;
import com.jinqinxixi.bountifulbaubles.client.model.SunglassesModel;
import com.jinqinxixi.bountifulbaubles.client.render.curio.AmuletCurioRenderer;
import com.jinqinxixi.bountifulbaubles.client.render.curio.AnkhCharmCurioRenderer;
import com.jinqinxixi.bountifulbaubles.client.render.curio.CrownGoldCurioRenderer;
import com.jinqinxixi.bountifulbaubles.client.render.curio.SunglassesCurioRenderer;
import com.jinqinxixi.bountifulbaubles.item.ModItems;
import com.jinqinxixi.bountifulbaubles.network.handler.NetworkHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = BountifulBaublesMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientProxy {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        BountifulBaublesMod.LOGGER.info("[BB] ClientProxy.onClientSetup 触发，开始登记饰品渲染器");

        // 饰品的穿戴渲染要在这里登记，不能更早：物品是在注册事件里填进注册表的，而 init() 是从
        // 主类构造函数调用的，那个时刻注册表还空着，读 RegistryObject 会抛 "Registry Object not
        // present"。客户端设置事件是注册完成之后才发的。
        registerCurioRenderers();

        BountifulBaublesMod.LOGGER.info("[BB] 饰品渲染器登记完成：金冠 + 6 条项链");
    }

    public static void init() {
        // 客户端专用注册
        MinecraftForge.EVENT_BUS.register(BottledCloudInputHandler.class);
        NetworkHandler.registerPackets();

        BountifulBaublesMod.LOGGER.info("ClientProxy initialized - Network packets registered");
    }

    /**
     * 登记有穿戴外观的饰品。
     *
     * <p>1.20.1 的 Curios 用注册制：这里登记之后，Curios 自己的渲染层会在玩家身上回调对应的
     * 渲染器。模型在这里 bake 一次就够，之后的每一帧都用同一份。
     */
    private static void registerCurioRenderers() {
        var crownRoot = CrownGoldModel.createLayer().bakeRoot();

        CuriosRendererRegistry.register(ModItems.CURIOUS_CROWN.get(),
                () -> new CrownGoldCurioRenderer(crownRoot));

        registerAmulets();

        // 墨镜：一个和头同大的盒子，架在脸上。
        var sunglassesRoot = SunglassesModel.createLayer().bakeRoot();
        CuriosRendererRegistry.register(ModItems.SUNGLASSES.get(),
                () -> new SunglassesCurioRenderer(sunglassesRoot));

        // 安卡护符：平面图标，贴在胸口（照 Trinkets 荣耀之盾的 ChestItem）。
        CuriosRendererRegistry.register(ModItems.ANKH_CHARM.get(),
                AnkhCharmCurioRenderer::new);
    }

    /**
     * 登记项链：好奇护符和五条原版项链。
     *
     * <p>六条共用一个模型、各配一张贴图 —— 项链的形状是一样的，差别只在贴上画了哪个吊坠。
     * 贴图沿用原版的六张，图案都画在同一个位置，所以 UV 不用逐个去对。
     */
    private static void registerAmulets() {
        // 模型只 bake 一次：它是只读的形状，六条项链各拿一份反而多占内存。
        var amuletRoot = AmuletModel.createLayer().bakeRoot();

        amulet(ModItems.CURIOUS_AMULET.get(), amuletRoot, "amulet_curious");
        amulet(ModItems.CROSS_NECKLACE.get(), amuletRoot, "amulet_cross");
        amulet(ModItems.AMULET_SIN_EMPTY.get(), amuletRoot, "amulet_sin_empty");
        amulet(ModItems.GLUTTONY_PENDANT.get(), amuletRoot, "amulet_sin_gluttony");
        amulet(ModItems.PRIDE_PENDANT.get(), amuletRoot, "amulet_sin_pride");
        amulet(ModItems.WRATH_PENDANT.get(), amuletRoot, "amulet_sin_wrath");
    }

    /** 给一条项链登记渲染器，贴图按名字取。 */
    private static void amulet(net.minecraft.world.item.Item item,
                               net.minecraft.client.model.geom.ModelPart root,
                               String textureName) {
        ResourceLocation texture = new ResourceLocation(BountifulBaublesMod.MOD_ID,
                "textures/models/armor/" + textureName + ".png");

        CuriosRendererRegistry.register(item, () -> new AmuletCurioRenderer(root, texture));
    }
}