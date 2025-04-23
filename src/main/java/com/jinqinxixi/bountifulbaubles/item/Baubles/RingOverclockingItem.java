package com.jinqinxixi.bountifulbaubles.item.Baubles;

import com.jinqinxixi.bountifulbaubles.system.modifier.ModifiableBaubleItem;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber
public class RingOverclockingItem extends ModifiableBaubleItem {
    // 移速加成值
    private static final double SPEED_BOOST = 0.07; // 7% 移速增加
    private static final String SPEED_UUID_KEY = "SpeedUUID";

    private static final ModifiableBaubleItem.Modifier[] MODIFIERS = ModifiableBaubleItem.Modifier.values();

    @Override
    public ModifiableBaubleItem.Modifier[] getModifiers() {
        return MODIFIERS;
    }

    public RingOverclockingItem(Properties properties) {
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
            public List<Component> getAttributesTooltip(List<Component> tooltips) {
                return Collections.emptyList(); // 在父类中统一隐藏属性提示
            }

            @Override
            public void onEquip(SlotContext slotContext, ItemStack prevStack) {
                LivingEntity entity = slotContext.entity();
                if (entity instanceof Player player) {
                    initializeSpeedUUID(stack);  // 初始化UUID
                    applyModifier(player, stack);   // 父类属性
                    applySpeedModifier(player, stack); // 子类移速
                    clearSlownessEffect(player);
                }
            }

            @Override
            public void onUnequip(SlotContext slotContext, ItemStack newStack) {
                LivingEntity entity = slotContext.entity();
                if (entity instanceof Player player) {
                    removeModifier(player, stack);  // 父类属性
                    removeSpeedModifier(player, stack); // 子类移速
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

    // ===== 新增核心方法 =====
    private static void initializeSpeedUUID(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.hasUUID(SPEED_UUID_KEY)) {
            tag.putUUID(SPEED_UUID_KEY, UUID.randomUUID());
        }
    }

    private static void applySpeedModifier(Player player, ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.hasUUID(SPEED_UUID_KEY)) {
            UUID speedUUID = tag.getUUID(SPEED_UUID_KEY);
            AttributeModifier existing = player.getAttribute(Attributes.MOVEMENT_SPEED).getModifier(speedUUID);

            // 如果不存在或值不同则更新
            if (existing == null || existing.getAmount() != SPEED_BOOST) {
                player.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(speedUUID);
                AttributeModifier modifier = new AttributeModifier(
                        speedUUID,
                        "RingOverclockingSpeedBoost",
                        SPEED_BOOST,
                        AttributeModifier.Operation.MULTIPLY_TOTAL
                );
                player.getAttribute(Attributes.MOVEMENT_SPEED).addTransientModifier(modifier);
            }
        }
    }

    private static void removeSpeedModifier(Player player, ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.hasUUID(SPEED_UUID_KEY)) {
            UUID speedUUID = tag.getUUID(SPEED_UUID_KEY);
            player.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(speedUUID);
        }
    }
    // ===== 核心方法结束 =====

    // 其他方法保持原有逻辑不变...
    @Override
    public int getEnchantmentValue() {
        return 0;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        if (event.getEntity() instanceof Player player) {
            MobEffectInstance effect = event.getEffectInstance();
            if (effect != null && effect.getEffect() == MobEffects.MOVEMENT_SLOWDOWN) {
                CuriosApi.getCuriosInventory(player).ifPresent(curios -> {
                    if (curios.findFirstCurio(stack ->
                            stack.getItem() instanceof RingOverclockingItem).isPresent()) {
                        event.setResult(net.minecraftforge.eventbus.api.Event.Result.DENY);
                        clearSlownessEffect(player);
                    }
                });
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(LivingEvent.LivingTickEvent event) {
        if (event.getEntity() instanceof Player player) {
            CuriosApi.getCuriosInventory(player).ifPresent(curios -> {
                if (curios.findFirstCurio(stack ->
                        stack.getItem() instanceof RingOverclockingItem).isPresent()) {
                    clearSlownessEffect(player);
                }
            });
        }
    }

    private static void clearSlownessEffect(Player player) {
        player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.ring_overclocking.effect")
                .withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.ring_overclocking.description")
                .withStyle(ChatFormatting.GREEN));
        super.appendHoverText(stack, level, tooltip, flag); // 调用基类方法显示修饰符信息
    }
}
