package com.jinqinxixi.bountifulbaubles.item.Baubles;

import com.jinqinxixi.bountifulbaubles.system.modifier.ModifiableBaubleItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EmberItem extends ModifiableBaubleItem {

    private static final ModifiableBaubleItem.Modifier[] MODIFIERS = ModifiableBaubleItem.Modifier.values();
    private static BlockPos lastMagmaPos = null;

    public EmberItem(Properties properties) {
        super(properties.stacksTo(1).fireResistant());
    }

    @Override
    public ModifiableBaubleItem.Modifier[] getModifiers() {
        return MODIFIERS;
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (slotContext.entity() instanceof Player player) {
            if (!player.hasEffect(MobEffects.FIRE_RESISTANCE)) {
                player.addEffect(new MobEffectInstance(
                        MobEffects.FIRE_RESISTANCE,
                        Integer.MAX_VALUE,
                        0,
                        false,
                        false
                ));
            }
            player.clearFire();
        }
    }

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        CuriosApi.getCuriosInventory(player).ifPresent(handler -> {
            handler.findFirstCurio(item -> item.getItem() instanceof EmberItem).ifPresent(result -> {
                Level level = player.level();
                BlockPos currentPos = player.blockPosition().below();

                // 如果上一个岩浆块位置存在且不是当前位置，则还原为岩浆
                if (lastMagmaPos != null && !lastMagmaPos.equals(currentPos)) {
                    if (level.getBlockState(lastMagmaPos).is(Blocks.MAGMA_BLOCK)) {
                        level.setBlock(lastMagmaPos, Blocks.LAVA.defaultBlockState(), 3);
                    }
                    lastMagmaPos = null;
                }

                // 如果玩家脚下是岩浆，则生成岩浆块
                if (level.getBlockState(currentPos).is(Blocks.LAVA)) {
                    level.setBlock(currentPos, Blocks.MAGMA_BLOCK.defaultBlockState(), 3);
                    lastMagmaPos = currentPos.immutable();
                }

                player.clearFire();
            });
        });
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        // 调用父类的onEquip方法，处理修饰符
        super.onEquip(slotContext, prevStack, stack);

    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (slotContext.entity() instanceof Player player) {
            removeModifier(player, stack);
            player.removeEffect(MobEffects.FIRE_RESISTANCE);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.ember.effect")
                .withStyle(ChatFormatting.BLUE));

        tooltip.add(Component.translatable("tooltip.bountifulbaubles.ember.effect1")
                .withStyle(ChatFormatting.BLUE));
        super.appendHoverText(stack, level, tooltip, flag);
    }
    // 1. 禁止铁砧/指令附魔
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    // 2. 附魔等级设为0（防止附魔台操作）
    @Override
    public int getEnchantmentValue() {
        return 0;
    }
}