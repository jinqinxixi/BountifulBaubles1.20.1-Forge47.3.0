// 新建 PlayerEventHandler.java
package com.jinqinxixi.bountifulbaubles.system.infinitetotem;

import com.jinqinxixi.bountifulbaubles.item.Baubles.InfiniteTotemOfUndyingItem;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class PlayerEventHandler {
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (!player.level().isClientSide) { // 确保只在服务端执行
            InfiniteTotemOfUndyingItem.adjustExistingCooldown(player);
        }
    }
}
