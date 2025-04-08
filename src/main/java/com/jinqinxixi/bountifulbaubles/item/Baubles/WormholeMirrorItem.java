package com.jinqinxixi.bountifulbaubles.item.Baubles;

import com.jinqinxixi.bountifulbaubles.system.wormhole.PacketHandler;
import com.jinqinxixi.bountifulbaubles.system.wormhole.TeleportRequestManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.network.PacketDistributor;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class WormholeMirrorItem extends Item {
    private static final int COOLDOWN_TICKS = 2;
    private static final int USE_DURATION = 20;

    public WormholeMirrorItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return USE_DURATION;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }

        if (player.isShiftKeyDown()) {
            if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
                PacketHandler.INSTANCE.send(
                        PacketDistributor.PLAYER.with(() -> serverPlayer),
                        new PacketHandler.RequestPlayerListPacket(false)
                );
            }
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        } else {
            if (canTeleport(player)) {
                player.startUsingItem(hand);
                return InteractionResultHolder.consume(stack);
            }
            return InteractionResultHolder.fail(stack);
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof Player player) {
            if (player.isShiftKeyDown()) {
                if (level.isClientSide) {
                    openPlayerListGUI(player);
                }
            } else {
                if (checkCooldown(player)) {
                    teleportToSpawn((ServerPlayer) player);
                    setCooldown(player);
                }
            }
        }
        return stack;
    }

    @OnlyIn(Dist.CLIENT)
    private void openPlayerListGUI(Player player) {
        PacketHandler.INSTANCE.sendToServer(new PacketHandler.RequestPlayerListPacket(false));
    }

    public void teleportToPlayer(ServerPlayer sender, String targetName) {
        if (sender.getGameProfile().getName().equalsIgnoreCase(targetName)) {
            sender.sendSystemMessage(Component.translatable("msg.mirror.self_teleport"));
            return;
        }

        ServerPlayer target = sender.server.getPlayerList().getPlayerByName(targetName);
        if (target == null) {
            sender.sendSystemMessage(Component.translatable("msg.mirror.player_offline", targetName));
            return;
        }

        if (!checkCooldown(sender)) return;

        UUID requestId = TeleportRequestManager.addRequest(sender, target);
        Component message = createClickableMessage(sender, target, requestId);
        target.sendSystemMessage(message);
        sender.sendSystemMessage(Component.translatable("msg.mirror.request_sent", targetName));
        setCooldown(sender);

        // 获取使用的物品并处理消耗
        ItemStack usedStack = sender.getMainHandItem();
        if (usedStack.getItem() == this) {
            sender.setItemInHand(InteractionHand.MAIN_HAND, handleItemConsumption(usedStack, sender));
        } else {
            usedStack = sender.getOffhandItem();
            if (usedStack.getItem() == this) {
                sender.setItemInHand(InteractionHand.OFF_HAND, handleItemConsumption(usedStack, sender));
            }
        }
    }

    private Component createClickableMessage(ServerPlayer requester, ServerPlayer target, UUID requestId) {
        MutableComponent acceptBtn = Component.literal("[✔]")
                .withStyle(ChatFormatting.GREEN)
                .withStyle(style -> style
                        .withClickEvent(new ClickEvent(
                                ClickEvent.Action.RUN_COMMAND,
                                "/bountifulbaubles tp_accept " + requestId
                        ))
                        .withHoverEvent(new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                Component.translatable("command.tp_request.accepted").withStyle(ChatFormatting.GREEN)
                        )));

        MutableComponent denyBtn = Component.literal("[✖]")
                .withStyle(ChatFormatting.RED)
                .withStyle(style -> style
                        .withClickEvent(new ClickEvent(
                                ClickEvent.Action.RUN_COMMAND,
                                "/bountifulbaubles tp_deny " + requestId
                        ))
                        .withHoverEvent(new HoverEvent(
                                HoverEvent.Action.SHOW_TEXT,
                                Component.translatable("command.tp_request.denied").withStyle(ChatFormatting.RED)
                        )));

        return Component.translatable("msg.mirror.request_received", requester.getScoreboardName())
                .append(" ") // 添加空格
                .append(acceptBtn) // 添加接受按钮
                .append(" ") // 再添加空格
                .append(denyBtn); // 最后添加拒绝按钮
    }


    private boolean canTeleport(Player player) {
        return !player.getCooldowns().isOnCooldown(this);
    }

    protected boolean checkCooldown(Player player) {
        if (player.getCooldowns().isOnCooldown(this)) {
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.sendSystemMessage(Component.translatable("message.magic_mirror.cooldown"));
            }
            return false;
        }
        return true;
    }

    // 播放传送粒子效果和音效
    public void playTeleportEffects(ServerPlayer player) {
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
        ((ServerLevel) player.level()).sendParticles(ParticleTypes.PORTAL,
                player.getX(), player.getY(), player.getZ(),
                100, 0.5, 0.5, 0.5, 1.0);
    }

    // 传送至重生点逻辑
    private void teleportToSpawn(ServerPlayer player) {
        ServerLevel targetLevel = player.server.getLevel(player.getRespawnDimension());
        BlockPos spawnPos = player.getRespawnPosition();
        float angle = player.getRespawnAngle();

        if (spawnPos == null || targetLevel == null) {
            targetLevel = player.server.overworld();
            spawnPos = targetLevel.getSharedSpawnPos();
        }

        player.teleportTo(targetLevel,
                spawnPos.getX() + 0.5,
                spawnPos.getY(),
                spawnPos.getZ() + 0.5,
                angle,
                0);
        player.fallDistance = 0;
        playTeleportEffects(player);
    }

    // 设置冷却并发送同步包
    private void setCooldown(Player player) {
        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);

        // 服务端发送冷却包给客户端
        if (!player.level().isClientSide && player instanceof ServerPlayer serverPlayer) {
            PacketHandler.INSTANCE.send(
                    PacketDistributor.PLAYER.with(() -> serverPlayer),
                    new PacketHandler.SetCooldownPacket(COOLDOWN_TICKS)
            );
            System.out.println("[SERVER] 发送冷却包给 " + serverPlayer.getName().getString());
        }
    }

    // Curios兼容
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
        return CuriosApi.createCurioProvider(new ICurio() {
            @Override
            public ItemStack getStack() {
                return stack;
            }
        });
    }

    protected ItemStack handleItemConsumption(ItemStack stack, ServerPlayer player) {
        return stack; // 默认实现，不消耗物品
    }

    // 物品描述
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.wormhole_mirror.effect")
                .withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.wormhole_mirror.effect1")
                .withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.wormhole_mirror.description")
                .withStyle(ChatFormatting.GREEN));
    }

}