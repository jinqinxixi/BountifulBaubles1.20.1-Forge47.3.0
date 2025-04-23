package com.jinqinxixi.bountifulbaubles.item.Baubles;

import com.jinqinxixi.bountifulbaubles.config.ModConfig;
import com.jinqinxixi.bountifulbaubles.system.modifier.ModifiableBaubleItem;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber
public class DropSpindleItem extends ModifiableBaubleItem {
    private static final Random RANDOM = new Random();

    public DropSpindleItem(Properties properties) {
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
            public List<Component> getAttributesTooltip(List<Component> tooltips) {
                return Collections.emptyList(); // 在父类中统一隐藏属性提示
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
    public static void onAttackEntity(AttackEntityEvent event) {
        Player player = event.getEntity();

        // 检查玩家是否装备了纺锤
        boolean hasSpindle = CuriosApi.getCuriosInventory(player)
                .resolve()
                .flatMap(handler -> handler.findFirstCurio(item -> item.getItem() instanceof DropSpindleItem))
                .isPresent();

        if (hasSpindle && !player.level().isClientSide) {
            // 使用配置的概率触发修复
            if (RANDOM.nextFloat() < ModConfig.getDropSpindleRepairChance()) {
                // 获取所有盔甲槽位
                EquipmentSlot[] armorSlots = {
                        EquipmentSlot.HEAD,
                        EquipmentSlot.CHEST,
                        EquipmentSlot.LEGS,
                        EquipmentSlot.FEET
                };

                // 获取所有已装备的受损盔甲
                List<ItemStack> damagedArmor = java.util.Arrays.stream(armorSlots)
                        .map(player::getItemBySlot)
                        .filter(stack -> !stack.isEmpty())
                        .filter(stack -> stack.getItem() instanceof ArmorItem)
                        .filter(stack -> stack.isDamaged())
                        .toList();

                if (!damagedArmor.isEmpty()) {
                    // 随机选择一件受损盔甲修复
                    ItemStack armorToRepair = damagedArmor.get(RANDOM.nextInt(damagedArmor.size()));
                    // 使用配置的修复量
                    int repairAmount = Math.min(
                            ModConfig.getDropSpindleRepairAmount(),
                            armorToRepair.getDamageValue()
                    );
                    armorToRepair.setDamageValue(armorToRepair.getDamageValue() - repairAmount);
                }
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.dropspindle.effects",
                        ModConfig.getDropSpindleRepairChance() * 100,
                        ModConfig.getDropSpindleRepairAmount())
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