package com.jinqinxixi.bountifulbaubles.network.packet;

import com.jinqinxixi.bountifulbaubles.item.Baubles.MindsEyeItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.AbstractMap;
import java.util.UUID;
import java.util.function.Supplier;

public class MarkTargetPacket {
    private final UUID playerUUID;
    private final int entityId;
    private final long endTime;
    private final boolean shouldClear;
    private final PacketType type;

    // 数据包类型枚举
    public enum PacketType {
        MARK,       // 标记目标
        CLEAR,      // 清除目标
        UPDATE      // 更新目标
    }

    public MarkTargetPacket(UUID playerUUID, int entityId, long endTime, boolean shouldClear) {
        this.playerUUID = playerUUID;
        this.entityId = entityId;
        this.endTime = endTime;
        this.shouldClear = shouldClear;
        this.type = shouldClear ? PacketType.CLEAR : PacketType.MARK;
    }

    // 从ByteBuf构造数据包
    public MarkTargetPacket(FriendlyByteBuf buf) {
        this.playerUUID = buf.readUUID();
        this.entityId = buf.readInt();
        this.endTime = buf.readLong();
        this.shouldClear = buf.readBoolean();
        this.type = PacketType.values()[buf.readByte()];
    }

    // 编码数据包
    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(playerUUID);
        buf.writeInt(entityId);
        buf.writeLong(endTime);
        buf.writeBoolean(shouldClear);
        buf.writeByte(type.ordinal());
    }

    // 处理数据包
    public static void handle(MarkTargetPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // 确保在客户端处理
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                    handleClient(packet);
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }

    // 客户端处理逻辑
    private static void handleClient(MarkTargetPacket packet) {
        switch (packet.type) {
            case CLEAR:
                // 清除标记
                MindsEyeItem.getMarkedTargets().remove(packet.playerUUID);
                break;
            case MARK:
            case UPDATE:
                // 更新或添加标记
                if (packet.shouldClear) {
                    MindsEyeItem.getMarkedTargets().remove(packet.playerUUID);
                } else {
                    MindsEyeItem.getMarkedTargets().put(packet.playerUUID,
                            new AbstractMap.SimpleEntry<>(packet.entityId, packet.endTime));
                }
                break;
        }
    }

    // Getter方法
    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public int getEntityId() {
        return entityId;
    }

    public long getEndTime() {
        return endTime;
    }

    public boolean isShouldClear() {
        return shouldClear;
    }

    public PacketType getType() {
        return type;
    }
}