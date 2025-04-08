//package com.jinqinxixi.bountifulbaubles.Modifier;
//
//import com.jinqinxixi.bountifulbaubles.Config.ItemConfig;
//import com.jinqinxixi.bountifulbaubles.item.ModItem;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.nbt.ListTag;
//import net.minecraft.world.effect.MobEffectInstance;
//import net.minecraft.world.effect.MobEffects;
//import net.minecraft.world.entity.ai.attributes.AttributeModifier;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.core.registries.BuiltInRegistries;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraftforge.event.AnvilUpdateEvent;
//import net.minecraftforge.event.entity.player.AnvilRepairEvent;
//import net.minecraftforge.event.entity.player.PlayerEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//import top.theillusivec4.curios.api.CuriosApi;
//import top.theillusivec4.curios.api.event.CurioChangeEvent;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.entity.ai.attributes.AttributeInstance;
//import net.minecraft.world.entity.ai.attributes.Attributes;
//import java.util.Random;
//import java.util.UUID;
//
//@Mod.EventBusSubscriber(modid = "bountifulbaubles", bus = Mod.EventBusSubscriber.Bus.FORGE)
//public class ItemModificationEvents {
//
//    private static final Random RANDOM = new Random();
//
//    private static enum Modifier {
//        // 直接加值的属性 (Operation: 0)
//        HALF_HEARTED("half_hearted", 1.0, "minecraft:generic.max_health", 0),
//        HEARTY("hearty", 2.0, "minecraft:generic.max_health", 0),
//        HARD("hard", 1.0, "minecraft:generic.armor", 0),
//        GUARDING("guarding", 1.5, "minecraft:generic.armor", 0),
//        ARMORED("armored", 2.0, "minecraft:generic.armor", 0),
//        WARDING("warding", 1.0, "minecraft:generic.armor_toughness", 0),
//
//        // 百分比增加的属性 (Operation: 1)
//        JAGGED("jagged", 0.02, "minecraft:generic.attack_damage", 1),
//        SPIKED("spiked", 0.04, "minecraft:generic.attack_damage", 1),
//        ANGRY("angry", 0.06, "minecraft:generic.attack_damage", 1),
//        MENACING("menacing", 0.08, "minecraft:generic.attack_damage", 1),
//        BRISK("brisk", 0.01, "minecraft:generic.movement_speed", 1),
//        FLEETING("fleeting", 0.02, "minecraft:generic.movement_speed", 1),
//        HASTY("hasty", 0.03, "minecraft:generic.movement_speed", 1),
//        QUICK("quick", 0.04, "minecraft:generic.movement_speed", 1),
//        WILD("wild", 0.02, "minecraft:generic.attack_speed", 1),
//        RASH("rash", 0.04, "minecraft:generic.attack_speed", 1),
//        INTREPID("intrepid", 0.06, "minecraft:generic.attack_speed", 1),
//        VIOLENT("violent", 0.08, "minecraft:generic.attack_speed", 1);
//
//        private final String name;
//        private final double amount;
//        private final String attributeId;
//        private final int operation;
//
//        Modifier(String name, double amount, String attributeId, int operation) {
//            this.name = name;
//            this.amount = amount;
//            this.attributeId = attributeId;
//            this.operation = operation;
//        }
//    }
//
//    // 初始化物品属性的方法
//    private static void initializeItemAttributes(ItemStack stack) {
//        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
//        if (!ItemConfig.isItemModifiable(itemId)) {
//            return;
//        }
//
//        CompoundTag nbt = stack.getOrCreateTag();
//        if (!nbt.contains("CurioAttributeModifiers")) {
//            Modifier modifier = Modifier.values()[RANDOM.nextInt(Modifier.values().length)];
//
//            ListTag modifiers = new ListTag();
//            CompoundTag modifierTag = new CompoundTag();
//            modifierTag.putString("Slot", ItemConfig.getSlotForItem(itemId));
//            modifierTag.putString("AttributeName", modifier.attributeId);
//            modifierTag.putString("Name", modifier.name);
//            modifierTag.putDouble("Amount", modifier.amount);
//            modifierTag.putInt("Operation", modifier.operation);
//            modifiers.add(modifierTag);
//            nbt.put("CurioAttributeModifiers", modifiers);
//        }
//    }
//
//    // 修改物品NBT的方法
//    private static void modifyFeralClaws(ItemStack stack, boolean shouldHaveModifier, Player player) {
//        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
//        if (stack.isEmpty() || !ItemConfig.isItemModifiable(itemId)) {
//            return;
//        }
//
//        CompoundTag nbt = stack.getOrCreateTag();
//
//        if (!nbt.contains("CurioAttributeModifiers")) {
//            initializeItemAttributes(stack);
//        }
//
//        // 只有在装备时才更新属性
//        if (shouldHaveModifier && player != null) {
//            updatePlayerAttributes(player);
//        }
//    }
//
//    // 添加新方法来刷新玩家属性
//    private static void refreshPlayerAttributes(Player player) {
//        AttributeInstance healthAttribute = player.getAttribute(Attributes.MAX_HEALTH);
//        if (healthAttribute != null) {
//            // 记录当前生命值
//            float currentHealth = player.getHealth();
//
//            // 给玩家一个极短时间的生命提升效果来强制刷新属性
//            player.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 0, 0, false, false, false));
//
//            // 在下一个tick移除效果
//            player.level().getServer().tell(new net.minecraft.server.TickTask(
//                    player.level().getServer().getTickCount() + 1,
//                    () -> {
//                        player.removeEffect(MobEffects.HEALTH_BOOST);
//                        // 如果当前生命值超过最大生命值，才进行调整
//                        if (currentHealth > player.getMaxHealth()) {
//                            player.setHealth(player.getMaxHealth());
//                        } else {
//                            // 否则保持原来的生命值
//                            player.setHealth(currentHealth);
//                        }
//                    }
//            ));
//        }
//    }
//
//    // 更新玩家属性的方法
//    private static void updatePlayerAttributes(Player player) {
//        AttributeInstance healthAttribute = player.getAttribute(Attributes.MAX_HEALTH);
//        if (healthAttribute != null) {
//            double currentHealth = player.getHealth();
//            double maxHealth = healthAttribute.getValue();
//
//            if (currentHealth > maxHealth) {
//                player.setHealth((float) maxHealth);
//            }
//        }
//    }
//
//    // 当饰品改变时（装备或卸下）
//    @SubscribeEvent
//    public static void onCurioChange(CurioChangeEvent event) {
//        if (!(event.getEntity() instanceof Player player)) {
//            return;
//        }
//
//        ItemStack from = event.getFrom();
//        ItemStack to = event.getTo();
//
//        // 当卸下饰品时
//        if (!from.isEmpty()) {
//            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(from.getItem());
//            if (ItemConfig.isItemModifiable(itemId)) {
//                // 先处理物品
//                modifyFeralClaws(from, false, player);
//                // 强制同步属性
//                refreshPlayerAttributes(player);
//            }
//        }
//
//        if (!to.isEmpty()) {
//            modifyFeralClaws(to, true, player);
//        }
//    }
//
//    // 物品捡起时检查
//    @SubscribeEvent
//    public static void onItemPickup(PlayerEvent.ItemPickupEvent event) {
//        ItemStack stack = event.getStack();
//        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
//        if (ItemConfig.isItemModifiable(itemId)) {
//            initializeItemAttributes(stack);
//        }
//    }
//
//    // 玩家加载时检查
//    @SubscribeEvent
//    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
//        Player player = event.getEntity();
//
//        player.level().getServer().tell(new net.minecraft.server.TickTask(
//                player.level().getServer().getTickCount() + 1,
//                () -> {
//                    // 处理背包中的物品
//                    player.getInventory().items.forEach(stack -> {
//                        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
//                        if (ItemConfig.isItemModifiable(itemId)) {
//                            initializeItemAttributes(stack);
//                        }
//                    });
//
//                    // 检查已装备的饰品
//                    CuriosApi.getCuriosInventory(player).ifPresent(handler -> {
//                        handler.getCurios().forEach((slotType, stackHandler) -> {
//                            for (int i = 0; i < stackHandler.getSlots(); i++) {
//                                ItemStack stack = stackHandler.getStacks().getStackInSlot(i);
//                                if (!stack.isEmpty()) {
//                                    modifyFeralClaws(stack, true, player);
//                                }
//                            }
//                        });
//                    });
//                }
//        ));
//    }
//
//    // 维度切换时检查
//    @SubscribeEvent
//    public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
//        Player player = event.getEntity();
//        player.level().getServer().tell(new net.minecraft.server.TickTask(
//                player.level().getServer().getTickCount() + 1,
//                () -> {
//                    player.getInventory().items.forEach(stack -> {
//                        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
//                        if (ItemConfig.isItemModifiable(itemId)) {
//                            initializeItemAttributes(stack);
//                        }
//                    });
//                }
//        ));
//    }
//
//    @SubscribeEvent
//    public static void onAnvilUpdate(AnvilUpdateEvent event) {
//        ItemStack left = event.getLeft();
//        ItemStack right = event.getRight();
//
//        ResourceLocation leftId = BuiltInRegistries.ITEM.getKey(left.getItem());
//
//        if (ItemConfig.isItemModifiable(leftId) &&
//                right.getItem().equals(ItemConfig.getAnvilMaterial())) {
//
//            ItemStack output = new ItemStack(left.getItem());
//
//            if (left.hasCustomHoverName()) {
//                output.setHoverName(left.getHoverName());
//            }
//
//            event.setOutput(output);
//            event.setCost(ItemConfig.getAnvilXpCost());
//            event.setMaterialCost(ItemConfig.getAnvilMaterialCost());
//        }
//    }
//
//    @SubscribeEvent
//    public static void onAnvilRepair(AnvilRepairEvent event) {
//        ItemStack left = event.getLeft();
//        ItemStack right = event.getRight();
//        ItemStack output = event.getOutput();
//
//        ResourceLocation leftId = BuiltInRegistries.ITEM.getKey(left.getItem());
//
//        if (ItemConfig.isItemModifiable(leftId) &&
//                right.getItem().equals(ItemConfig.getAnvilMaterial())) {
//
//            CompoundTag nbt = output.getOrCreateTag();
//            nbt.remove("CurioAttributeModifiers");
//            initializeItemAttributes(output);
//
//            if (left.hasCustomHoverName()) {
//                output.setHoverName(left.getHoverName());
//            }
//        }
//    }
//}