package com.jinqinxixi.bountifulbaubles.item.Baubles;

import com.jinqinxixi.bountifulbaubles.system.modifier.ModifiableBaubleItem;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BezoarItem extends ModifiableBaubleItem {
    private static final ModifiableBaubleItem.Modifier[] MODIFIERS = ModifiableBaubleItem.Modifier.values();
    @Override
    public ModifiableBaubleItem.Modifier[] getModifiers() {
        return MODIFIERS;
    }

    public BezoarItem(Properties properties) {
        super(properties);
    }
        @Override
        public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag unused) {
            return CuriosApi.createCurioProvider(new ICurio() {
                @Override
                public ItemStack getStack() {
                    return stack;
                }

                // ===== 关键修复：集成父类逻辑 =====
                @Override
                public void onEquip(SlotContext slotContext, ItemStack prevStack) {
                    LivingEntity entity = slotContext.entity();
                    if (entity instanceof Player player) {
                        // 先执行父类属性修正
                        applyModifier(player, stack);
                        // 立即移除中毒效果
                        if (player.hasEffect(MobEffects.POISON)) {
                            player.removeEffect(MobEffects.POISON);
                        }
                    }
                }

                @Override
                public void onUnequip(SlotContext slotContext, ItemStack newStack) {
                    LivingEntity entity = slotContext.entity();
                    if (entity instanceof Player player) {
                        // 先移除父类属性修正
                        removeModifier(player, stack);
                    }
                }

                // ===== 必须实现的方法 =====
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

    @Override
    public int getEnchantmentValue() {
        return 0; // 附魔等级为0
    }

    // 禁止任何形式的附魔（包括铁砧）
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        if (event.getEntity() instanceof Player player) {
            MobEffectInstance effect = event.getEffectInstance();

            // 检测中毒效果
            if (effect != null && effect.getEffect() == MobEffects.POISON) {  // 直接比较效果
                boolean hasBezoar = CuriosApi.getCuriosInventory(player)
                        .resolve()
                        .map(curios -> curios.findFirstCurio(stack ->
                                stack.getItem() instanceof BezoarItem).isPresent())
                        .orElse(false);

                if (hasBezoar) {
                    event.setResult(Event.Result.DENY);
                    // 确保移除当前的中毒效果
                    if (player.hasEffect(MobEffects.POISON)) {
                        player.removeEffect(MobEffects.POISON);
                    }
                }
            }
        }
    }


    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.bezoar.effect").withStyle(ChatFormatting.BLUE));
        super.appendHoverText(stack, level, tooltip, flag); // 调用基类方法显示修饰符信息
    }
}