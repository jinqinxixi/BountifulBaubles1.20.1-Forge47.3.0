package com.jinqinxixi.bountifulbaubles.system.wormhole;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TeleportRequestManager {
    private static final Map<UUID, TeleportRequest> REQUESTS = new ConcurrentHashMap<>();

    public static class TeleportRequest {
        public final ServerPlayer requester;
        public final ServerPlayer target;
        public final long createTime;

        public TeleportRequest(ServerPlayer requester, ServerPlayer target) {
            this.requester = requester;
            this.target = target;
            this.createTime = System.currentTimeMillis();
        }
        public boolean isValid() {
            return !requester.hasDisconnected() &&
                    !target.hasDisconnected() &&
                    requester.isAlive() &&
                    target.isAlive();
        }
    }

    public static UUID addRequest(ServerPlayer requester, ServerPlayer target) {
        UUID requestId = UUID.randomUUID();
        REQUESTS.put(requestId, new TeleportRequest(requester, target));
        return requestId;
    }

    public static void checkExpiredRequests() {
        long now = System.currentTimeMillis();
        REQUESTS.entrySet().removeIf(entry -> {
            TeleportRequest request = entry.getValue();

            if (now - request.createTime > 30000) {
                if (request.isValid()) {  // 只在双方都在线时通知
                    notifyExpired(request);
                }
                return true;
            }
            return false;
        });
    }
    private static void notifyExpired(TeleportRequest request) {
        // 给请求方发送通知
        if (request.requester.isAlive() && !request.requester.hasDisconnected()) {
            request.requester.sendSystemMessage(
                    Component.translatable("msg.mirror.request_expired",
                                    request.target.getScoreboardName())
                            .withStyle(ChatFormatting.GOLD)
            );
        }

        // 给目标方发送通知
        if (request.target.isAlive() && !request.target.hasDisconnected()) {
            request.target.sendSystemMessage(
                    Component.translatable("msg.mirror.request_expired",
                                    request.requester.getScoreboardName())
                            .withStyle(ChatFormatting.GOLD)
            );
        }
    }

    public static void removeRequest(UUID requestId) {
        REQUESTS.remove(requestId);
    }

    public static TeleportRequest getRequest(UUID requestId) {
        return REQUESTS.get(requestId);
    }

}
