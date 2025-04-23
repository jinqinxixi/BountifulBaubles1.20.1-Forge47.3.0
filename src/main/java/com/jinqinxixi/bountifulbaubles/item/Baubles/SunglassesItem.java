package com.jinqinxixi.bountifulbaubles.item.Baubles;

import com.jinqinxixi.bountifulbaubles.system.modifier.ModifiableBaubleItem;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber
public class SunglassesItem extends ModifiableBaubleItem {

    private static final ModifiableBaubleItem.Modifier[] MODIFIERS = ModifiableBaubleItem.Modifier.values();
    @Override
    public ModifiableBaubleItem.Modifier[] getModifiers() {
        return MODIFIERS;
    }

    // 需要免疫的效果列表（1.20.1版本）
    private static final MobEffect[] BLOCKED_EFFECTS = {
            MobEffects.BLINDNESS,  // ID 15
            MobEffects.DARKNESS     // ID 28（1.19+新增）
    };
    @Override
    public int getEnchantmentValue() {
        return 0; // 附魔等级为0
    }

    // 禁止任何形式的附魔（包括铁砧）
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }
    public SunglassesItem(Properties pProperties) {
        super(pProperties);
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
                LivingEntity entity = slotContext.entity();
                if (entity instanceof Player player) {
                    applyModifier(player, stack);  // 应用属性修饰符
                    clearBlockedEffects(player);   // 清除负面效果
                }
            }

            @Override
            public void onUnequip(SlotContext slotContext, ItemStack newStack) {
                LivingEntity entity = slotContext.entity();
                if (entity instanceof Player player) {
                    removeModifier(player, stack); // 移除属性修饰符
                }
            }

            // ===== 必须实现的接口方法 =====
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

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        if (event.getEntity() instanceof Player player) {
            MobEffectInstance effect = event.getEffectInstance();

            if (effect != null && isBlockedEffect(effect.getEffect())) {
                CuriosApi.getCuriosInventory(player).ifPresent(curios -> {
                    if (curios.findFirstCurio(stack ->
                            stack.getItem() instanceof SunglassesItem).isPresent()) {
                        event.setResult(net.minecraftforge.eventbus.api.Event.Result.DENY);
                        clearBlockedEffects(player);
                    }
                });
            }
        }
    }

    private static void clearBlockedEffects(Player player) {
        for (MobEffect effect : BLOCKED_EFFECTS) {
            player.removeEffect(effect);
        }
    }

    private static boolean isBlockedEffect(MobEffect effect) {
        for (MobEffect blocked : BLOCKED_EFFECTS) {
            if (effect == blocked) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {

        tooltip.add(Component.translatable("tooltip.bountifulbaubles.sunglasses.effect")
                .withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.sunglasses.description")
                .withStyle(ChatFormatting.GREEN));
        super.appendHoverText(stack, level, tooltip, flag); // 调用基类方法显示修饰符信息
    }
}
