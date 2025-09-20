package com.jinqinxixi.bountifulbaubles.network.packet;

import com.jinqinxixi.bountifulbaubles.item.Baubles.BottledCloudItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SpawnParticlesPacket {
    private final int playerId;

    public SpawnParticlesPacket(int playerId) {
        this.playerId = playerId;
    }

    public SpawnParticlesPacket(FriendlyByteBuf buf) {
        this.playerId = buf.readVarInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(playerId);
    }

    public static void handle(SpawnParticlesPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level != null) {
                Entity entity = level.getEntity(msg.playerId);
                if (entity instanceof Player player) {
                    BottledCloudItem.spawnJumpParticles(player);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}