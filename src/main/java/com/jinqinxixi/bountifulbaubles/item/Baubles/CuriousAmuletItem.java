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
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber
public class CuriousAmuletItem extends ModifiableBaubleItem {
    private static final Modifier[] MODIFIERS = Modifier.values();
    private static final MobEffect TARGET_EFFECT = MobEffects.REGENERATION;
    private static final int EFFECT_DURATION = Integer.MAX_VALUE;

    public CuriousAmuletItem(Properties properties) {
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
                    // 子类逻辑：应用生命恢复效果
                    applyRegeneration(player);
                }
            }

            @Override
            public void onUnequip(SlotContext slotContext, ItemStack newStack) {
                LivingEntity entity = slotContext.entity();
                if (entity instanceof Player player) {
                    // 父类逻辑：移除属性修正
                    removeModifier(player, stack);
                    // 子类逻辑：移除生命恢复效果
                    player.removeEffect(TARGET_EFFECT);
                }
            }

            @Override
            public boolean canEquip(SlotContext slotContext) {
                return true;
            }

            // 每tick给予效果
            @Override
            public void curioTick(SlotContext slotContext) {
                LivingEntity entity = slotContext.entity();
                if (entity instanceof Player player) {
                    applyRegeneration(player);
                }
            }
        });
    }

    private static void applyRegeneration(Player player) {
        player.addEffect(new MobEffectInstance(
                TARGET_EFFECT,
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

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.curious_amulet.effect")
                .withStyle(ChatFormatting.BLUE));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}