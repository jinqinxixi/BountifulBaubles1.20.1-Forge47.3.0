
package com.jinqinxixi.bountifulbaubles.item.Baubles;

import com.jinqinxixi.bountifulbaubles.system.modifier.ModifiableBaubleItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber
public class PhylacteryCharmItem extends ModifiableBaubleItem {
    private static final UUID PHYLACTERY_UUID = UUID.fromString("bba5e6f7-b8c9-d0e1-f2a3-b4c5d6e7f8a9");
    private static final String DATA_KEY = "phylactery_loss";
    private static final float TELEPORT_COST = 6.0F;
    private static final int COOLDOWN_TICKS = 0;
    private static final int USE_DURATION = 1;

    private static final ModifiableBaubleItem.Modifier[] MODIFIERS = ModifiableBaubleItem.Modifier.values();
    @Override
    public ModifiableBaubleItem.Modifier[] getModifiers() {
        return MODIFIERS;
    }

    public PhylacteryCharmItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!player.getCooldowns().isOnCooldown(this)) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }
        return InteractionResultHolder.fail(stack);
    }
    @Override
    public int getEnchantmentValue() {
        return 0; // 附魔等级为0
    }

    // 禁止任何形式的附魔（包括铁砧）
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof ServerPlayer player) {
            player.hurt(player.damageSources().magic(), TELEPORT_COST);
            performTeleport(player);
            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        }
        return stack;
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        // 使用 ModList 检查，如果 FirstAid 已加载则返回
        if (ModList.get().isLoaded("firstaid")) return;

        if (event.getEntity() instanceof ServerPlayer player &&
                !event.isCanceled() &&
                isEquipped(player)) {
            handleDamageProtection(player, event);
        }
    }

    private static void handleDamageProtection(ServerPlayer player, LivingHurtEvent event) {
        float effectiveHealth = player.getHealth() + player.getAbsorptionAmount();
        float damageAmount = event.getAmount();

        if (effectiveHealth <= damageAmount) {
            float overflowDamage = damageAmount - effectiveHealth;
            float adjustedDamage = overflowDamage <= 1.0F ? 1.0F : overflowDamage;

            AttributeInstance maxHealthAttr = player.getAttribute(Attributes.MAX_HEALTH);
            if (maxHealthAttr == null) return;

            float currentMax = (float) maxHealthAttr.getValue();

            if (currentMax > adjustedDamage) {
                event.setCanceled(true);
                player.setAbsorptionAmount(0);
                player.setHealth(1.0F);

                CompoundTag data = player.getPersistentData();
                float totalLoss = data.getFloat(DATA_KEY);
                totalLoss += adjustedDamage;
                data.putFloat(DATA_KEY, totalLoss);

                updateHealthModifier(player, maxHealthAttr, totalLoss);
                performTeleport(player);
                triggerAttributeSync(player);
            }
        }
    }

    private static void updateHealthModifier(Player player, AttributeInstance maxHealthAttr, float totalLoss) {
        maxHealthAttr.removeModifier(PHYLACTERY_UUID);
        maxHealthAttr.addPermanentModifier(new AttributeModifier(
                PHYLACTERY_UUID,
                "phylactery_penalty",
                -totalLoss,
                AttributeModifier.Operation.ADDITION
        ));
    }

    public static void performTeleport(ServerPlayer player) {
        ServerLevel targetLevel = player.server.getLevel(player.getRespawnDimension());
        BlockPos spawnPos = player.getRespawnPosition();

        if (spawnPos == null || targetLevel == null) {
            targetLevel = player.server.overworld();
            spawnPos = targetLevel.getSharedSpawnPos();
        }

        player.teleportTo(
                targetLevel,
                spawnPos.getX() + 0.5,
                spawnPos.getY() + 0.1,
                spawnPos.getZ() + 0.5,
                player.getYRot(),
                player.getXRot()

        );
        player.fallDistance = 0;
        triggerAttributeSync(player);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 0.8F);
        ((ServerLevel) player.level()).sendParticles(ParticleTypes.PORTAL,
                player.getX(), player.getY(), player.getZ(),
                100, 0.5, 0.5, 0.5, 0.1);

    }

    @SubscribeEvent
    public static void onPlayerWakeUp(PlayerWakeUpEvent event) {
        Player player = event.getEntity();
        // 使用 ModList 检查
        if (ModList.get().isLoaded("firstaid")) {
            if (player.getAttribute(Attributes.MAX_HEALTH).getModifier(PHYLACTERY_UUID) != null) {
                player.getAttribute(Attributes.MAX_HEALTH).removeModifier(PHYLACTERY_UUID);
            }
            return;
        }

        if (!player.level().isClientSide &&
                player.isSleepingLongEnough() &&
                isEquipped(player)) {
            handleHealthRecovery(player);
            triggerAttributeSync(player);
        }
    }

    private static void handleHealthRecovery(Player player) {
        CompoundTag data = player.getPersistentData();
        float totalLoss = data.getFloat(DATA_KEY);

        if (totalLoss > 0) {
            float recovery = Math.min(totalLoss, 4.0F);
            totalLoss -= recovery;
            data.putFloat(DATA_KEY, totalLoss);

            AttributeInstance maxHealthAttr = player.getAttribute(Attributes.MAX_HEALTH);
            if (maxHealthAttr != null) {
                maxHealthAttr.removeModifier(PHYLACTERY_UUID);
                if (totalLoss > 0) {
                    maxHealthAttr.addPermanentModifier(new AttributeModifier(
                            PHYLACTERY_UUID,
                            "phylactery_penalty",
                            -totalLoss,
                            AttributeModifier.Operation.ADDITION
                    ));
                }

                player.setHealth(Math.min(player.getHealth(), (float) maxHealthAttr.getValue()));
                triggerAttributeSync(player);

                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.sendSystemMessage(
                            Component.literal("Restored " + recovery + " max health")
                                    .withStyle(ChatFormatting.GREEN),
                            false
                    );
                }
            }
        }
    }

    public static boolean isEquipped(LivingEntity entity) {
        return CuriosApi.getCuriosInventory(entity).resolve()
                .flatMap(curios -> curios.findFirstCurio(stack ->
                        stack.getItem() instanceof PhylacteryCharmItem))
                .isPresent();
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return USE_DURATION;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.TOOT_HORN;
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
        return CuriosApi.createCurioProvider(new ICurio() {
            @Override
            public ItemStack getStack() {
                return stack;
            }



            @Override
            public void onEquip(SlotContext slotContext, ItemStack prevStack) {
                if (slotContext.entity() instanceof Player player) {
                    applyModifier(player, stack);  // 父类属性
                }
            }

            @Override
            public void onUnequip(SlotContext slotContext, ItemStack newStack) {
                if (slotContext.entity() instanceof Player player) {
                    removeModifier(player, stack); // 父类属性
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

            // 修改后的 ICurio 实现部分
            @Override
            public void curioTick(SlotContext slotContext) {
                if (slotContext.entity() instanceof Player player) {
                    // 移除冷却
                    player.getCooldowns().removeCooldown(PhylacteryCharmItem.this);

                    // 仅服务端执行生命值检查
                    if (!player.level().isClientSide()) {
                        float maxHealth = player.getMaxHealth();
                        float currentHealth = player.getHealth();

                        // 强制执行健康上限
                        if (currentHealth > maxHealth) {
                            player.setHealth(maxHealth);
                            if (player.tickCount % 2 == 0) {
                                triggerAttributeSync(player);
                            }
                        }
                    }
                }
            }
        });
    }

    // 修改同步方法
    private static void triggerAttributeSync(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            // 创建属性实例集合
            List<AttributeInstance> attributes = List.of(
                    serverPlayer.getAttribute(Attributes.MAX_HEALTH)
            );

            // 发送属性更新包
            serverPlayer.connection.send(
                    new ClientboundUpdateAttributesPacket(
                            serverPlayer.getId(),
                            attributes
                    )
            );
        }
    }
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.phylactery_charm.effect")
                .withStyle(ChatFormatting.BLUE));
        super.appendHoverText(stack, level, tooltip, flag); // 调用基类方法显示修饰符信息
    }
}
