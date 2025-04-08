package com.jinqinxixi.bountifulbaubles.system.wormhole;

import com.jinqinxixi.bountifulbaubles.item.Baubles.WormholeMirrorItem;
import com.jinqinxixi.bountifulbaubles.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.List;
import java.util.function.Supplier;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1.0";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("bountifulbaubles", "wormhole_mirror"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int packetId = 0;

        // 通用包（服务端和客户端都注册）
        INSTANCE.registerMessage(packetId++, RequestPlayerListPacket.class,
                RequestPlayerListPacket::encode, RequestPlayerListPacket::decode,
                (msg, ctx) -> handlePacket(ctx, () -> RequestPlayerListPacket.handle(msg, ctx.get())));

        INSTANCE.registerMessage(packetId++, PlayerSelectedPacket.class,
                PlayerSelectedPacket::encode, PlayerSelectedPacket::decode,
                (msg, ctx) -> handlePacket(ctx, () -> PlayerSelectedPacket.handle(msg, ctx.get())));

        INSTANCE.registerMessage(packetId++, TeleportRequestPacket.class,
                TeleportRequestPacket::encode, TeleportRequestPacket::decode,
                (msg, ctx) -> handlePacket(ctx, () -> TeleportRequestPacket.handle(msg, ctx.get())));

        INSTANCE.registerMessage(packetId++, TeleportResponsePacket.class,
                TeleportResponsePacket::encode, TeleportResponsePacket::decode,
                (msg, ctx) -> handlePacket(ctx, () -> TeleportResponsePacket.handle(msg, ctx.get())));

        // 客户端专用包（仍注册，但处理时检查Dist）
        INSTANCE.registerMessage(packetId++, PlayerListResponsePacket.class,
                PlayerListResponsePacket::encode, PlayerListResponsePacket::decode,
                (msg, ctx) -> {
                    if (ctx.get().getDirection().getReceptionSide().isClient()) {
                        handleClientPacket(ctx, () -> PlayerListResponsePacket.handleClient(msg, ctx.get()));
                    }
                });

        INSTANCE.registerMessage(packetId++, SetCooldownPacket.class,
                SetCooldownPacket::encode, SetCooldownPacket::decode,
                (msg, ctx) -> {
                    if (ctx.get().getDirection().getReceptionSide().isClient()) {
                        handleClientPacket(ctx, () -> SetCooldownPacket.handleClient(msg, ctx.get()));
                    }
                });
    }
    //============== 数据包处理核心方法 ==============
    private static void handlePacket(Supplier<NetworkEvent.Context> ctxSupplier, Runnable handler) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            try {
                System.out.println("[SERVER] 处理数据包: " + handler.getClass().getSimpleName());
                handler.run();
            } finally {
                ctx.setPacketHandled(true);
            }
        });
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleClientPacket(Supplier<NetworkEvent.Context> ctxSupplier, Runnable handler) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            try {
                System.out.println("[CLIENT] 处理客户端数据包");
                handler.run();
            } finally {
                ctx.setPacketHandled(true);
            }
        });
    }

    //============== 数据包定义 ==============

    //------------------ 请求玩家列表包 ------------------
    public static class RequestPlayerListPacket {
        private final boolean isFromPotion;

        public RequestPlayerListPacket(boolean isFromPotion) {
            this.isFromPotion = isFromPotion;
        }

        public static void encode(RequestPlayerListPacket msg, FriendlyByteBuf buffer) {
            buffer.writeBoolean(msg.isFromPotion);
        }

        public static RequestPlayerListPacket decode(FriendlyByteBuf buffer) {
            return new RequestPlayerListPacket(buffer.readBoolean());
        }

        public static void handle(RequestPlayerListPacket msg, NetworkEvent.Context ctx) {
            ServerPlayer sender = ctx.getSender();
            if (sender == null) return;

            System.out.println("[SERVER] 收到玩家列表请求 fromPotion=" + msg.isFromPotion);

            // 处理冷却
            if (!msg.isFromPotion) {
                handleCooldown(sender);
            }

            // 获取在线玩家列表
            List<String> players = sender.server.getPlayerList().getPlayers().stream()
                    .filter(p -> !p.getGameProfile().getName().equals(sender.getGameProfile().getName()))
                    .map(p -> p.getGameProfile().getName())
                    .toList();

            // 发送给客户端
            sendPlayerList(sender, players);
        }

        private static void handleCooldown(ServerPlayer player) {
            ItemStack mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
            ItemStack offHand = player.getItemInHand(InteractionHand.OFF_HAND);
            boolean holdingMirror = mainHand.getItem() == ModItems.WORMHOLE_MIRROR.get() ||
                    offHand.getItem() == ModItems.WORMHOLE_MIRROR.get();

            if (holdingMirror) {
                player.getCooldowns().addCooldown(ModItems.WORMHOLE_MIRROR.get(), 2);
            }
        }

        private static void sendPlayerList(ServerPlayer target, List<String> players) {
            System.out.println("[SERVER] 发送玩家列表给 " + target.getName().getString() + ": " + players);
            INSTANCE.send(PacketDistributor.PLAYER.with(() -> target), new PlayerListResponsePacket(players));
        }
    }

    //------------------ 玩家列表响应包 ------------------
    public static class PlayerListResponsePacket {
        private final List<String> players;

        public PlayerListResponsePacket(List<String> players) {
            this.players = players;
        }

        public static void encode(PlayerListResponsePacket msg, FriendlyByteBuf buffer) {
            buffer.writeCollection(msg.players, FriendlyByteBuf::writeUtf);
        }

        public static PlayerListResponsePacket decode(FriendlyByteBuf buffer) {
            return new PlayerListResponsePacket(
                    buffer.readList(FriendlyByteBuf::readUtf)
            );
        }

        @OnlyIn(Dist.CLIENT)
        public static void handleClient(PlayerListResponsePacket msg, NetworkEvent.Context ctx) {
            Minecraft.getInstance().execute(() -> {
                System.out.println("[CLIENT] 收到玩家列表: " + msg.players);
                if (Minecraft.getInstance().screen == null) {
                    Minecraft.getInstance().setScreen(new PlayerSelectionScreen(msg.players));
                }
            });
        }
    }

    //------------------ 玩家选择包 ------------------
    public static class PlayerSelectedPacket {
        private final String playerName;

        public PlayerSelectedPacket(String playerName) {
            this.playerName = playerName;
        }

        public static void encode(PlayerSelectedPacket msg, FriendlyByteBuf buffer) {
            buffer.writeUtf(msg.playerName);
        }

        public static PlayerSelectedPacket decode(FriendlyByteBuf buffer) {
            return new PlayerSelectedPacket(buffer.readUtf());
        }

        public static void handle(PlayerSelectedPacket msg, NetworkEvent.Context ctx) {
            ServerPlayer player = ctx.getSender();
            if (player == null) return;

            ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
            if (stack.getItem() instanceof WormholeMirrorItem mirror) {
                mirror.teleportToPlayer(player, msg.playerName);
            }
        }
    }

    //------------------ 传送请求包 ------------------
    public static class TeleportRequestPacket {
        private final String requesterName;
        private final String targetName;

        public TeleportRequestPacket(String requesterName, String targetName) {
            this.requesterName = requesterName;
            this.targetName = targetName;
        }

        public static void encode(TeleportRequestPacket msg, FriendlyByteBuf buffer) {
            buffer.writeUtf(msg.requesterName);
            buffer.writeUtf(msg.targetName);
        }

        public static TeleportRequestPacket decode(FriendlyByteBuf buffer) {
            return new TeleportRequestPacket(buffer.readUtf(), buffer.readUtf());
        }

        public static void handle(TeleportRequestPacket msg, NetworkEvent.Context ctx) {
            ServerPlayer target = ctx.getSender().server.getPlayerList().getPlayerByName(msg.targetName);
            if (target != null) {
                INSTANCE.send(PacketDistributor.PLAYER.with(() -> target),
                        new TeleportNotificationPacket(msg.requesterName));
            }
        }
    }

    //------------------ 传送通知包 ------------------
    public static class TeleportNotificationPacket {
        private final String requesterName;

        public TeleportNotificationPacket(String requesterName) {
            this.requesterName = requesterName;
        }

        public static void encode(TeleportNotificationPacket msg, FriendlyByteBuf buffer) {
            buffer.writeUtf(msg.requesterName);
        }

        public static TeleportNotificationPacket decode(FriendlyByteBuf buffer) {
            return new TeleportNotificationPacket(buffer.readUtf());
        }

        @OnlyIn(Dist.CLIENT)
        public static void handleClient(TeleportNotificationPacket msg, NetworkEvent.Context ctx) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                player.sendSystemMessage(
                        Component.translatable("msg.mirror.request_received", msg.requesterName));
            }
        }
    }

    //------------------ 传送响应包 ------------------
    public static class TeleportResponsePacket {
        private final boolean accepted;
        private final String requesterName;
        private final String targetName;

        public TeleportResponsePacket(boolean accepted, String requesterName, String targetName) {
            this.accepted = accepted;
            this.requesterName = requesterName;
            this.targetName = targetName;
        }

        public static void encode(TeleportResponsePacket msg, FriendlyByteBuf buffer) {
            buffer.writeBoolean(msg.accepted);
            buffer.writeUtf(msg.requesterName);
            buffer.writeUtf(msg.targetName);
        }

        public static TeleportResponsePacket decode(FriendlyByteBuf buffer) {
            return new TeleportResponsePacket(
                    buffer.readBoolean(),
                    buffer.readUtf(),
                    buffer.readUtf());
        }

        public static void handle(TeleportResponsePacket msg, NetworkEvent.Context ctx) {
            ServerPlayer requester = ctx.getSender().server.getPlayerList().getPlayerByName(msg.requesterName);
            ServerPlayer target = ctx.getSender().server.getPlayerList().getPlayerByName(msg.targetName);

            if (msg.accepted && requester != null && target != null) {
                requester.teleportTo(
                        target.serverLevel(),
                        target.getX(),
                        target.getY(),
                        target.getZ(),
                        target.getYRot(),
                        target.getXRot());
                ((WormholeMirrorItem) ModItems.WORMHOLE_MIRROR.get()).playTeleportEffects(requester);
            }
        }
    }

    //------------------ 设置冷却包 ------------------
    public static class SetCooldownPacket {
        private final int ticks;

        public SetCooldownPacket(int ticks) {
            this.ticks = ticks;
        }

        public static void encode(SetCooldownPacket msg, FriendlyByteBuf buffer) {
            buffer.writeInt(msg.ticks);
        }

        public static SetCooldownPacket decode(FriendlyByteBuf buffer) {
            return new SetCooldownPacket(buffer.readInt());
        }

        @OnlyIn(Dist.CLIENT)
        public static void handleClient(SetCooldownPacket msg, NetworkEvent.Context ctx) {
            LocalPlayer player = Minecraft.getInstance().player;
            System.out.println("[CLIENT] 设置冷却: " + msg.ticks + " ticks");
            if (player != null) {
                player.getCooldowns().addCooldown(ModItems.WORMHOLE_MIRROR.get(), msg.ticks);
            }
        }
    }
}
