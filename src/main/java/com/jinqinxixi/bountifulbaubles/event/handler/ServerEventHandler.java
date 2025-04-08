package com.jinqinxixi.bountifulbaubles.event.handler;

import com.jinqinxixi.bountifulbaubles.BountifulBaublesMod;
import com.jinqinxixi.bountifulbaubles.system.wormhole.TeleportRequestManager;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BountifulBaublesMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerEventHandler {
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            TeleportRequestManager.checkExpiredRequests();
        }
    }
}
