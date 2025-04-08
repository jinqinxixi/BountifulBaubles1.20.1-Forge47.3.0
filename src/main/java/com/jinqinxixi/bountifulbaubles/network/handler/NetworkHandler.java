package com.jinqinxixi.bountifulbaubles.network.handler;

import com.jinqinxixi.bountifulbaubles.BountifulBaublesMod;
import com.jinqinxixi.bountifulbaubles.network.packet.MarkTargetPacket;
import com.jinqinxixi.bountifulbaubles.network.packet.SpawnParticlesPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;
import java.util.function.Supplier;

public class NetworkHandler {
    // 统一通道配置
    private static final String PROTOCOL_VERSION = "2.0";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(BountifulBaublesMod.MOD_ID, "main_channel"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void registerPackets() {
        // 统一注册所有包（客户端和服务器同步）
        registerCommonPackets();
    }

    private static void registerCommonPackets() {
        // 注册顺序必须完全一致
        CHANNEL.registerMessage(packetId++,
                DoubleJumpPacket.class,
                DoubleJumpPacket::encode,
                DoubleJumpPacket::new,
                DoubleJumpPacket::handle
        );

        CHANNEL.registerMessage(packetId++,
                InfiniteTotemCooldownPacket.class,
                InfiniteTotemCooldownPacket::encode,
                InfiniteTotemCooldownPacket::new,
                InfiniteTotemCooldownPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT) // 明确方向
        );

        CHANNEL.registerMessage(packetId++,
                TotemParticlesPacket.class,
                TotemParticlesPacket::encode,
                TotemParticlesPacket::new,
                TotemParticlesPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT) // 指定为服务端->客户端
        );

        CHANNEL.registerMessage(packetId++,
                MarkTargetPacket.class,
                MarkTargetPacket::encode,
                MarkTargetPacket::new,
                MarkTargetPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        );
    }

    // ===== 无限图腾冷却包 =====
    public static class InfiniteTotemCooldownPacket {
        final int cooldown;

        public InfiniteTotemCooldownPacket(int cooldown) {
            this.cooldown = cooldown;
        }

        public InfiniteTotemCooldownPacket(FriendlyByteBuf buf) {
            this.cooldown = buf.readInt();
        }

        public void encode(FriendlyByteBuf buf) {
            buf.writeInt(cooldown);
        }

        // 关键修改点1：移除 @OnlyIn(Dist.CLIENT) 注解
        public static void handle(InfiniteTotemCooldownPacket packet, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                    // 通过 DistExecutor 安全执行客户端代码
                    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                        ClientPacketHandler.handleInfiniteTotemCooldown(packet);
                    });
                }
            });
            ctx.get().setPacketHandled(true);
        }


        public static void sendToClient(ServerPlayer player, int cooldown) {
            CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new InfiniteTotemCooldownPacket(cooldown));
        }
    }

    // ===== 二段跳包 =====
    public static class DoubleJumpPacket {
        private final boolean withParticles;

        public DoubleJumpPacket(boolean withParticles) {
            this.withParticles = withParticles;
        }

        public DoubleJumpPacket(FriendlyByteBuf buf) {
            this.withParticles = buf.readBoolean();
        }

        public void encode(FriendlyByteBuf buf) {
            buf.writeBoolean(withParticles);
        }

        public static void handle(DoubleJumpPacket msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                // 服务端需要广播粒子效果给所有可见玩家（包含自己）
                if (ctx.get().getDirection().getReceptionSide().isServer()) {
                    ServerPlayer sender = ctx.get().getSender();
                    if (sender != null && msg.withParticles) {
                        // 创建粒子数据包
                        SpawnParticlesPacket particlesPacket = new SpawnParticlesPacket(sender.getId());

                        // 发送给触发玩家自己（确保旁观模式可见）
                        CHANNEL.send(PacketDistributor.PLAYER.with(() -> sender), particlesPacket);

                        // 广播给其他追踪该实体的玩家
                        CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> sender), particlesPacket);
                    }
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }

    // ===== 图腾粒子包 =====
    public static class TotemParticlesPacket {
        private final double x, y, z;

        public TotemParticlesPacket(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public TotemParticlesPacket(FriendlyByteBuf buf) {
            this.x = buf.readDouble();
            this.y = buf.readDouble();
            this.z = buf.readDouble();
        }

        public void encode(FriendlyByteBuf buf) {
            buf.writeDouble(x);
            buf.writeDouble(y);
            buf.writeDouble(z);
        }

        public static void handle(TotemParticlesPacket packet, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                        ClientPacketHandler.spawnTotemParticles(packet.x, packet.y, packet.z);
                    });
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }
}