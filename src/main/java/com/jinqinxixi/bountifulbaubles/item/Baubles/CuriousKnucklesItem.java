package com.jinqinxixi.bountifulbaubles.item.Baubles;

import com.jinqinxixi.bountifulbaubles.system.modifier.ModifiableBaubleItem;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CuriousKnucklesItem extends ModifiableBaubleItem {
    // NBT数据存储键
    private static final String KNUCKLES_UUID_KEY = "KnucklesUUID";
    private static final Modifier[] MODIFIERS = Modifier.values();

    @Override
    public Modifier[] getModifiers() {
        return MODIFIERS;
    }

    public CuriousKnucklesItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public int getEnchantmentValue() {
        return 0;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    // ========== UUID管理 ==========
    private UUID getOrCreateUniqueId(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag();
        if (!nbt.hasUUID(KNUCKLES_UUID_KEY)) {
            UUID newUUID = UUID.randomUUID();
            nbt.putUUID(KNUCKLES_UUID_KEY, newUUID);
        }
        return nbt.getUUID(KNUCKLES_UUID_KEY);
    }

    // ========== 叠加事件处理器 ==========
    @Mod.EventBusSubscriber(modid = "bountifulbaubles", bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class KnucklesStackHandler {
        private static final Map<UUID, AttributeModifier> ACTIVE_MODIFIERS = new ConcurrentHashMap<>();
        private static final String MODIFIER_NAME = "bountifulbaubles:knuckles_stacked_damage";
        private static final double BASE_DAMAGE = 4.0;

        @SubscribeEvent
        public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
            if (event.phase == TickEvent.Phase.START || event.player.level().isClientSide()) return;

            Player player = event.player;
            if (player.tickCount % 10 == 0) {
                processStackedEffect(player);
            }
        }

        private static void processStackedEffect(Player player) {
            List<ItemStack> knuckles = CuriosApi.getCuriosInventory(player)
                    .map(inv -> inv.findCurios(stack ->
                            stack.getItem() instanceof CuriousKnucklesItem))
                    .orElse(Collections.emptyList())
                    .stream()
                    .map(SlotResult::stack)
                    .toList();

            double totalBoost = BASE_DAMAGE * knuckles.size();
            updateAttribute(player, totalBoost, knuckles);
        }

        private static void updateAttribute(Player player, double newBoost, List<ItemStack> knuckles) {
            AttributeInstance attrib = player.getAttribute(Attributes.ATTACK_DAMAGE);
            UUID playerId = player.getUUID();

            // 清理旧修饰符
            if (ACTIVE_MODIFIERS.containsKey(playerId)) {
                AttributeModifier oldModifier = ACTIVE_MODIFIERS.get(playerId);
                if (attrib.hasModifier(oldModifier)) {
                    attrib.removeModifier(oldModifier.getId());
                }
                ACTIVE_MODIFIERS.remove(playerId);
            }

            // 清理所有物品关联的旧修饰符
            knuckles.forEach(stack -> {
                UUID itemUUID = ((CuriousKnucklesItem) stack.getItem()).getOrCreateUniqueId(stack);
                AttributeModifier existing = attrib.getModifier(itemUUID);
                if (existing != null) {
                    attrib.removeModifier(itemUUID);
                }
            });

            if (newBoost > 0) {
                // 生成基于所有物品UUID的组合UUID
                UUID modifierId = knuckles.stream()
                        .map(stack -> ((CuriousKnucklesItem) stack.getItem()).getOrCreateUniqueId(stack))
                        .reduce((u1, u2) -> UUID.nameUUIDFromBytes(
                                (u1.toString() + u2.toString()).getBytes()))
                        .orElse(UUID.randomUUID());

                AttributeModifier modifier = new AttributeModifier(
                        modifierId,
                        MODIFIER_NAME,
                        newBoost,
                        AttributeModifier.Operation.ADDITION
                );

                if (!attrib.hasModifier(modifier)) {
                    attrib.addPermanentModifier(modifier);
                    ACTIVE_MODIFIERS.put(playerId, modifier);
                }
            }
        }

        @SubscribeEvent
        public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
            cleanupPlayer(event.getEntity());
        }

        @SubscribeEvent
        public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
            cleanupPlayer(event.getEntity());
        }

        private static void cleanupPlayer(Player player) {
            UUID playerId = player.getUUID();
            if (ACTIVE_MODIFIERS.containsKey(playerId)) {
                AttributeInstance attrib = player.getAttribute(Attributes.ATTACK_DAMAGE);
                AttributeModifier modifier = ACTIVE_MODIFIERS.get(playerId);
                if (attrib.hasModifier(modifier)) {
                    attrib.removeModifier(modifier);
                }
                ACTIVE_MODIFIERS.remove(playerId);
            }
        }
    }

    // ========== 物品信息 ==========
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.curious_knuckles.effect")
                .withStyle(ChatFormatting.BLUE));
        super.appendHoverText(stack, level, tooltip, flag);
    }

    // ========== 装备行为 ==========
    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }

    // ========== 复制控制 ==========
    @Override
    public boolean isFoil(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isFireResistant() {
        return true;
    }
}
