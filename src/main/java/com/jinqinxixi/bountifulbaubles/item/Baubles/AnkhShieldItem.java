package com.jinqinxixi.bountifulbaubles.item.Baubles;

import com.jinqinxixi.bountifulbaubles.system.modifier.ModifiableBaubleItem;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AnkhShieldItem extends ModifiableBaubleItem implements Equipable {
    private static final int MAX_DURABILITY = 1680;
    private static final ModifiableBaubleItem.Modifier[] MODIFIERS = ModifiableBaubleItem.Modifier.values();


    public AnkhShieldItem() {
        super(new Item.Properties()
                .durability(MAX_DURABILITY)
                .rarity(Rarity.UNCOMMON)
                .fireResistant());
    }

    @Override
    public Modifier[] getModifiers() {
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
            public void onEquip(SlotContext slotContext, ItemStack prevStack) {
                LivingEntity entity = slotContext.entity();
                if (entity instanceof Player player) {
                    // 先执行父类属性修正
                    applyModifier(player, stack);
                    if (player.hasEffect(MobEffects.DARKNESS)) {
                        player.removeEffect(MobEffects.DARKNESS);
                    }
                    if (player.hasEffect(MobEffects.WITHER)) {
                        player.removeEffect(MobEffects.WITHER);
                    }
                    if (player.hasEffect(MobEffects.CONFUSION)) {
                        player.removeEffect(MobEffects.CONFUSION);
                    }
                    if (player.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
                        player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
                    }
                    if (player.hasEffect(MobEffects.HUNGER)) {
                        player.removeEffect(MobEffects.HUNGER);
                    }
                    if (player.hasEffect(MobEffects.LEVITATION)) {
                        player.removeEffect(MobEffects.LEVITATION);
                    }
                    if (player.hasEffect(MobEffects.BLINDNESS)) {
                        player.removeEffect(MobEffects.BLINDNESS);
                    }
                    if (player.hasEffect(MobEffects.DIG_SLOWDOWN)) {
                        player.removeEffect(MobEffects.DIG_SLOWDOWN);
                    }
                    if (player.hasEffect(MobEffects.WEAKNESS)) {
                        player.removeEffect(MobEffects.WEAKNESS);
                    }
                    if (player.hasEffect(MobEffects.POISON)) {
                        player.removeEffect(MobEffects.POISON);
                    }
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

    // 添加效果免疫事件处理
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        if (event.getEntity() instanceof Player player) {
            MobEffectInstance effect = event.getEffectInstance();
            if (effect != null && (effect.getEffect() == MobEffects.DARKNESS ||
                    effect.getEffect() == MobEffects.WITHER ||
                    effect.getEffect() == MobEffects.CONFUSION ||
                    effect.getEffect() == MobEffects.MOVEMENT_SLOWDOWN ||
                    effect.getEffect() == MobEffects.HUNGER ||
                    effect.getEffect() == MobEffects.LEVITATION ||
                    effect.getEffect() == MobEffects.BLINDNESS ||
                    effect.getEffect() == MobEffects.DIG_SLOWDOWN ||
                    effect.getEffect() == MobEffects.WEAKNESS ||
                    effect.getEffect() == MobEffects.POISON)) {
                if (hasShield(player)) {
                    event.setResult(Event.Result.DENY);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onItemEquipped(net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent event) {
        if (event.getEntity() instanceof Player player) {
            ItemStack stack = event.getTo();
            if (stack.getItem() instanceof AnkhShieldItem &&
                    (event.getSlot() == EquipmentSlot.MAINHAND || event.getSlot() == EquipmentSlot.OFFHAND)) {
                if (player.hasEffect(MobEffects.DARKNESS)) {
                    player.removeEffect(MobEffects.DARKNESS);
                }
                if (player.hasEffect(MobEffects.WITHER)) {
                    player.removeEffect(MobEffects.WITHER);
                }
                if (player.hasEffect(MobEffects.CONFUSION)) {
                    player.removeEffect(MobEffects.CONFUSION);
                }
                if (player.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
                    player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
                }
                if (player.hasEffect(MobEffects.HUNGER)) {
                    player.removeEffect(MobEffects.HUNGER);
                }
                if (player.hasEffect(MobEffects.LEVITATION)) {
                    player.removeEffect(MobEffects.LEVITATION);
                }
                if (player.hasEffect(MobEffects.BLINDNESS)) {
                    player.removeEffect(MobEffects.BLINDNESS);
                }
                if (player.hasEffect(MobEffects.DIG_SLOWDOWN)) {
                    player.removeEffect(MobEffects.DIG_SLOWDOWN);
                }
                if (player.hasEffect(MobEffects.WEAKNESS)) {
                    player.removeEffect(MobEffects.WEAKNESS);
                }
                if (player.hasEffect(MobEffects.POISON)) {
                    player.removeEffect(MobEffects.POISON);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();

        if (isFireDamage(source) && !isLavaDamage(source) && hasShield(entity)) {
            event.setAmount(event.getAmount() * 0.5F);
        }
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();

        if (isFireDamage(source) && hasShield(entity)) {
            entity.clearFire();
        }
    }

    @SubscribeEvent
    public static void onKnockback(LivingKnockBackEvent event) {
        if (hasShield(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    // 定期检查和更新
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.player.level().isClientSide()) {
            Player player = event.player;
            if (hasShield(player)) {
                // 处理自动修复
                handleAutoRepair(player);
            }
        }
    }

    private static void handleAutoRepair(Player player) {
        if (player.tickCount % 200 == 0) {
            // 修复主手和副手
            repairShield(player.getMainHandItem());
            repairShield(player.getOffhandItem());

            // 修复饰品栏
            CuriosApi.getCuriosInventory(player).ifPresent(inv ->
                    inv.findFirstCurio(stack -> stack.getItem() instanceof AnkhShieldItem)
                            .ifPresent(slotResult -> repairShield(slotResult.stack())));
        }
    }

    private static void repairShield(ItemStack stack) {
        if (stack.getItem() instanceof AnkhShieldItem && stack.isDamaged()) {
            stack.setDamageValue(stack.getDamageValue() - 1);
        }
    }

    private static boolean hasShield(LivingEntity entity) {
        if (!(entity instanceof Player player)) return false;

        // 检查主副手
        boolean inHand = player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof AnkhShieldItem ||
                player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof AnkhShieldItem;

        // 检查饰品栏
        boolean inCurio = CuriosApi.getCuriosInventory(player).resolve()
                .map(inv -> inv.findFirstCurio(stack ->
                        stack.getItem() instanceof AnkhShieldItem).isPresent())
                .orElse(false);

        return inHand || inCurio;
    }

    private static boolean isFireDamage(DamageSource source) {
        return source.is(DamageTypes.IN_FIRE) ||
                source.is(DamageTypes.ON_FIRE) ||
                source.is(DamageTypes.FIREBALL) ||
                source.is(DamageTypes.UNATTRIBUTED_FIREBALL);
    }

    private static boolean isLavaDamage(DamageSource source) {
        return source.is(DamageTypes.LAVA) ||
                source.is(DamageTypes.HOT_FLOOR);
    }

    // 物品属性相关方法
    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return repair.is(Items.OBSIDIAN);
    }

    @Override
    public int getEnchantmentValue() {
        return 0;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    // 盾牌功能相关方法
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(itemstack);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return ToolActions.DEFAULT_SHIELD_ACTIONS.contains(toolAction);
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.OFFHAND;
    }

    @Override
    public EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EquipmentSlot.OFFHAND;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.ankh_shield.effect")
                .withStyle(ChatFormatting.BLUE));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}