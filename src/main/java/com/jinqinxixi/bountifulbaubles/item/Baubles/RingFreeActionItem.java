package com.jinqinxixi.bountifulbaubles.item.Baubles;

import com.jinqinxixi.bountifulbaubles.system.modifier.ModifiableBaubleItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.entity.living.LivingEvent;
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
public class RingFreeActionItem extends ModifiableBaubleItem {

    private static final ModifiableBaubleItem.Modifier[] MODIFIERS = ModifiableBaubleItem.Modifier.values();
    @Override
    public ModifiableBaubleItem.Modifier[] getModifiers() {
        return MODIFIERS;
    }

    // 需要免疫的效果列表
    private static final MobEffect[] BLOCKED_EFFECTS = {
            MobEffects.MOVEMENT_SLOWDOWN, // 缓慢
            MobEffects.LEVITATION         // 飘浮
    };

    public RingFreeActionItem(Properties properties) {
        super(properties);
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
                if (slotContext.entity() instanceof Player player) {
                    applyModifier(player, stack);   // 父类属性
                    clearBlockedEffects(player);
                }
            }

            @Override
            public void onUnequip(SlotContext slotContext, ItemStack newStack) {
                if (slotContext.entity() instanceof Player player) {
                    removeModifier(player, stack);  // 父类属性
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

    @Override
    public int getEnchantmentValue() {
        return 0; // 附魔等级为0
    }


    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        if (event.getEntity() instanceof Player player) {
            MobEffectInstance effect = event.getEffectInstance();

            if (effect != null && isBlockedEffect(effect.getEffect())) {
                CuriosApi.getCuriosInventory(player).ifPresent(curios -> {
                    if (curios.findFirstCurio(stack ->
                            stack.getItem() instanceof RingFreeActionItem).isPresent()) {
                        event.setResult(net.minecraftforge.eventbus.api.Event.Result.DENY);
                        clearBlockedEffects(player);
                    }
                });
            }
        }
    }

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingTickEvent event) {
        if (event.getEntity() instanceof Player player) {
            CuriosApi.getCuriosInventory(player).ifPresent(curios -> {
                if (curios.findFirstCurio(stack ->
                        stack.getItem() instanceof RingFreeActionItem).isPresent()) {

                    // 检测玩家周围的蜘蛛网并破坏
                    BlockPos pos = player.blockPosition();
                    Level level = player.level();

                    // 检测玩家周围的方块
                    for (int x = -1; x <= 1; x++) {
                        for (int y = -1; y <= 1; y++) {
                            for (int z = -1; z <= 1; z++) {
                                BlockPos blockPos = pos.offset(x, y, z);
                                if (level.getBlockState(blockPos).is(Blocks.COBWEB)) {
                                    level.destroyBlock(blockPos, true); // 破坏蜘蛛网并掉落物品
                                }
                            }
                        }
                    }

                    // 持续清除效果
                    clearBlockedEffects(player);
                }
            });
        }
    }

    // 清除需要免疫的效果
    private static void clearBlockedEffects(Player player) {
        for (MobEffect effect : BLOCKED_EFFECTS) {
            player.removeEffect(effect);
        }
    }

    // 检测是否为需要免疫的效果
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
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.ring_free_action.effect")
                .withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.ring_free_action.effect1")
                .withStyle(ChatFormatting.BLUE));
        super.appendHoverText(stack, level, tooltip, flag); // 调用基类方法显示修饰符信息
    }
}