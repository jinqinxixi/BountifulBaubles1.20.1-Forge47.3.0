
package com.jinqinxixi.bountifulbaubles.item.Baubles;

import com.jinqinxixi.bountifulbaubles.system.modifier.ModifiableBaubleItem;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
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
public class BrokenHeartItem extends ModifiableBaubleItem {
    private static final UUID BROKEN_HEART_UUID = UUID.fromString("BBA1B2C3-D4E5-F6A7-B8C9-D0E1F2A3B4C5");
    private static final String DATA_KEY = "broken_heart_loss";

    private static final ModifiableBaubleItem.Modifier[] MODIFIERS = ModifiableBaubleItem.Modifier.values();

    @Override
    public ModifiableBaubleItem.Modifier[] getModifiers() {
        return MODIFIERS;
    }

    public BrokenHeartItem(Properties properties) {
        super(properties);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag unused) {
        return CuriosApi.createCurioProvider(new ICurio() {
            @Override
            public ItemStack getStack() {
                return stack;
            }



            @Override
            public void curioTick(SlotContext slotContext) {
                LivingEntity entity = slotContext.entity();
                if (entity instanceof Player player && !player.level().isClientSide()) {
                    // 使用缓存值进行脏数据检测
                    AttributeInstance maxHealthAttr = player.getAttribute(Attributes.MAX_HEALTH);
                    if (maxHealthAttr != null) {
                        float currentMax = (float) maxHealthAttr.getValue();
                        float lastMax = player.getPersistentData().getFloat("LastMaxHealth");

                        // 当检测到最大值变化时强制同步
                        if (Math.abs(currentMax - lastMax) > 0.001f) {
                            triggerAttributeSync(player);
                            player.getPersistentData().putFloat("LastMaxHealth", currentMax);
                        }

                        // 生命值上限强制约束
                        if (player.getHealth() > currentMax) {
                            player.setHealth(currentMax);
                        }
                    }

                    // 缩短同步周期到每5tick（0.25秒）
                    if (player.tickCount % 2 == 0) {
                        triggerAttributeSync(player);
                    }
                }
            }

            @Override
            public void onEquip(SlotContext slotContext, ItemStack prevStack) {
                if (slotContext.entity() instanceof Player player) {
                    applyModifier(player, stack); // 父类修饰符
                    triggerAttributeSync(player); // 确保装备时同步
                }
            }

            @Override
            public void onUnequip(SlotContext slotContext, ItemStack newStack) {
                if (slotContext.entity() instanceof Player player) {
                    removeModifier(player, stack); // 父类修饰符
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

    @Override
    public int getEnchantmentValue() {
        return 0; // 附魔等级为0
    }

    // 禁止任何形式的附魔（包括铁砧）
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        // 使用ModList检查而不是类加载检查
        if (ModList.get().isLoaded("firstaid")) return;

        if (event.getEntity() instanceof Player player &&
                !event.isCanceled() &&
                !player.level().isClientSide &&
                isEquipped(player)) {
            handleDamageProtection(player, event);
        }
    }


    private static void handleDamageProtection(Player player, LivingHurtEvent event) {
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

                if (player instanceof ServerPlayer serverPlayer) {
                    // 播放铁傀儡受伤的声音
                    serverPlayer.level().playSound(null,
                            serverPlayer.getX(),
                            serverPlayer.getY(),
                            serverPlayer.getZ(),
                            SoundEvents.IRON_GOLEM_HURT, // 铁傀儡受伤音效
                            SoundSource.PLAYERS,
                            1.0F,  // 音量
                            1.0F   // 音高
                    );
                }
                triggerAttributeSync(player);
            }
        }
    }

    private static void updateHealthModifier(Player player, AttributeInstance maxHealthAttr, float totalLoss) {
        double newValue = -totalLoss;
        boolean needsUpdate = true;

        // 检查现有修饰器是否存在变化
        if (maxHealthAttr.getModifier(BROKEN_HEART_UUID) != null) {
            double currentValue = maxHealthAttr.getModifier(BROKEN_HEART_UUID).getAmount();
            needsUpdate = Math.abs(currentValue - newValue) > 0.001;
        }

        if (needsUpdate) {
            maxHealthAttr.removeModifier(BROKEN_HEART_UUID);
            maxHealthAttr.addPermanentModifier(new AttributeModifier(

                    BROKEN_HEART_UUID,
                    "broken_heart_penalty",
                    newValue,
                    AttributeModifier.Operation.ADDITION
            ));

            // 直接更新缓存值
            player.getPersistentData().putFloat("LastMaxHealth", (float) maxHealthAttr.getValue());
            triggerAttributeSync(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerWakeUp(PlayerWakeUpEvent event) {
        Player player = event.getEntity();
        // 使用ModList检查
        if (ModList.get().isLoaded("firstaid")) {
            if (player.getAttribute(Attributes.MAX_HEALTH).getModifier(BROKEN_HEART_UUID) != null) {
                player.getAttribute(Attributes.MAX_HEALTH).removeModifier(BROKEN_HEART_UUID);
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
            // 获取玩家当前的基础最大生命值（包含其他饰品的加成）
            AttributeInstance maxHealthAttr = player.getAttribute(Attributes.MAX_HEALTH);
            if (maxHealthAttr != null) {
                // 先移除破碎之心的减益效果，以获得真实的最大生命值
                maxHealthAttr.removeModifier(BROKEN_HEART_UUID);
                double baseMaxHealth = maxHealthAttr.getValue();

                float recovery = Math.min(totalLoss, 4.0F);
                totalLoss -= recovery;
                data.putFloat(DATA_KEY, totalLoss);

                // 如果还有剩余减益，重新应用
                if (totalLoss > 0) {
                    maxHealthAttr.addPermanentModifier(new AttributeModifier(
                            BROKEN_HEART_UUID,
                            "broken_heart_penalty",
                            -totalLoss,
                            AttributeModifier.Operation.ADDITION
                    ));
                }

                // 确保生命值不超过当前的最大值
                float newMaxHealth = (float) maxHealthAttr.getValue();
                float currentHealth = player.getHealth();
                // 如果当前生命值低于恢复后的最大值，则增加生命值
                if (currentHealth < newMaxHealth) {
                    player.setHealth(Math.min(currentHealth + recovery, newMaxHealth));
                }

                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.sendSystemMessage(
                            Component.literal("Restored " + recovery + " max health")
                                    .withStyle(ChatFormatting.GREEN),
                            false
                    );

                    // 添加当前最大生命值的提示
                    serverPlayer.sendSystemMessage(
                            Component.literal("Current max health: " + String.format("%.1f", newMaxHealth))
                                    .withStyle(ChatFormatting.GRAY),
                            false
                    );

                    // 播放恢复音效
                    player.level().playSound(null, player.blockPosition(),
                            SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.5F, 1.2F);
                }

                // 确保客户端同步
                triggerAttributeSync(player);
            }
        }
    }

    public static boolean isEquipped(LivingEntity entity) {
        return CuriosApi.getCuriosInventory(entity).resolve()
                .flatMap(curios -> curios.findFirstCurio(stack ->
                        stack.getItem() instanceof BrokenHeartItem))
                .isPresent();
    }



    private static void triggerAttributeSync(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            // 正确的属性包构造方式
            List<AttributeInstance> attributes = List.of(serverPlayer.getAttribute(Attributes.MAX_HEALTH));
            serverPlayer.connection.send(new ClientboundUpdateAttributesPacket(
                    serverPlayer.getId(),
                    attributes
            ));
            // 保持原有临时修饰器方法作为备用
            AttributeModifier tempMod = new AttributeModifier(UUID.randomUUID(),
                    "temp_sync", 0.0, AttributeModifier.Operation.ADDITION);
            AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
            if (maxHealth != null) {
                maxHealth.addTransientModifier(tempMod);
                maxHealth.removeModifier(tempMod);
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.broken_heart.effect")
                .withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.broken_heart.effect1")
                .withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.broken_heart.lore")
                .withStyle(ChatFormatting.GREEN));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}