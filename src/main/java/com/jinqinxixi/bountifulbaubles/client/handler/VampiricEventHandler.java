package com.jinqinxixi.bountifulbaubles.client.handler;

import com.jinqinxixi.bountifulbaubles.config.ModConfig;
import com.jinqinxixi.bountifulbaubles.item.Baubles.VampiricGloveItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

@Mod.EventBusSubscriber
public class VampiricEventHandler {

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getSource().getDirectEntity() instanceof LivingEntity attacker) {
            // 修改为检测是否至少有一个手套（不叠加）
            if (hasAtLeastOneGlove(attacker)) {
                double percent = ModConfig.getVampiricPercent();
                double maxHeal = ModConfig.getVampiricMax();

                float damage = event.getAmount();
                float healAmount = (float) Math.min(damage * percent, maxHeal);

                attacker.heal(healAmount);
            }
        }
    }

    // 优化后的检测方法（只要存在至少一个即返回true，且不统计数量）
    private static boolean hasAtLeastOneGlove(LivingEntity entity) {
        return CuriosApi.getCuriosInventory(entity)
                .resolve()
                .map(handler -> {
                    for (String identifier : handler.getCurios().keySet()) {
                        ICurioStacksHandler stackHandler = handler.getCurios().get(identifier);
                        if (stackHandler != null) {
                            for (int i = 0; i < stackHandler.getSlots(); i++) {
                                if (stackHandler.getStacks().getStackInSlot(i).getItem() instanceof VampiricGloveItem) {
                                    return true; // 找到第一个立即返回
                                }
                            }
                        }
                    }
                    return false;
                })
                .orElse(false);
    }
}
