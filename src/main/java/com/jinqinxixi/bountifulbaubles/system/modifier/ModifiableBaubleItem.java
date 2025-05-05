package com.jinqinxixi.bountifulbaubles.system.modifier;

import com.jinqinxixi.bountifulbaubles.config.ModConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Mod.EventBusSubscriber
public abstract class ModifiableBaubleItem extends Item implements ICurioItem {
    public static final String MODIFIER_TAG = "BaubleModifier";
    public static final String INITIALIZED_TAG = "IsInitialized";
    protected static final Random RANDOM = new Random();
    private static final Logger LOGGER = LoggerFactory.getLogger(ModifiableBaubleItem.class);

    public ModifiableBaubleItem(Properties properties) {
        super(properties);
    }

    public abstract Modifier[] getModifiers();

    // 获取修饰符UUID
    protected UUID getModifierUUID(ItemStack stack) {
        CompoundTag modifierTag = stack.getTagElement(MODIFIER_TAG);
        if (modifierTag != null && modifierTag.contains("UUID")) {
            return modifierTag.getUUID("UUID");
        }
        return null;
    }

    // 比较两个物品堆的修饰符
    protected boolean hasSameModifier(ItemStack stack1, ItemStack stack2) {
        if (stack1.isEmpty() || stack2.isEmpty()) return false;

        UUID uuid1 = getModifierUUID(stack1);
        UUID uuid2 = getModifierUUID(stack2);

        return uuid1 != null && uuid1.equals(uuid2);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
        if (!ModConfig.isModifierEnabled()) return;
        super.inventoryTick(stack, world, entity, slot, selected);
        if (!world.isClientSide && entity instanceof Player) {
            initializeModifier(stack);
        }
    }

