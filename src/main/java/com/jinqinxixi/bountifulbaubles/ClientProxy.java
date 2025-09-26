package com.jinqinxixi.bountifulbaubles;

import com.jinqinxixi.bountifulbaubles.client.handler.BottledCloudInputHandler;
import com.jinqinxixi.bountifulbaubles.network.handler.NetworkHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = BountifulBaublesMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientProxy {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // 在客户端设置时初始化
        init();
    }

    public static void init() {
        // 客户端专用注册
        MinecraftForge.EVENT_BUS.register(BottledCloudInputHandler.class);
        NetworkHandler.registerPackets();

        BountifulBaublesMod.LOGGER.info("ClientProxy initialized - Network packets registered");
    }
}