package com.jinqinxixi.bountifulbaubles.item.Baubles;

import com.jinqinxixi.bountifulbaubles.system.modifier.ModifiableBaubleItem;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber
public class ObsidianSkullItem extends ModifiableBaubleItem {

    private static final ModifiableBaubleItem.Modifier[] MODIFIERS = ModifiableBaubleItem.Modifier.values();
    @Override
    public ModifiableBaubleItem.Modifier[] getModifiers() {
        return MODIFIERS;
    }

    public ObsidianSkullItem(Properties properties) {
        super(properties);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag unused) {
        return super.initCapabilities(stack, unused); // 调用父类方法
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
    @Override
    public int getEnchantmentValue() {
        return 0; // 附魔等级为0
    }

    // 禁止任何形式的附魔（包括铁砧）
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }
    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();

        // 阻止点燃状态
        if (isFireDamage(source) && hasSkull(entity)) {
            entity.clearFire();
        }
    }

    private static void checkAndApplyProtection(LivingEntity entity, LivingHurtEvent event) {
        if (hasSkull(entity)) {
            // 减少50%伤害
            event.setAmount(event.getAmount() * 0.5F);
        }
    }

    // 检测是否佩戴黑曜石头骨
    private static boolean hasSkull(LivingEntity entity) {
        if (!(entity instanceof Player player)) return false;

        return CuriosApi.getCuriosInventory(player).resolve()
                .map(inv -> inv.findFirstCurio(stack ->
                        stack.getItem() instanceof ObsidianSkullItem).isPresent())
                .orElse(false);
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
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.obsidian_skull.effect")
                .withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.obsidian_skull.description")
                .withStyle(ChatFormatting.GREEN));
        super.appendHoverText(stack, level, tooltip, flag); // 调用基类方法显示修饰符信息
    }
}