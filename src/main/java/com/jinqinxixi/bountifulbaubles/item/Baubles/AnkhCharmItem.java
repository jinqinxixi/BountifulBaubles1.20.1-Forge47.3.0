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
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;
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
public class AnkhCharmItem extends ModifiableBaubleItem {

    // 明确引用基类中的 Modifier 枚举
    private static final ModifiableBaubleItem.Modifier[] MODIFIERS = ModifiableBaubleItem.Modifier.values();

    @Override
    public Modifier[] getModifiers() {
        return MODIFIERS;
    }


    public AnkhCharmItem(Properties pProperties) {
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
                LivingEntity entity = slotContext.entity();
                if (entity instanceof Player player) {
                    // 父类修饰符逻辑
                    removeModifier(player, stack);
                }
            }

            @Override
            public boolean canEquip(SlotContext slotContext) {
                return true;
            }
        });
    }

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
                boolean hasAnkhCharm = CuriosApi.getCuriosInventory(player)
                        .resolve()
                        .map(curios -> curios.findFirstCurio(stack ->
                                stack.getItem() instanceof AnkhCharmItem).isPresent())
                        .orElse(false);

                if (hasAnkhCharm) {
                    event.setResult(Event.Result.DENY);
                }
            }
        }
    }


    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.ankh_charm.effect").withStyle(ChatFormatting.BLUE));
        super.appendHoverText(stack, level, tooltip, flag); // 调用基类方法显示修饰符信息
    }

}
