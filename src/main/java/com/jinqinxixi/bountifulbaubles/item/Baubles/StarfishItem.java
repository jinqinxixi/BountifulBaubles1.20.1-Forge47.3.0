package com.jinqinxixi.bountifulbaubles.item.Baubles;

import com.jinqinxixi.bountifulbaubles.system.modifier.ModifiableBaubleItem;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber
public class StarfishItem extends ModifiableBaubleItem {

    public StarfishItem(Properties properties) {
        super(properties);
    }

    private static final Modifier[] MODIFIERS = Modifier.values();

    @Override
    public Modifier[] getModifiers() {
        return MODIFIERS;
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag unused) {
        return CuriosApi.createCurioProvider(new ICurio() {
            @Override
            public ItemStack getStack() {
                return stack;
            }

            @Override
            public void onEquip(SlotContext slotContext, ItemStack prevStack) {
                if (slotContext.entity() instanceof Player player) {
                    applyModifier(player, stack);
                }
            }

            @Override
            public void onUnequip(SlotContext slotContext, ItemStack newStack) {
                if (slotContext.entity() instanceof Player player) {
                    removeModifier(player, stack);
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

    // 处理服务端的伤害事件
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingAttack(LivingAttackEvent event) {
        if (!(event.getEntity() instanceof Player victim)) {
            return;
        }

        if (event.getSource().getEntity() instanceof Player attacker) {
            if (shouldPreventAttack(attacker, victim)) {
                event.setCanceled(true);
            }
        }
    }

    // 处理客户端的攻击事件（用于阻止粒子效果和攻击动画）
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerAttack(AttackEntityEvent event) {
        if (event.getTarget() instanceof Player && event.getEntity() instanceof Player) {
            Player victim = (Player) event.getTarget();
            Player attacker = (Player) event.getEntity();

            if (shouldPreventAttack(attacker, victim)) {
                event.setCanceled(true);
            }
        }
    }

    // 检查是否应该阻止攻击的辅助方法
    private static boolean shouldPreventAttack(Player attacker, Player victim) {
        boolean victimHasStarfish = CuriosApi.getCuriosInventory(victim)
                .resolve()
                .flatMap(handler -> handler.findFirstCurio(item -> item.getItem() instanceof StarfishItem))
                .isPresent();

        boolean attackerHasStarfish = CuriosApi.getCuriosInventory(attacker)
                .resolve()
                .flatMap(handler -> handler.findFirstCurio(item -> item.getItem() instanceof StarfishItem))
                .isPresent();

        return victimHasStarfish && attackerHasStarfish;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.starfish.effect")
                .withStyle(ChatFormatting.BLUE));
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public int getEnchantmentValue() {
        return 0;
    }
}