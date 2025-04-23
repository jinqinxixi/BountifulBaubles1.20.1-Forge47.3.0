package com.jinqinxixi.bountifulbaubles.mixin;

import com.jinqinxixi.bountifulbaubles.config.ModifierConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import java.util.List;
import top.theillusivec4.curios.client.ClientEventHandler;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

@Mixin(ClientEventHandler.class)
public class CuriosTooltipMixin {

    @ModifyVariable(
            method = "onTooltip",
            at = @At("STORE"),
            ordinal = 1,
            remap = false
    )
    private List<Component> modifyAttributeTooltip(List<Component> attributeTooltip, ItemTooltipEvent evt) {
        ItemStack stack = evt.getItemStack();
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        String itemKey = itemId.toString();

        ModifierConfig config = ModifierConfig.load();
        if (config.isItemModifiable(itemKey)) {
            return List.of(); // 如果物品在配置中，返回空列表
        }
        return attributeTooltip; // 否则保持原样
    }
}