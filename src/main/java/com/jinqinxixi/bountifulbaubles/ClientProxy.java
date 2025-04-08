package com.jinqinxixi.bountifulbaubles;

import com.jinqinxixi.bountifulbaubles.client.handler.BottledCloudInputHandler;
import com.jinqinxixi.bountifulbaubles.network.handler.NetworkHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@OnlyIn(Dist.CLIENT)
public class ClientProxy {
    public static void init() {
        // 客户端专用注册
        MinecraftForge.EVENT_BUS.register(BottledCloudInputHandler.class);
        NetworkHandler.registerPackets();
    }
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // 客户端初始化
    }
}

