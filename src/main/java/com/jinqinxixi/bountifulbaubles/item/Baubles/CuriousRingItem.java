package com.jinqinxixi.bountifulbaubles.item.Baubles;

import com.jinqinxixi.bountifulbaubles.system.modifier.ModifiableBaubleItem;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

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
    public int getEnchantmentValue() {
        return 0; // 附魔等级为0
    }

    // 禁止任何形式的附魔（包括铁砧）
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }
    // ====== UUID初始化 ======
    private void initializeUUIDs(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();

        // 三重UUID生成确保每个属性独立
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

    // ========== 事件处理器 ==========
    @Mod.EventBusSubscriber(modid = "bountifulbaubles", bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class RingEffectHandler {
        // 急迫效果的配置参数
        private static final int HASTE_INTERVAL = 20 * 2; // 检测间隔（40刻=2秒）
        private static final int HASTE_DURATION = 20 * 3; // 持续时间（60刻=3秒）
        private static final MobEffect HASTE_EFFECT = MobEffects.DIG_SPEED;

        @SubscribeEvent
        public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
            if (event.phase == TickEvent.Phase.START) return;
            Player player = event.player;

            if (!player.level().isClientSide()) {
                // 每0.5秒更新属性
                if (player.tickCount % 10 == 0) {
                    updateRingAttributes(player);
                }

                // 每2秒处理急迫效果
                if (player.tickCount % 40 == 0) {
                    handleHasteEffect(player);
                }
            }
        }

        // ========== 新增方法定义 ==========
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

            // 移除旧修饰符
            attrib.removeModifier(modifierId);

            if (amount > 0) {
                AttributeModifier modifier = new AttributeModifier(
                        modifierId, modifierName, amount, op
                );
                attrib.addPermanentModifier(modifier);
            }
        }


        private static void handleHasteEffect(Player player) {
            boolean hasRing = CuriosApi.getCuriosInventory(player)
                    .map(inv -> inv.findFirstCurio(stack ->
                            stack.getItem() instanceof CuriousRingItem).isPresent())
                    .orElse(false);

            MobEffectInstance currentEffect = player.getEffect(HASTE_EFFECT);

            if (hasRing) {
                if (shouldRefreshEffect(currentEffect)) {
                    applyHaste(player);
                }
            } else if (isFromRing(currentEffect)) {
                player.removeEffect(HASTE_EFFECT);
            }
        }

        private static boolean shouldRefreshEffect(@Nullable MobEffectInstance effect) {
            return effect == null ||
                    effect.getDuration() <= HASTE_INTERVAL;
        }

        private static void applyHaste(Player player) {
            player.addEffect(new MobEffectInstance(
                    HASTE_EFFECT,
                    HASTE_DURATION,
                    0,
                    false,  // 非环境效果
                    true    // 显示粒子
            ) {
                @Override
                public CompoundTag save(CompoundTag nbt) {
                    nbt.putString("Source", "bountiful_ring");
                    return super.save(nbt);
                }
            });
        }

        private static boolean isFromRing(@Nullable MobEffectInstance effect) {
            if (effect == null) return false;
            CompoundTag tag = effect.save(new CompoundTag());
            return tag.contains("Source") &&
                    tag.getString("Source").equals("bountiful_ring");
        }
    }
    // ====== 物品描述 ======
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.curious_ring.effect")
                .withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.curious_ring.effect1")
                .withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.curious_ring.effect2")
                .withStyle(ChatFormatting.BLUE));
        super.appendHoverText(stack, level, tooltip, flag); // 调用基类方法显示修饰符信息
    }
}
