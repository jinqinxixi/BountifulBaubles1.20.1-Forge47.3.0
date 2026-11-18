package com.jinqinxixi.bountifulbaubles.particle;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, "bountifulbaubles");

    // 天蓝色传送门粒子（魔镜专用，继承原版 PortalParticle 改色）
    public static final RegistryObject<SimpleParticleType> SKY_BLUE_PORTAL =
            PARTICLE_TYPES.register("sky_blue_portal", () -> new SimpleParticleType(false));

    // 天蓝色逆向传送门粒子（魔镜专用，继承原版 ReversePortalParticle 改色）
    public static final RegistryObject<SimpleParticleType> SKY_BLUE_REVERSE_PORTAL =
            PARTICLE_TYPES.register("sky_blue_reverse_portal", () -> new SimpleParticleType(false));

    // 纯天蓝粉尘粒子（魔镜蓄力专用，固定纯色，避免 DustParticle 的 RGB 随机扰动混入紫色）
    public static final RegistryObject<SimpleParticleType> SKY_BLUE_DUST =
            PARTICLE_TYPES.register("sky_blue_dust", () -> new SimpleParticleType(false));
}