    protected void initializeModifier(ItemStack stack) {
        if (!ModConfig.isModifierEnabled()) return;
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.getBoolean(INITIALIZED_TAG)) {
            Modifier selected = getRandomModifier();
            UUID uniqueUUID = UUID.randomUUID();
            saveModifierToTag(stack, selected, uniqueUUID);
            tag.putBoolean(INITIALIZED_TAG, true);
        }
    }

    private Modifier getRandomModifier() {
        Modifier[] modifiers = getModifiers();
        return modifiers[RANDOM.nextInt(modifiers.length)];
    }

    private void saveModifierToTag(ItemStack stack, Modifier modifier, UUID uniqueUUID) {
        CompoundTag modifierTag = new CompoundTag();
        modifierTag.putString("Attribute", ForgeRegistries.ATTRIBUTES.getKey(modifier.attribute).toString());
        modifierTag.putString("TranslationKey", modifier.translationKey);
        modifierTag.putDouble("Value", modifier.value);
        modifierTag.putUUID("UUID", uniqueUUID);
        stack.addTagElement(MODIFIER_TAG, modifierTag);
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player)) return;

        // 检查是否初始化
        if (!stack.getOrCreateTag().getBoolean(INITIALIZED_TAG)) {
            initializeModifier(stack);
            stack.getOrCreateTag().putBoolean(INITIALIZED_TAG, true);
            applyModifier(player, stack);
            return;
        }

        // 比较修饰符UUID
        if (!prevStack.isEmpty() && hasSameModifier(prevStack, stack)) {
            // 如果修饰符相同，不需要重新应用
            return;
        }

        // 应用修饰符
        applyModifier(player, stack);
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (!(slotContext.entity() instanceof Player player)) return;

        // 如果新装备的物品有相同的修饰符，不移除效果
        if (!newStack.isEmpty() && hasSameModifier(newStack, stack)) {
            return;
        }

        // 移除修饰符效果
        removeModifier(player, stack);
    }

    protected void applyModifier(Player player, ItemStack stack) {
        if (!ModConfig.isModifierEnabled()) return;

        CompoundTag modifierTag = stack.getTagElement(MODIFIER_TAG);
        if (modifierTag != null) {
            try {
                ResourceLocation attributeId = new ResourceLocation(modifierTag.getString("Attribute"));
                Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(attributeId);
                if (attribute == null) {
                    LOGGER.error("Unknown attribute: {}", attributeId);
                    return;
                }

                UUID uuid = modifierTag.contains("UUID") ?
                        modifierTag.getUUID("UUID") :
                        UUID.randomUUID();

                // 确保UUID被保存
                if (!modifierTag.contains("UUID")) {
                    modifierTag.putUUID("UUID", uuid);
                }

                double value = modifierTag.getDouble("Value");
                AttributeModifier.Operation operation = getOperationType(attribute);

                AttributeModifier mod = new AttributeModifier(
                        uuid,
                        "BaubleModifier_" + stack.hashCode(),
                        value,
                        operation
                );

                if (!player.getAttribute(attribute).hasModifier(mod)) {
                    player.getAttribute(attribute).addTransientModifier(mod);
                }
            } catch (Exception e) {
                LOGGER.error("Apply modifier failed: {}", e.getMessage());
            }
        }
    }

    protected void removeModifier(Player player, ItemStack stack) {
        CompoundTag modifierTag = stack.getTagElement(MODIFIER_TAG);
        if (modifierTag != null && modifierTag.contains("UUID")) {
            try {
                ResourceLocation attributeId = new ResourceLocation(modifierTag.getString("Attribute"));
                Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(attributeId);
                if (attribute != null) {
                    UUID uuid = modifierTag.getUUID("UUID");
                    player.getAttribute(attribute).removeModifier(uuid);

                    // 特殊处理最大生命值
                    if (attribute == Attributes.MAX_HEALTH) {
                        player.setHealth(Math.min(player.getHealth(), player.getMaxHealth()));
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Remove modifier failed: {}", e.getMessage());
            }
        }
    }

    AttributeModifier.Operation getOperationType(Attribute attribute) {
        return (attribute == Attributes.ATTACK_SPEED ||
                attribute == Attributes.MOVEMENT_SPEED ||
                attribute == Attributes.ATTACK_DAMAGE)
                ? AttributeModifier.Operation.MULTIPLY_BASE
                : AttributeModifier.Operation.ADDITION;
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
                ModifiableBaubleItem.this.curioTick(slotContext, stack);
            }

            @Override
            public void onEquip(SlotContext slotContext, ItemStack prevStack) {
                ModifiableBaubleItem.this.onEquip(slotContext, prevStack, stack);
            }



            @Override
            public void onUnequip(SlotContext slotContext, ItemStack newStack) {
                ModifiableBaubleItem.this.onUnequip(slotContext, newStack, stack);
            }

            @Override
            public boolean canEquip(SlotContext slotContext) {
                return true;
            }
        });
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        if (!ModConfig.isModifierEnabled()) {
            return;
        }

        CompoundTag modifierTag = stack.getTagElement(MODIFIER_TAG);
        if (modifierTag != null) {
            String translationKey = modifierTag.getString("TranslationKey");
            double value = modifierTag.getDouble("Value");
            String formattedValue;

            if (value == (int) value) {
                formattedValue = String.valueOf((int) value);
            } else if (value >= 1.0) {
                formattedValue = String.format("%.1f", value);
            } else {
                formattedValue = String.format("%d%%", (int)(value * 100));
            }

            tooltip.add(Component.literal("◆ ").withStyle(ChatFormatting.LIGHT_PURPLE)
                    .append(Component.translatable(translationKey).withStyle(ChatFormatting.LIGHT_PURPLE))
                    .append(Component.literal(" +" + formattedValue).withStyle(ChatFormatting.GREEN)));
        } else {
            tooltip.add(Component.translatable("tooltip.bountifulbaubles.unidentified")
                    .withStyle(ChatFormatting.GRAY));
        }
    }

    public enum Modifier {
        HALF_HEARTED("half_hearted", 1.0, Attributes.MAX_HEALTH),
        HEARTY("hearty", 2.0, Attributes.MAX_HEALTH),
        HARD("hard", 1.0, Attributes.ARMOR),
        GUARDING("guarding", 1.5, Attributes.ARMOR),
        ARMORED("armored", 2.0, Attributes.ARMOR),
        WARDING("warding", 1.0, Attributes.ARMOR_TOUGHNESS),
        JAGGED("jagged", 0.02, Attributes.ATTACK_DAMAGE),
        SPIKED("spiked", 0.04, Attributes.ATTACK_DAMAGE),
        ANGRY("angry", 0.06, Attributes.ATTACK_DAMAGE),
        MENACING("menacing", 0.08, Attributes.ATTACK_DAMAGE),
        BRISK("brisk", 0.01, Attributes.MOVEMENT_SPEED),
        FLEETING("fleeting", 0.02, Attributes.MOVEMENT_SPEED),
        HASTY("hasty", 0.03, Attributes.MOVEMENT_SPEED),
        QUICK("quick", 0.04, Attributes.MOVEMENT_SPEED),
        WILD("wild", 0.02, Attributes.ATTACK_SPEED),
        RASH("rash", 0.04, Attributes.ATTACK_SPEED),
        INTREPID("intrepid", 0.06, Attributes.ATTACK_SPEED),
        VIOLENT("violent", 0.08, Attributes.ATTACK_SPEED);

        final String translationKey;
        final double value;
        final Attribute attribute;

        Modifier(String key, double value, Attribute attribute) {
            this.translationKey = "modifier.bountifulbaubles." + key;
            this.value = value;
            this.attribute = attribute;
        }
    }
}