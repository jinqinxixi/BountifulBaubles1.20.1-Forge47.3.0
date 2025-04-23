package com.jinqinxixi.bountifulbaubles.item.Baubles;

import com.jinqinxixi.bountifulbaubles.system.modifier.ModifiableBaubleItem;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
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
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber
public class ObsidianShieldItem extends ModifiableBaubleItem {
    private static final int MAX_DURABILITY = 1000;
    public static final int EFFECTIVE_BLOCK_DELAY = 5;
    public static final float MINIMUM_DURABILITY_DAMAGE = 3.0F;
    public static final String TAG_BASE_COLOR = "Base";
    // 明确引用基类中的 Modifier 枚举
    private static final ModifiableBaubleItem.Modifier[] MODIFIERS = ModifiableBaubleItem.Modifier.values();
    @Override
    public ModifiableBaubleItem.Modifier[] getModifiers() {
        return MODIFIERS;
    }

    public ObsidianShieldItem() {
        super(new Item.Properties().durability(MAX_DURABILITY).rarity(Rarity.UNCOMMON).fireResistant().fireResistant());
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
                    applyModifier(player, stack); // 调用父类方法
                }
            }

            @Override
            public void onUnequip(SlotContext slotContext, ItemStack newStack) {
                if (slotContext.entity() instanceof Player player) {
                    removeModifier(player, stack); // 调用父类方法
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
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();

        // 检测需要处理的伤害类型
        if (isFireDamage(source) && !isLavaDamage(source)) {
            checkAndApplyProtection(entity, event);
        }
    }
    @SubscribeEvent
    public static void onKnockback(LivingKnockBackEvent event) {
        if (hasShield(event.getEntity())) {
            event.setCanceled(true);
        }
    }
    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();

        // 阻止点燃状态
        if (isFireDamage(source) && hasShield(entity)) {
            entity.clearFire();
        }
    }

    // ========== 火焰防护逻辑 ==========
    private static void checkAndApplyProtection(LivingEntity entity, LivingHurtEvent event) {
        if (hasShield(entity)) {
            event.setAmount(event.getAmount() * 0.5F);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.player.level().isClientSide()) {
            Player player = event.player;
            ItemStack mainHand = player.getMainHandItem();
            ItemStack offHand = player.getOffhandItem();

            // 检查主副手和饰品栏
            if (hasShield(player)) {
                // 处理自动修复
                handleAutoRepair(player, mainHand);
                handleAutoRepair(player, offHand);

            }
        }
    }
    private static void handleAutoRepair(Player player, ItemStack stack) {
        if (stack.getItem() instanceof ObsidianShieldItem && stack.isDamaged()) {
            if (player.tickCount % 200 == 0) {
                stack.setDamageValue(stack.getDamageValue() - 1);
            }
        }
    }
    private static boolean hasShield(LivingEntity entity) {
        if (!(entity instanceof Player player)) return false;

        // 检查主副手
        boolean inHand = player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof ObsidianShieldItem ||
                player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof ObsidianShieldItem;

        // 检查饰品栏
        boolean inCurio = CuriosApi.getCuriosInventory(player).resolve()
                .map(inv -> inv.findFirstCurio(stack ->
                        stack.getItem() instanceof ObsidianShieldItem).isPresent())
                .orElse(false);

        return inHand || inCurio;
    }

    // 火焰伤害类型检测
    private static boolean isFireDamage(DamageSource source) {
        return source.is(DamageTypes.IN_FIRE) ||
                source.is(DamageTypes.ON_FIRE) ||
                source.is(DamageTypes.FIREBALL) ||
                source.is(DamageTypes.UNATTRIBUTED_FIREBALL);
    }

    // 岩浆伤害检测
    private static boolean isLavaDamage(DamageSource source) {
        return source.is(DamageTypes.LAVA) ||
                source.is(DamageTypes.HOT_FLOOR);
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.startUsingItem(hand); // 关键调用
        return InteractionResultHolder.consume(stack);
    }
    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK; // 必须为BLOCK动画类型
    }
    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000; // 保持与原版盾牌一致
    }


    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return ToolActions.DEFAULT_SHIELD_ACTIONS.contains(toolAction);
    }

    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.OFFHAND;
    }
    @Override
    public EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EquipmentSlot.OFFHAND;
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
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.obsidian_shield.effect")
                .withStyle(ChatFormatting.BLUE));
        super.appendHoverText(stack, level, tooltip, flag); // 调用基类方法显示修饰符信息
    }
}