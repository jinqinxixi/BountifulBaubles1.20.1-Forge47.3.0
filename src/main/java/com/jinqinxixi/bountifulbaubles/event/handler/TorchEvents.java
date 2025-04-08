package com.jinqinxixi.bountifulbaubles.event.handler;

import com.jinqinxixi.bountifulbaubles.item.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class TorchEvents {
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.player.level().isClientSide) {
            CuriosApi.getCuriosInventory(event.player).ifPresent(curios -> {
                curios.findCurios(ModItems.AUTO_TORCH.get()).forEach(slotResult -> {
                    ItemStack stack = slotResult.stack();
                    if (stack.getItem() instanceof ICurioItem curioItem) {
                        curioItem.curioTick(new SlotContext(
                                slotResult.slotContext().identifier(),
                                event.player,
                                slotResult.slotContext().index(),
                                false,
                                true
                        ), stack);
                    }
                });
            });
        }
    }
}
