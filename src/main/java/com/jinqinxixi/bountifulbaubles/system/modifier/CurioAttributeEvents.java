package com.jinqinxixi.bountifulbaubles.system.modifier;

import com.jinqinxixi.bountifulbaubles.BountifulBaublesMod;
import com.jinqinxixi.bountifulbaubles.config.ModifierConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;
import top.theillusivec4.curios.api.event.CurioChangeEvent;

import java.util.Random;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = BountifulBaublesMod.MOD_ID)
public class CurioAttributeEvents {
//    private static final Random RANDOM = new Random();
//    private static ModifierConfig config;
//
//    public static void init() {
//        config = ModifierConfig.load();
//    }
//
//    @SubscribeEvent
//    public static void onLivingUpdate(TickEvent.PlayerTickEvent event) {
//        if (event.phase != TickEvent.Phase.END) return;
//
//        Player player = event.player;
//        player.getInventory().items.forEach(stack -> {
//            String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
//            if (config.isItemModifiable(itemId)) {
//                checkAndInitializeModifier(stack, player);
//            }
//        });
//    }
//
//    private static void checkAndInitializeModifier(ItemStack stack, Player player) {
//        if (stack.isEmpty()) return;
//
//        CompoundTag tag = stack.getOrCreateTag();
//        if (!tag.contains("ModifierType") || !tag.contains("ModifierUUID")) {
//            try {
//                Modifier randomModifier = getRandomModifier();
//                UUID modifierUUID = UUID.randomUUID();
//
//                tag.putString("ModifierType", randomModifier.name());
//                tag.putString("ModifierUUID", modifierUUID.toString());
//            } catch (Exception e) {
//                // 忽略异常
//            }
//        }
//    }
//
//    @SubscribeEvent
//    public static void onCurioAttributeModifier(CurioAttributeModifierEvent event) {
//        ItemStack stack = event.getItemStack();
//        String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
//
//        if (!config.isItemModifiable(itemId)) return;
//
//        CompoundTag tag = stack.getTag();
//        if (tag == null || !tag.contains("ModifierType") || !tag.contains("ModifierUUID")) return;
//
//        try {
//            String modifierName = tag.getString("ModifierType");
//            String uuidString = tag.getString("ModifierUUID");
//            Modifier modifier = Modifier.valueOf(modifierName);
//            UUID uuid = UUID.fromString(uuidString);
//
//            ResourceLocation attributeId = new ResourceLocation("minecraft", modifier.attributeId);
//            ResourceKey<Attribute> key = ResourceKey.create(BuiltInRegistries.ATTRIBUTE.key(), attributeId);
//
//            BuiltInRegistries.ATTRIBUTE.getHolder(key).ifPresent(attributeHolder -> {
//                Attribute attribute = attributeHolder.value();
//                AttributeModifier attributeModifier = new AttributeModifier(
//                        uuid,
//                        String.format("%s.%s", itemId, modifier.name().toLowerCase()),
//                        modifier.amount,
//                        getOperation(modifier)
//                );
//
//                event.addModifier(attribute, attributeModifier);
//            });
//        } catch (Exception e) {
//            // 忽略异常
//        }
//    }
//
//    @SubscribeEvent
//    public static void onCurioChange(CurioChangeEvent event) {
//        ItemStack removedStack = event.getFrom();
//        ItemStack addedStack = event.getTo();
//
//        if (removedStack.isEmpty()) return;
//
//        String itemId = BuiltInRegistries.ITEM.getKey(removedStack.getItem()).toString();
//        if (!config.isItemModifiable(itemId)) return;
//
//        if (hasSameModifier(removedStack, addedStack)) {
//            return;
//        }
//
//        removeModifierFromStack(removedStack, event.getEntity());
//    }
//
//    private static boolean hasSameModifier(ItemStack stack1, ItemStack stack2) {
//        if (stack1.isEmpty() || stack2.isEmpty()) return false;
//
//        CompoundTag tag1 = stack1.getTag();
//        CompoundTag tag2 = stack2.getTag();
//
//        if (tag1 == null || tag2 == null) return false;
//        if (!tag1.contains("ModifierUUID") || !tag2.contains("ModifierUUID")) return false;
//
//        String uuid1 = tag1.getString("ModifierUUID");
//        String uuid2 = tag2.getString("ModifierUUID");
//
//        return uuid1.equals(uuid2);
//    }
//
//    private static void removeModifierFromStack(ItemStack stack, net.minecraft.world.entity.Entity entity) {
//        if (!(entity instanceof Player player)) return;
//
//        CompoundTag tag = stack.getTag();
//        if (tag == null || !tag.contains("ModifierType") || !tag.contains("ModifierUUID")) return;
//
//        try {
//            String modifierName = tag.getString("ModifierType");
//            String uuidString = tag.getString("ModifierUUID");
//            Modifier modifier = Modifier.valueOf(modifierName);
//            UUID uuid = UUID.fromString(uuidString);
//
//            ResourceLocation attributeId = new ResourceLocation("minecraft", modifier.attributeId);
//            ResourceKey<Attribute> key = ResourceKey.create(BuiltInRegistries.ATTRIBUTE.key(), attributeId);
//
//            BuiltInRegistries.ATTRIBUTE.getHolder(key).ifPresent(attributeHolder -> {
//                Attribute attribute = attributeHolder.value();
//                AttributeInstance instance = player.getAttribute(attribute);
//
//                if (instance != null) {
//                    instance.removeModifier(uuid);
//
//                    if (attribute == Attributes.MAX_HEALTH) {
//                        player.setHealth(Math.min(player.getHealth(), player.getMaxHealth()));
//                    }
//                }
//            });
//        } catch (Exception e) {
//            // 忽略异常
//        }
//    }
//
//    @SubscribeEvent
//    public static void onAnvilUpdate(AnvilUpdateEvent event) {
//        ItemStack left = event.getLeft();
//        ItemStack right = event.getRight();
//
//        String leftItemId = BuiltInRegistries.ITEM.getKey(left.getItem()).toString();
//        if (!config.isItemModifiable(leftItemId)) return;
//
//        ModifierConfig.ReforgeConfig reforgeConfig = config.getReforgeConfig(leftItemId);
//        String rightItemId = BuiltInRegistries.ITEM.getKey(right.getItem()).toString();
//
//        if (rightItemId.equals(reforgeConfig.requiredItem)) {
//            ItemStack output = left.copy();
//
//            CompoundTag tag = output.getTag();
//            if (tag != null) {
//                if (tag.contains("ModifierType")) {
//                    tag.remove("ModifierType");
//                }
//                if (tag.contains("ModifierUUID")) {
//                    tag.remove("ModifierUUID");
//                }
//            }
//
//            event.setOutput(output);
//            event.setCost(reforgeConfig.experienceCost);
//            event.setMaterialCost(reforgeConfig.materialCost);
//        }
//    }
//
//    @SubscribeEvent
//    public static void onAnvilRepair(AnvilRepairEvent event) {
//        ItemStack result = event.getOutput();
//        String itemId = BuiltInRegistries.ITEM.getKey(result.getItem()).toString();
//
//        if (!config.isItemModifiable(itemId)) return;
//
//        try {
//            Modifier newModifier = getRandomModifier();
//            CompoundTag tag = result.getOrCreateTag();
//            UUID newUUID = UUID.randomUUID();
//
//            tag.putString("ModifierType", newModifier.name());
//            tag.putString("ModifierUUID", newUUID.toString());
//        } catch (Exception e) {
//            // 忽略异常
//        }
//    }
//
//    private static Modifier getRandomModifier() {
//        Modifier[] values = Modifier.values();
//        return values[RANDOM.nextInt(values.length)];
//    }
//
//    private static AttributeModifier.Operation getOperation(Modifier modifier) {
//        return switch (modifier.attributeId) {
//            case "generic.attack_speed",
//                 "generic.movement_speed",
//                 "generic.attack_damage" -> AttributeModifier.Operation.MULTIPLY_BASE;
//            default -> AttributeModifier.Operation.ADDITION;
//        };
//    }
//
//    public enum Modifier {
//        // 生命值修改器 (ADD_VALUE)
//        HALF_HEARTED("half_hearted", 1.0, "generic.max_health"),    // +2生命值
//        HEARTY("hearty", 2.0, "generic.max_health"),                // +4生命值
//
//        // 护甲修改器 (ADD_VALUE)
//        HARD("hard", 1.0, "generic.armor"),                         // +1护甲
//        GUARDING("guarding", 1.5, "generic.armor"),                 // +1.5护甲
//        ARMORED("armored", 2.0, "generic.armor"),                   // +2护甲
//        WARDING("warding", 1.0, "generic.armor_toughness"),         // +1韧性
//
//        // 攻击力修改器 (ADD_MULTIPLIED_BASE)
//        JAGGED("jagged", 0.02, "generic.attack_damage"),            // +2%攻击
//        SPIKED("spiked", 0.04, "generic.attack_damage"),           // +4%攻击
//        ANGRY("angry", 0.06, "generic.attack_damage"),             // +6%攻击
//        MENACING("menacing", 0.08, "generic.attack_damage"),       // +8%攻击
//
//        // 移动速度修改器 (ADD_MULTIPLIED_BASE)
//        BRISK("brisk", 0.01, "generic.movement_speed"),            // +1%速度
//        FLEETING("fleeting", 0.02, "generic.movement_speed"),      // +2%速度
//        HASTY("hasty", 0.03, "generic.movement_speed"),           // +3%速度
//        QUICK("quick", 0.04, "generic.movement_speed"),           // +4%速度
//
//        // 攻击速度修改器 (ADD_MULTIPLIED_BASE)
//        WILD("wild", 0.02, "generic.attack_speed"),               // +2%攻速
//        RASH("rash", 0.04, "generic.attack_speed"),              // +4%攻速
//        INTREPID("intrepid", 0.06, "generic.attack_speed"),      // +6%攻速
//        VIOLENT("violent", 0.08, "generic.attack_speed");         // +8%攻速
//
//        final String name;
//        final double amount;
//        final String attributeId;
//
//        Modifier(String name, double amount, String attributeId) {
//            this.name = name;
//            this.amount = amount;
//            this.attributeId = attributeId;
//        }
//    }
}