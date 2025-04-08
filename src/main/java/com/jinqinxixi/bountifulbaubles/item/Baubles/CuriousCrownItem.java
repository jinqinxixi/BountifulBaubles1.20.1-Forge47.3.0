package com.jinqinxixi.bountifulbaubles.item.Baubles;

import com.jinqinxixi.bountifulbaubles.system.modifier.ModifiableBaubleItem;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.event.CurioUnequipEvent;

import javax.annotation.Nullable;
import java.util.List;

public class CuriousCrownItem extends ModifiableBaubleItem {

    private static final Modifier[] MODIFIERS = Modifier.values();
    private static final String TAG_WEARING = "bountifulbaubles:wearing_crown";
    private static final int CHECK_INTERVAL = 20;
    private static final int EFFECT_DURATION = Integer.MAX_VALUE;

    public CuriousCrownItem(Properties properties) {
        super(properties);
    }

    @Override
    public Modifier[] getModifiers() {
        return MODIFIERS;
    }

    @Mod.EventBusSubscriber(modid = "bountifulbaubles", bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class EventHandler {
        @SubscribeEvent
        public static void onCurioUnequip(CurioUnequipEvent event) {
                ItemStack stack = event.getStack();
            if (stack.getItem() instanceof CuriousCrownItem) {
                Player player = (Player) event.getEntity();
                player.removeEffect(MobEffects.NIGHT_VISION);
                player.getPersistentData().remove(TAG_WEARING);
            }
        }

        @SubscribeEvent
        public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
            if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide()) return;

            Player player = event.player;
            if (player.tickCount % CHECK_INTERVAL != 0) return;

            boolean hasCrown = CuriosApi.getCuriosInventory(player)
                    .map(inv -> inv.findFirstCurio(stack ->
                            stack.getItem() instanceof CuriousCrownItem).isPresent())
                    .orElse(false);

            updateNightVision(player, hasCrown);
        }

        private static void updateNightVision(Player player, boolean hasCrown) {
            CompoundTag data = player.getPersistentData();
            boolean wasWearing = data.getBoolean(TAG_WEARING);

            if (hasCrown) {
                if (!wasWearing || needsEffectRefresh(player)) {
                    applyNightVision(player);
                    data.putBoolean(TAG_WEARING, true);
                }
            } else if (wasWearing) {
                data.remove(TAG_WEARING);
            }
        }

        private static boolean needsEffectRefresh(Player player) {
            MobEffectInstance effect = player.getEffect(MobEffects.NIGHT_VISION);
            return effect == null || effect.getDuration() <= CHECK_INTERVAL * 2;
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
    }

    @Override
    public int getEnchantmentValue() {
        return 0;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.curious_crown.effect")
                .withStyle(ChatFormatting.BLUE));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
