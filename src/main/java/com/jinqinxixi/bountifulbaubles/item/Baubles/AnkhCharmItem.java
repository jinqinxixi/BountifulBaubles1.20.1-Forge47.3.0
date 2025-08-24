package com.jinqinxixi.bountifulbaubles.item.Baubles;

import com.jinqinxixi.bountifulbaubles.system.modifier.ModifiableBaubleItem;
import com.jinqinxixi.bountifulbaubles.util.BaubleUtils;
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
import net.minecraftforge.event.entity.living.LivingEvent;
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
                    for (MobEffect effect : ModConfig.ankh_debuffs) {
                    	if (player.hasEffect(effect)) {
                    		player.removeEffect(effect);
                    	}
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
            if (effect != null && (ModConfig.ankh_debuffs.contains(effect.getEffect()))) {
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
    @SubscribeEvent
    public static void onLivingUpdate(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
        	Player player = event.player;
            CuriosApi.getCuriosInventory(player).ifPresent(curios -> {
                if (curios.findFirstCurio(stack ->
                        stack.getItem() instanceof AnkhCharmItem).isPresent()) {
                    BaubleUtils.destroyNearbyWebs(player);
                }
            });
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.ankh_charm.effect").withStyle(ChatFormatting.BLUE));
        super.appendHoverText(stack, level, tooltip, flag); // 调用基类方法显示修饰符信息
    }

}
