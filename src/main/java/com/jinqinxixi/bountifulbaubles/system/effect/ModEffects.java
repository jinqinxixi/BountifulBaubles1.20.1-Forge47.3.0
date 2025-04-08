package com.jinqinxixi.bountifulbaubles.system.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.jinqinxixi.bountifulbaubles.BountifulBaublesMod.MOD_ID;

public class ModEffects {
    // 效果注册器
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MOD_ID);

    // 药水注册器
    public static final DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(ForgeRegistries.POTIONS, MOD_ID);

    // 注册原罪效果
    public static final RegistryObject<MobEffect> SINFUL = EFFECTS.register(
            "sinful",
            SinfulEffect::new
    );


    // 注册原罪药水（基础版）
    public static final RegistryObject<Potion> SINFUL_POTION = POTIONS.register(
            "sinful_potion",
            () -> new Potion(new MobEffectInstance(SINFUL.get(), 3600, 0)) // 默认1小时I级
    );

    // 注册长效版药水
    public static final RegistryObject<Potion> LONG_SINFUL_POTION = POTIONS.register(
            "long_sinful_potion",
            () -> new Potion(new MobEffectInstance(SINFUL.get(), 9600, 0)) // 延长至2小时40分
    );

    // 注册强化版药水
    public static final RegistryObject<Potion> STRONG_SINFUL_POTION = POTIONS.register(
            "strong_sinful_potion",
            () -> new Potion(new MobEffectInstance(SINFUL.get(), 1800, 1)) // II级30分钟
    );

    // 注册方法
    public static void register(IEventBus eventBus) {
        EFFECTS.register(eventBus);
        POTIONS.register(eventBus);
    }
}
