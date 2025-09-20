package com.jinqinxixi.bountifulbaubles.item.Baubles;

import com.jinqinxixi.bountifulbaubles.system.modifier.ModifiableBaubleItem;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber
public class CuriousRingItem extends ModifiableBaubleItem {

    // NBT存储键
    private static final String SPEED_UUID_KEY = "RingSpeedUUID";
    private static final String ATTACK_SPEED_UUID_KEY = "RingAttackSpeedUUID";
    private static final String ARMOR_UUID_KEY = "RingArmorUUID";

    private static final ModifiableBaubleItem.Modifier[] MODIFIERS = ModifiableBaubleItem.Modifier.values();

    @Override
    public ModifiableBaubleItem.Modifier[] getModifiers() {
        return MODIFIERS;
    }

    public CuriousRingItem(Properties pProperties) {
        super(pProperties);
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
                LivingEntity entity = slotContext.entity();
                if (entity instanceof Player player) {
                    applyModifier(player, stack);
                }
            }

            @Override
            public void onUnequip(SlotContext slotContext, ItemStack newStack) {
                LivingEntity entity = slotContext.entity();
                if (entity instanceof Player player) {
                    removeModifier(player, stack);
                    player.removeEffect(MobEffects.DIG_SPEED);
                }
            }

            @Override
            public void curioTick(SlotContext slotContext) {
                LivingEntity entity = slotContext.entity();
                if (entity instanceof Player player) {
                    player.addEffect(new MobEffectInstance(
                            MobEffects.DIG_SPEED,
                            Integer.MAX_VALUE,
                            0,
                            true, true, false
                    ) {
                        @Override
                        public boolean isCurativeItem(ItemStack stack) {
                            return false;
                        }
                    });
                }
            }

            @Override
            public boolean canEquip(SlotContext slotContext) {
                return true;
            }
        });
    }

    // UUID初始化
    private void initializeUUIDs(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();

        if (!tag.hasUUID(SPEED_UUID_KEY)) {
            tag.putUUID(SPEED_UUID_KEY, UUID.randomUUID());
        }
        if (!tag.hasUUID(ATTACK_SPEED_UUID_KEY)) {
            tag.putUUID(ATTACK_SPEED_UUID_KEY, UUID.randomUUID());
        }
        if (!tag.hasUUID(ARMOR_UUID_KEY)) {
            tag.putUUID(ARMOR_UUID_KEY, UUID.randomUUID());
        }
    }

    @Mod.EventBusSubscriber(modid = "bountifulbaubles", bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class RingEffectHandler {
        @SubscribeEvent
        public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
            if (event.phase == TickEvent.Phase.START) return;
            Player player = event.player;

            if (!player.level().isClientSide()) {
                if (player.tickCount % 10 == 0) {
                    updateRingAttributes(player);
                }
            }
        }

        private static void updateRingAttributes(Player player) {
            int ringCount = CuriosApi.getCuriosInventory(player)
                    .map(inv -> inv.findCurios(stack ->
                            stack.getItem() instanceof CuriousRingItem).size())
                    .orElse(0);

            double speedBoost = 0.10 * ringCount;
            double atkSpeedBoost = 0.10 * ringCount;
            double armorBoost = 2.0 * ringCount;

            applyAttributeModifiers(player,
                    Attributes.MOVEMENT_SPEED,
                    "bountifulbaubles:ring_movement_speed",
                    speedBoost,
                    AttributeModifier.Operation.MULTIPLY_TOTAL
            );

            applyAttributeModifiers(player,
                    Attributes.ATTACK_SPEED,
                    "bountifulbaubles:ring_attack_speed",
                    atkSpeedBoost,
                    AttributeModifier.Operation.MULTIPLY_TOTAL
            );

            applyAttributeModifiers(player,
                    Attributes.ARMOR,
                    "bountifulbaubles:ring_armor",
                    armorBoost,
                    AttributeModifier.Operation.ADDITION
            );
        }

        private static void applyAttributeModifiers(Player player, Attribute attribute,
                                                    String modifierName, double amount,
                                                    AttributeModifier.Operation op) {
            AttributeInstance attrib = player.getAttribute(attribute);
            UUID modifierId = UUID.nameUUIDFromBytes((modifierName + player.getUUID()).getBytes());

            attrib.removeModifier(modifierId);

            if (amount > 0) {
                AttributeModifier modifier = new AttributeModifier(
                        modifierId, modifierName, amount, op
                );
                attrib.addPermanentModifier(modifier);
            }
        }
    }

    @Override
    public int getEnchantmentValue() {
        return 0;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.curious_ring.effect")
                .withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.curious_ring.effect1")
                .withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.curious_ring.effect2")
                .withStyle(ChatFormatting.BLUE));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}