package com.jinqinxixi.bountifulbaubles.item.Baubles;

import com.jinqinxixi.bountifulbaubles.system.modifier.ModifiableBaubleItem;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
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

public class CuriousAmuletItem extends ModifiableBaubleItem {
    private static final Modifier[] MODIFIERS = Modifier.values();
    private static final String TAG_AMULET = "bountifulbaubles:amulet_equipped";
    private static final MobEffect TARGET_EFFECT = MobEffects.REGENERATION;
    private static final int EFFECT_DURATION = Integer.MAX_VALUE;

    @Override
    public Modifier[] getModifiers() {
        return MODIFIERS;
    }

    public CuriousAmuletItem(Properties properties) {
        super(properties);
    }

    // ========== 事件处理器 ==========
    @Mod.EventBusSubscriber(modid = "bountifulbaubles", bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class CurioEffectHandler {

        @SubscribeEvent
        public static void onCurioUnequip(CurioUnequipEvent event) {
            if (event.getStack().getItem() instanceof CuriousAmuletItem) {
                Player player = (Player) event.getEntity();
                CompoundTag data = player.getPersistentData();

                if (data.getBoolean(TAG_AMULET)) {
                    player.removeEffect(TARGET_EFFECT); // 精确移除效果 [^1]
                    data.remove(TAG_AMULET); // 清理状态标记 [^2]
                }
            }
        }

        @SubscribeEvent
        public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
            if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide()) return;

            Player player = event.player;
            boolean hasAmulet = CuriosApi.getCuriosInventory(player)
                    .map(inv -> inv.findFirstCurio(stack ->
                            stack.getItem() instanceof CuriousAmuletItem).isPresent())
                    .orElse(false);

            if (hasAmulet) {
                handleEffectApplication(player);
            }
        }

        private static void handleEffectApplication(Player player) {
            MobEffectInstance current = player.getEffect(TARGET_EFFECT);
            CompoundTag data = player.getPersistentData();

            if (current == null || current.getDuration() <= 40) {
                player.addEffect(new MobEffectInstance(
                        TARGET_EFFECT,
                        EFFECT_DURATION,
                        0,
                        true, true, true // 启用效果同步 [^3]
                ) {
                    @Override
                    public boolean isCurativeItem(ItemStack stack) {
                        return false;
                    }
                });
                data.putBoolean(TAG_AMULET, true); // 更新佩戴状态 [^2]
            }
        }
    }

    // ========== 工具提示 ==========
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.curious_amulet.effect")
                .withStyle(ChatFormatting.BLUE));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
