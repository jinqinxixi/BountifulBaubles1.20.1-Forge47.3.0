package com.jinqinxixi.bountifulbaubles.item.Baubles;

import com.jinqinxixi.bountifulbaubles.system.modifier.ModifiableBaubleItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber
public class WarmVoidItem extends ModifiableBaubleItem {

    public WarmVoidItem(Properties properties) {
        super(properties);
    }

    private static final ModifiableBaubleItem.Modifier[] MODIFIERS = ModifiableBaubleItem.Modifier.values();

    @Override
    public ModifiableBaubleItem.Modifier[] getModifiers() {
        return MODIFIERS;
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag unused) {
        return CuriosApi.createCurioProvider(new ICurio() {
            @Override
            public ItemStack getStack() {
                return stack;
            }

            @Override
            public void onEquip(SlotContext slotContext, ItemStack prevStack) {
                if (slotContext.entity() instanceof Player player) {
                    applyModifier(player, stack);
                }
            }

            @Override
            public void onUnequip(SlotContext slotContext, ItemStack newStack) {
                if (slotContext.entity() instanceof Player player) {
                    removeModifier(player, stack);
                }
            }

            @Override
            public boolean canEquip(SlotContext slotContext) {
                return true;
            }

            @Override
            public boolean canUnequip(SlotContext slotContext) {
                return true;
            }
        });
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof Player player && !player.level().isClientSide) {
            // 检查是否是虚空伤害
            if (event.getSource().type().msgId().equals("outOfWorld")) {
                // 检查玩家是否装备了虚空庇护饰品
                boolean hasWarmVoidItem = CuriosApi.getCuriosInventory(player)
                        .resolve()
                        .flatMap(handler -> handler.findFirstCurio(item -> item.getItem() instanceof WarmVoidItem))
                        .isPresent();

                if (hasWarmVoidItem) {
                    event.setCanceled(true); // 取消虚空伤害

                    if (player instanceof ServerPlayer serverPlayer) {
                        ServerLevel serverLevel = serverPlayer.serverLevel();
                        BlockPos spawnPos = serverPlayer.getRespawnPosition();
                        float spawnAngle = serverPlayer.getRespawnAngle();

                        // 如果没有重生点，使用世界出生点
                        if (spawnPos == null) {
                            spawnPos = serverLevel.getSharedSpawnPos();
                            spawnAngle = 0.0F;
                        }

                        // 确保重生点是有效的
                        if (spawnPos != null) {
                            // 播放传送音效
                            serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                                    SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);

                            // 传送玩家
                            player.teleportTo(spawnPos.getX() + 0.5D, spawnPos.getY() + 0.1D, spawnPos.getZ() + 0.5D);

                            // 重置玩家的速度
                            player.setDeltaMovement(0, 0, 0);
                            player.fallDistance = 0;

                            // 播放到达音效
                            serverLevel.playSound(null, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(),
                                    SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);

                            // 发送消息给玩家
                            player.sendSystemMessage(Component.translatable("message.bountifulbaubles.warm_void.teleport")
                                    .withStyle(ChatFormatting.LIGHT_PURPLE));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.warm_void.effects")
                .withStyle(ChatFormatting.BLUE));
        super.appendHoverText(stack, level, tooltip, flag);
    }
    // 1. 禁止铁砧/指令附魔
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    // 2. 附魔等级设为0（防止附魔台操作）
    @Override
    public int getEnchantmentValue() {
        return 0;
    }
}