package com.jinqinxixi.bountifulbaubles.item.Baubles;

import com.jinqinxixi.bountifulbaubles.config.ModConfig;
import com.jinqinxixi.bountifulbaubles.system.modifier.ModifiableBaubleItem;
import com.jinqinxixi.bountifulbaubles.network.packet.MarkTargetPacket;
import com.jinqinxixi.bountifulbaubles.network.handler.NetworkHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nullable;
import java.util.*;

@Mod.EventBusSubscriber
public class MindsEyeItem extends ModifiableBaubleItem {
    // 存储标记的目标
    private static final Map<UUID, Map.Entry<Integer, Long>> MARKED_TARGETS = new HashMap<>();
    // 存储是否需要立即重新标记
    private static final Map<UUID, Boolean> SHOULD_REMARK = new HashMap<>();
    // 存储冷却时间
    private static final Map<UUID, Long> SCAN_COOLDOWNS = new HashMap<>();

    // 添加getter方法供渲染器使用
    public static Map<UUID, Map.Entry<Integer, Long>> getMarkedTargets() {
        return MARKED_TARGETS;
    }

    public MindsEyeItem(Properties properties) {
        super(properties);
    }

    // 设置可用的修饰符
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
                    clearMarkedTarget(player); // 确保在脱下时清除标记
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
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) {
            return;
        }

        Player player = event.player;
        UUID playerUUID = player.getUUID();

        boolean hasGoggles = CuriosApi.getCuriosInventory(player).resolve()
                .flatMap(handler -> handler.findFirstCurio(item -> item.getItem() instanceof MindsEyeItem))
                .isPresent();

        if (!hasGoggles) {
            clearMarkedTarget(player);
            SHOULD_REMARK.remove(playerUUID);
            return;
        }

        long currentTime = player.level().getGameTime();

        // 检查是否应该立即重新标记
        if (SHOULD_REMARK.getOrDefault(playerUUID, false)) {
            scanAndMarkTarget(player);
            SHOULD_REMARK.put(playerUUID, false);
            return;
        }

        // 检查是否在冷却中
        long nextScanTime = SCAN_COOLDOWNS.getOrDefault(playerUUID, 0L);
        if (currentTime >= nextScanTime) {
            scanAndMarkTarget(player);
            // 更新下一次扫描时间
            SCAN_COOLDOWNS.put(playerUUID, currentTime + ModConfig.getTacticalScanInterval());

        }

        updateMarkedTargets(player);
    }

    private static void scanAndMarkTarget(Player player) {
        List<Mob> nearbyMobs = player.level().getEntitiesOfClass(
                Mob.class,
                new AABB(
                        player.position().subtract(ModConfig.getTacticalScanRadius(),
                                ModConfig.getTacticalDownRange(),
                                ModConfig.getTacticalScanRadius()),
                        player.position().add(ModConfig.getTacticalScanRadius(),
                                ModConfig.getTacticalUpRange(),
                                ModConfig.getTacticalScanRadius())
                ),
                mob -> {
                    if (!mob.isAlive() || mob.isInvisible()) {
                        return false;
                    }

                    boolean isHostile = mob instanceof Enemy;
                    if (!isHostile) {
                        return false;
                    }

                    // 确保不会重复标记同一个目标
                    if (MARKED_TARGETS.containsKey(player.getUUID()) &&
                            MARKED_TARGETS.get(player.getUUID()).getKey() == mob.getId()) {
                        return false;
                    }

                    double dx = mob.getX() - player.getX();
                    double dz = mob.getZ() - player.getZ();
                    double horizontalDistSqr = dx * dx + dz * dz;

                    if (horizontalDistSqr > ModConfig.getTacticalScanRadius() * ModConfig.getTacticalScanRadius()) {
                        return false;
                    }

                    return player.level().clip(new ClipContext(
                            player.getEyePosition(),
                            mob.getEyePosition(),
                            ClipContext.Block.COLLIDER,
                            ClipContext.Fluid.NONE,
                            player
                    )).getType() == HitResult.Type.MISS;
                }
        );

        if (!nearbyMobs.isEmpty()) {
            clearMarkedTarget(player);

            Mob target = nearbyMobs.get(player.getRandom().nextInt(nearbyMobs.size()));
            long endTime = player.level().getGameTime() + ModConfig.getTacticalMarkDuration();

            // 更新本地状态
            MARKED_TARGETS.put(player.getUUID(),
                    new AbstractMap.SimpleEntry<>(target.getId(), endTime));

            // 发送网络同步包
            sendMarkPacket(player, target.getId(), endTime);

            player.level().playSound(null,
                    target.getX(), target.getY(), target.getZ(),
                    SoundEvents.ARROW_HIT_PLAYER,
                    net.minecraft.sounds.SoundSource.PLAYERS,
                    1.0F,
                    2.0F
            );


        } else if (SHOULD_REMARK.getOrDefault(player.getUUID(), false)) {
            SHOULD_REMARK.put(player.getUUID(), false);
            // 如果没找到新目标，仍然设置冷却
            SCAN_COOLDOWNS.put(player.getUUID(), player.level().getGameTime() + ModConfig.getTacticalScanInterval());
        }
    }

    private static void updateMarkedTargets(Player player) {
        Map.Entry<Integer, Long> targetInfo = MARKED_TARGETS.get(player.getUUID());
        if (targetInfo != null) {
            // 检查标记是否过期
            if (player.level().getGameTime() > targetInfo.getValue()) {
                clearMarkedTarget(player);
            }
        }
    }

    private static void clearMarkedTarget(Player player) {
        UUID playerUUID = player.getUUID();
        Map.Entry<Integer, Long> targetInfo = MARKED_TARGETS.get(playerUUID);
        if (targetInfo != null) {
            MARKED_TARGETS.remove(playerUUID);
            SHOULD_REMARK.remove(playerUUID);
            // 发送清除标记的数据包
            if (!player.level().isClientSide) {
                NetworkHandler.CHANNEL.send(
                        PacketDistributor.ALL.noArg(),
                        new MarkTargetPacket(
                                playerUUID,
                                targetInfo.getKey(),
                                0,
                                true
                        )
                );
            }
        }
    }


    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            Map.Entry<Integer, Long> targetInfo = MARKED_TARGETS.get(player.getUUID());
            if (targetInfo != null && event.getEntity().getId() == targetInfo.getKey()) {
                event.setAmount(event.getAmount() * (float)ModConfig.getTacticalDamageMultiplier());
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            UUID playerUUID = player.getUUID();
            Map.Entry<Integer, Long> targetInfo = MARKED_TARGETS.get(playerUUID);
            if (targetInfo != null && event.getEntity().getId() == targetInfo.getKey()) {
                SHOULD_REMARK.put(playerUUID, true);
                // 击杀目标后移除冷却
                SCAN_COOLDOWNS.remove(playerUUID);
            }
        }
    }

    private static void sendMarkPacket(Player player, int entityId, long endTime) {
        if (!player.level().isClientSide) {
            NetworkHandler.CHANNEL.send(
                    PacketDistributor.ALL.noArg(),
                    new MarkTargetPacket(
                            player.getUUID(),
                            entityId,
                            endTime,
                            false
                    )
            );
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.minds_eye.effects",
                        ModConfig.TACTICAL_SCAN_INTERVAL.get() / 20.0f)
                .withStyle(ChatFormatting.BLUE));

        tooltip.add(Component.translatable("tooltip.bountifulbaubles.minds_eye.effects1",
                        (ModConfig.TACTICAL_DAMAGE_MULTIPLIER.get() - 1.0) * 100)
                .withStyle(ChatFormatting.BLUE));

        tooltip.add(Component.translatable("tooltip.bountifulbaubles.minds_eye.effects2")
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