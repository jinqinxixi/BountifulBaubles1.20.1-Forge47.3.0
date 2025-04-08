package com.jinqinxixi.bountifulbaubles.client.handler;

import com.jinqinxixi.bountifulbaubles.item.Baubles.BottledCloudItem;
import com.jinqinxixi.bountifulbaubles.network.handler.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;


public class BottledCloudInputHandler {
    private static boolean canDoubleJump;
    private static boolean hasReleasedJumpKey = true;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        updateJumpStates(player);
        attemptDoubleJump(player);
    }

    private static void updateJumpStates(LocalPlayer player) {
        if (player.onGround() || player.onClimbable() || player.isInWater()) {
            canDoubleJump = true;
            hasReleasedJumpKey = false;
        } else if (!isJumpPressed()) {
            hasReleasedJumpKey = true;
        }
    }

    private static void attemptDoubleJump(LocalPlayer player) {
        if (canDoubleJump &&
                hasReleasedJumpKey &&
                isJumpPressed() &&
                !player.getAbilities().flying &&
                BottledCloudItem.hasBottledCloud(player)) {
            NetworkHandler.CHANNEL.sendToServer(
                    new NetworkHandler.DoubleJumpPacket(true)
            );
            BottledCloudItem.jump(player);
            canDoubleJump = false;
            hasReleasedJumpKey = false;
        }
    }

    private static boolean isJumpPressed() {
        return Minecraft.getInstance().options.keyJump.isDown();
    }
}