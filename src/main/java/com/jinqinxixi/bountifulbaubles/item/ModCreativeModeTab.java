package com.jinqinxixi.bountifulbaubles.item;

import com.jinqinxixi.bountifulbaubles.BountifulBaublesMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTab {
    public static final String BOUNTIFUL_BAUBLES_TAB_STRING = "creativetab.bountiful_baubles_tab";

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BountifulBaublesMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> BOUNTIFUL_BAUBLES_TAB = CREATIVE_MODE_TABS.register("bountiful_baubles_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.BROKEN_HEART.get()))
                    .title(Component.translatable(BOUNTIFUL_BAUBLES_TAB_STRING))
                    .displayItems((parameters, output) -> {
                        // 自动添加所有在 ModItem 中注册的物品
                        ModItems.ITEMS.getEntries().forEach(item -> {
                            output.accept(item.get());
                        });
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}