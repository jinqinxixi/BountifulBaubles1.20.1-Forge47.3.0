package com.jinqinxixi.bountifulbaubles.event.listener;

import com.jinqinxixi.bountifulbaubles.BountifulBaublesMod;
import com.jinqinxixi.bountifulbaubles.item.Baubles.DarkEggItem;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

@Mod.EventBusSubscriber(modid = BountifulBaublesMod.MOD_ID)
public class DarkEggEvents {
    @SubscribeEvent
    public static void onVexTarget(LivingChangeTargetEvent event) {
        if (event.getEntity() instanceof Vex vex) {
            // 如果是友好恼鬼，阻止它攻击召唤者
            if (vex.getPersistentData().getBoolean(DarkEggItem.FRIENDLY_VEX_TAG) &&
                    event.getNewTarget() instanceof Player player) {
                // 检查玩家是否装备暗黑之蛋
                if (CuriosApi.getCuriosInventory(player).resolve()
                        .flatMap(curios -> curios.findFirstCurio(stack ->
                                stack.getItem() instanceof DarkEggItem))
                        .isPresent()) {
                    event.setCanceled(true);
                    vex.setTarget(null);
                }
            }
        }
    }
}