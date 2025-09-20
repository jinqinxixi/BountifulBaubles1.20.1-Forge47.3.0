package com.jinqinxixi.bountifulbaubles.item.Baubles;

import com.jinqinxixi.bountifulbaubles.system.modifier.ModifiableBaubleItem;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber
public class CuriousCrownItem extends ModifiableBaubleItem {
    private static final Modifier[] MODIFIERS = Modifier.values();
    private static final int EFFECT_DURATION = Integer.MAX_VALUE;

    public CuriousCrownItem(Properties properties) {
        super(properties);
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
                    // 父类逻辑：应用属性修正
                    applyModifier(player, stack);
                    // 子类逻辑：应用夜视效果
                    applyNightVision(player);
                }
            }

            @Override
            public void onUnequip(SlotContext slotContext, ItemStack newStack) {
                LivingEntity entity = slotContext.entity();
                if (entity instanceof Player player) {
                    // 父类逻辑：移除属性修正
                    removeModifier(player, stack);
                    // 子类逻辑：移除夜视效果
                    player.removeEffect(MobEffects.NIGHT_VISION);
                }
            }

            @Override
            public boolean canEquip(SlotContext slotContext) {
                return true;
            }

            // 每tick检查效果
            @Override
            public void curioTick(SlotContext slotContext) {
                LivingEntity entity = slotContext.entity();
                if (entity instanceof Player player) {
                    applyNightVision(player);
                }
            }
        });
    }

    private static void applyNightVision(Player player) {
        player.addEffect(new MobEffectInstance(
                MobEffects.NIGHT_VISION,
                EFFECT_DURATION,
                0,
                true, true, false
        ) {
            @Override
            public boolean isCurativeItem(ItemStack stack) {
                return false;
            }
        });
    }

    // ===== 防附魔核心代码 =====
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public int getEnchantmentValue() {
        return 0;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.curious_crown.effect")
                .withStyle(ChatFormatting.BLUE));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}