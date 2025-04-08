package com.jinqinxixi.bountifulbaubles.item.Baubles;

import com.jinqinxixi.bountifulbaubles.config.ModConfig;
import com.jinqinxixi.bountifulbaubles.system.modifier.ModifiableBaubleItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DarkEggItem extends ModifiableBaubleItem {
    private static final String TAG_IS_INITIALIZED = "IsInitialized";
    private static final String COOLDOWN_KEY = "DarkEggCooldown";
    public static final String DARK_EGG_CONTROLLED_TAG = "DarkEggControlled";
    public static final String FRIENDLY_VEX_TAG = "FriendlyVex";

    private static final ModifiableBaubleItem.Modifier[] MODIFIERS = ModifiableBaubleItem.Modifier.values();

    public DarkEggItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public ModifiableBaubleItem.Modifier[] getModifiers() {
        return MODIFIERS;
    }


    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (slotContext.entity() instanceof Player player) {
            CompoundTag tag = stack.getOrCreateTag();
            // 使用游戏时间来检查冷却，而不是修改 NBT
            long currentTime = player.level().getGameTime();
            if (tag.contains(COOLDOWN_KEY)) {
                long cooldownEnd = tag.getLong(COOLDOWN_KEY);
                if (currentTime >= cooldownEnd) {
                    tag.remove(COOLDOWN_KEY);
                }
            }

            if (!player.level().isClientSide) {
                // 控制范围内的友好恼鬼
                AABB searchBox = player.getBoundingBox().inflate(32.0D);
                player.level().getEntitiesOfClass(Vex.class, searchBox).forEach(vex -> {
                    if (vex.getPersistentData().getBoolean(FRIENDLY_VEX_TAG)) {
                        if (vex.getTarget() == null || vex.getTarget().isDeadOrDying()) {
                            followPlayer(vex, player);
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        // 调用父类的onEquip方法，它会处理修饰符的比较和应用
        super.onEquip(slotContext, prevStack, stack);

        // 只处理首次装备的初始化
        if (!stack.getOrCreateTag().getBoolean(TAG_IS_INITIALIZED)) {
            stack.getOrCreateTag().putBoolean(TAG_IS_INITIALIZED, true);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        // 调用父类的onUnequip方法，它会处理修饰符的移除
        super.onUnequip(slotContext, newStack, stack);
    }


    private static void followPlayer(Vex vex, Player player) {
        double followDistance = vex.distanceToSqr(player);

        // 检查周围是否有敌对目标
        AABB searchBox = vex.getBoundingBox().inflate(16.0D);
        List<LivingEntity> nearbyEntities = vex.level().getEntitiesOfClass(LivingEntity.class, searchBox,
                entity -> entity != player &&
                        entity != vex &&
                        !(entity instanceof Player) && // 排除所有玩家
                        !entity.getPersistentData().getBoolean(FRIENDLY_VEX_TAG) &&
                        entity.isAlive() &&
                        (entity.getLastHurtByMob() == player || // 伤害过玩家的实体
                                entity instanceof Vex && !((Vex)entity).getPersistentData().getBoolean(FRIENDLY_VEX_TAG))); // 敌对恼鬼

        // 如果找到敌对目标，主动攻击
        if (!nearbyEntities.isEmpty()) {
            LivingEntity nearestTarget = nearbyEntities.get(0);
            double nearestDistance = vex.distanceToSqr(nearestTarget);

            for (LivingEntity entity : nearbyEntities) {
                double distance = vex.distanceToSqr(entity);
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearestTarget = entity;
                }
            }

            vex.setTarget(nearestTarget);
            // 如果目标较远，快速接近
            if (nearestDistance > 64) {
                double targetX = nearestTarget.getX() + (vex.getRandom().nextDouble() - 0.5D) * 4.0D;
                double targetY = nearestTarget.getY() + 1.5D;
                double targetZ = nearestTarget.getZ() + (vex.getRandom().nextDouble() - 0.5D) * 4.0D;
                vex.getMoveControl().setWantedPosition(targetX, targetY, targetZ, 1.2D);
            }
            return;
        }

        // 如果没有目标，则跟随玩家
        if (followDistance > 256) { // 16格以上
            // 如果太远就传送到玩家周围8-12格范围内的随机位置
            double angle = vex.getRandom().nextDouble() * Math.PI * 2;
            double radius = 8.0D + vex.getRandom().nextDouble() * 4.0D;
            double offsetX = Math.cos(angle) * radius;
            double offsetZ = Math.sin(angle) * radius;
            double offsetY = 2.0D + vex.getRandom().nextDouble() * 2.0D;

            vex.moveTo(
                    player.getX() + offsetX,
                    player.getY() + offsetY,
                    player.getZ() + offsetZ
            );
        } else if (followDistance > 64) { // 8格以上
            double angle = Math.atan2(player.getZ() - vex.getZ(), player.getX() - vex.getX());
            double targetRadius = 6.0D;
            double targetX = player.getX() + Math.cos(angle) * targetRadius;
            double targetZ = player.getZ() + Math.sin(angle) * targetRadius;
            double targetY = player.getY() + 2.0D + vex.getRandom().nextDouble() * 2.0D;

            vex.getMoveControl().setWantedPosition(targetX, targetY, targetZ, 0.8D);
        }
    }

    private static void setVexFriendly(Vex vex, Player player) {
        vex.setPersistenceRequired();
        vex.setCustomName(Component.literal(player.getName().getString() + "'s Vex"));
        vex.setCustomNameVisible(true);

        // 增加恼鬼的移动速度和攻击欲望
        vex.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED)
                .setBaseValue(0.5D); // 提高移动速度
        vex.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.FOLLOW_RANGE)
                .setBaseValue(48.0D); // 增加追踪范围
        vex.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE)
                .setBaseValue(6.0D); // 增加攻击力

        CompoundTag data = vex.getPersistentData();
        data.putBoolean(DARK_EGG_CONTROLLED_TAG, true);
        data.putBoolean(FRIENDLY_VEX_TAG, true);
        data.putString("OwnerUUID", player.getStringUUID());

        vex.setLimitedLife(ModConfig.getDarkEggVexLifetime());
        vex.addTag("dark_egg_controlled");
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        // 当玩家被攻击时
        if (event.getEntity() instanceof Player player) {
            // 检查玩家是否装备了暗黑之蛋
            CuriosApi.getCuriosInventory(player).ifPresent(handler -> {
                handler.findFirstCurio(item -> item.getItem() instanceof DarkEggItem).ifPresent(result -> {
                    ItemStack stack = result.stack();
                    CompoundTag tag = stack.getOrCreateTag();

                    // 检查是否在冷却中
                    if (!tag.contains(COOLDOWN_KEY) && player.level() instanceof ServerLevel serverLevel) {
                        // 设置冷却时间
                        tag.putLong(COOLDOWN_KEY, player.level().getGameTime() + ModConfig.getDarkEggCooldown());

                        // 播放召唤音效
                        serverLevel.playSound(null,
                                player.getX(), player.getY(), player.getZ(),
                                SoundEvents.EVOKER_CAST_SPELL,
                                SoundSource.PLAYERS,
                                1.0F,
                                1.0F);

                        // 获取攻击者，但排除玩家
                        LivingEntity attacker = null;
                        if (event.getSource().getEntity() instanceof LivingEntity &&
                                !(event.getSource().getEntity() instanceof Player)) {
                            attacker = (LivingEntity)event.getSource().getEntity();
                        }

                        final LivingEntity finalAttacker = attacker;

                        // 召唤恼鬼
                        for (int i = 0; i < ModConfig.getDarkEggVexCount(); i++) {
                            // 在玩家周围8-12格范围内随机生成
                            double angle = Math.PI * 2 * i / ModConfig.getDarkEggVexCount();
                            double radius = 8.0D + player.getRandom().nextDouble() * 4.0D;
                            double offsetX = Math.cos(angle) * radius;
                            double offsetZ = Math.sin(angle) * radius;
                            double offsetY = 2.0D + player.getRandom().nextDouble() * 2.0D;

                            BlockPos spawnPos = new BlockPos(
                                    (int)(player.getX() + offsetX),
                                    (int)(player.getY() + offsetY),
                                    (int)(player.getZ() + offsetZ)
                            );

                            Vex vex = EntityType.VEX.spawn(
                                    serverLevel,
                                    null,
                                    player,
                                    spawnPos,
                                    MobSpawnType.MOB_SUMMONED,
                                    true,
                                    false
                            );

                            if (vex != null) {
                                setVexFriendly(vex, player);

                                // 如果有攻击者，立即设置为目标
                                if (finalAttacker != null && finalAttacker != player) {
                                    vex.setTarget(finalAttacker);
                                }
                            }
                        }
                    } else {
                        // 即使在冷却中，也让现有的恼鬼攻击攻击者（排除玩家）
                        if (event.getSource().getEntity() instanceof LivingEntity attacker &&
                                attacker != player &&
                                !(attacker instanceof Player)) {
                            AABB searchBox = player.getBoundingBox().inflate(32.0D);
                            player.level().getEntitiesOfClass(Vex.class, searchBox).forEach(vex -> {
                                if (vex.getPersistentData().getBoolean(FRIENDLY_VEX_TAG) &&
                                        vex.getPersistentData().getString("OwnerUUID").equals(player.getStringUUID())) {
                                    vex.setTarget(attacker);
                                }
                            });
                        }
                    }
                });
            });
        }
    }

    // 当玩家攻击目标时，让恼鬼也攻击相同目标
    @SubscribeEvent
    public static void onPlayerAttack(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            LivingEntity target = event.getEntity();
            // 确保目标不是玩家
            if (target != null && !(target instanceof Player)) {
                AABB searchBox = player.getBoundingBox().inflate(32.0D);
                player.level().getEntitiesOfClass(Vex.class, searchBox).forEach(vex -> {
                    if (vex.getPersistentData().getBoolean(FRIENDLY_VEX_TAG) &&
                            vex.getPersistentData().getString("OwnerUUID").equals(player.getStringUUID())) {
                        vex.setTarget(target);
                    }
                });
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.darkegg.effects",
                        ModConfig.getDarkEggVexCount(),
                        ModConfig.getDarkEggCooldown() / 20)  // 转换为秒
                .withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.darkegg.description",
                        ModConfig.getDarkEggVexLifetime() / 20)  // 转换为秒
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