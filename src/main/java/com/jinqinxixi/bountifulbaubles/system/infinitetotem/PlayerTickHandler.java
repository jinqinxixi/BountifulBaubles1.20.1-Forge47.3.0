package com.jinqinxixi.bountifulbaubles.system.infinitetotem;

import com.jinqinxixi.bountifulbaubles.BountifulBaublesMod;
import com.jinqinxixi.bountifulbaubles.item.Baubles.InfiniteTotemOfUndyingItem;
import com.jinqinxixi.bountifulbaubles.network.handler.NetworkHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BountifulBaublesMod.MOD_ID)
public class PlayerTickHandler {
    // 处理玩家每tick逻辑
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.player.level().isClientSide) {
            Player player = event.player;
            CompoundTag data = player.getPersistentData();
            String key = InfiniteTotemOfUndyingItem.PUBLIC_COOLDOWN_TAG;

            if (data.contains(key)) {
                int remaining = data.getInt(key);
                if (remaining > 0) {
                    int newRemaining = remaining - 1;
                    data.putInt(key, newRemaining);
                    // 同步冷却时间到客户端
                    NetworkHandler.InfiniteTotemCooldownPacket.sendToClient(
                            (ServerPlayer) player,
                            newRemaining
                    );
                } else {
                    data.remove(key);
                    // 清除客户端冷却显示
                    NetworkHandler.InfiniteTotemCooldownPacket.sendToClient(
                            (ServerPlayer) player,
                            0
                    );
                }
            }
        }
    }

    // 处理玩家重生时的数据复制
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;

        Player original = event.getOriginal();
        Player newPlayer = event.getEntity();
        CompoundTag originalData = original.getPersistentData();
        String key = InfiniteTotemOfUndyingItem.PUBLIC_COOLDOWN_TAG;

        if (originalData.contains(key)) {
            int remaining = originalData.getInt(key);
            newPlayer.getPersistentData().putInt(key, remaining);

            if (newPlayer instanceof ServerPlayer serverPlayer) {
                NetworkHandler.InfiniteTotemCooldownPacket.sendToClient(
                        serverPlayer,
                        remaining
                );
            }
        }
    }
}
