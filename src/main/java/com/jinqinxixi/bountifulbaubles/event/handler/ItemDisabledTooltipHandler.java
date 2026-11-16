package com.jinqinxixi.bountifulbaubles.event.handler;

import com.jinqinxixi.bountifulbaubles.config.ItemConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 被禁用的饰品在物品描述里显示「已禁用」。
 *
 * <p>开关在 {@code bountifulbaubles-items.toml} 里，每个饰品一项。
 * tooltip 属于纯渲染逻辑，只在客户端注册即可。
 */
@Mod.EventBusSubscriber(modid = "bountifulbaubles", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ItemDisabledTooltipHandler
{
    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event)
    {
        if (ItemConfig.isDisabled(event.getItemStack()))
        {
            event.getToolTip().add(
                    Component.translatable("tooltip.bountifulbaubles.disabled")
                            .withStyle(ChatFormatting.RED));
        }
    }
}
