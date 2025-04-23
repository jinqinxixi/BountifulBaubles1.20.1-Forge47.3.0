package com.jinqinxixi.bountifulbaubles.item.Baubles;

import com.jinqinxixi.bountifulbaubles.system.modifier.ModifiableBaubleItem;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class WrathPendantItem extends ModifiableBaubleItem {
    // 使用父类修饰符系统 + 独立攻击加成
    private static final String DAMAGE_UUID_KEY = "WrathDamageUUID";
    private static final double DAMAGE_BONUS = 2.0;

    private static final ModifiableBaubleItem.Modifier[] MODIFIERS = ModifiableBaubleItem.Modifier.values();

    @Override
    public ModifiableBaubleItem.Modifier[] getModifiers() {
        return MODIFIERS;
    }

    public WrathPendantItem(Properties properties) {
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
                if (slotContext.entity() instanceof Player player) {
                    // 父类修饰符应用
                    applyModifier(player, stack);
                    // 子类特效应用
                    applyWrathDamageModifier(player, stack);
                }
            }

            @Override
            public void onUnequip(SlotContext slotContext, ItemStack newStack) {
                if (slotContext.entity() instanceof Player player) {
                    // 父类修饰符移除
                    removeModifier(player, stack);
                    // 子类特效移除
                    removeWrathDamageModifier(player, stack);
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

    // ===== 私有辅助方法 =====
    private void initializeDamageUUID(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.hasUUID(DAMAGE_UUID_KEY)) {
            tag.putUUID(DAMAGE_UUID_KEY, UUID.randomUUID());
        }
    }

    private void applyWrathDamageModifier(Player player, ItemStack stack) {
        initializeDamageUUID(stack);
        CompoundTag tag = stack.getOrCreateTag();
        UUID damageUUID = tag.getUUID(DAMAGE_UUID_KEY);

        AttributeModifier modifier = new AttributeModifier(
                damageUUID,
                "WrathPendantDamage",
                DAMAGE_BONUS,
                AttributeModifier.Operation.ADDITION
        );

        if (!player.getAttribute(Attributes.ATTACK_DAMAGE).hasModifier(modifier)) {
            player.getAttribute(Attributes.ATTACK_DAMAGE).addPermanentModifier(modifier);
        }
    }

    private void removeWrathDamageModifier(Player player, ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.hasUUID(DAMAGE_UUID_KEY)) {
            UUID damageUUID = tag.getUUID(DAMAGE_UUID_KEY);
            player.getAttribute(Attributes.ATTACK_DAMAGE).removeModifier(damageUUID);
        }
    }

    // ===== 其他必要方法 =====
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (slotContext.entity() instanceof Player player) {
            // 确保特效持续生效
            applyWrathDamageModifier(player, stack);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.wrath_pendant.effect")
                .withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.wrath_pendant.effect1")
                .withStyle(ChatFormatting.BLUE));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
