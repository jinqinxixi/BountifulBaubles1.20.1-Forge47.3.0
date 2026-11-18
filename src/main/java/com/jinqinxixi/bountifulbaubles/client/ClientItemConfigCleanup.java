package com.jinqinxixi.bountifulbaubles.client;

import com.jinqinxixi.bountifulbaubles.config.ItemConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 客户端断开连接（回到主菜单 / 换服）时，清掉服务端同步的饰品开关覆盖。
 * <p>
 * 否则 {@code ItemConfig.serverOverride} 会残留上一个服务器的配置，
 * 进入下一个服务器或单机时在收到新同步包之前会读到旧值。
 */
@Mod.EventBusSubscriber(modid = "bountifulbaubles", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientItemConfigCleanup {
    @SubscribeEvent
    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ItemConfig.clearServerState();
    }
}
