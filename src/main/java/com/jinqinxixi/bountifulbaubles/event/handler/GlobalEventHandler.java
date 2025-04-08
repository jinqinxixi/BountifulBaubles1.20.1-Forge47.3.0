package com.jinqinxixi.bountifulbaubles.event.handler;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class GlobalEventHandler {
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent event) {
        Player player = event.player;
        CompoundTag data = player.getPersistentData();

        if (data.getInt("heart_cooldown") > 0) {
            data.putInt("heart_cooldown", data.getInt("heart_cooldown") - 1);
            if (data.getInt("heart_cooldown") <= 0) {
                data.putBoolean("heart_protected", false);
            }
        }
    }
}
