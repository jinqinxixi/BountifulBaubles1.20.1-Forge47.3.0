package com.jinqinxixi.bountifulbaubles.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.PortalParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 天蓝色传送门粒子：继承原版 PortalParticle，仅改颜色。
 * 纹理沿用 generic_*（白色），所以 setColor 染色即为纯天蓝，不会混色。
 */
@OnlyIn(Dist.CLIENT)
public class SkyBluePortalParticle extends PortalParticle {

    protected SkyBluePortalParticle(
            ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed
    ) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.setColor(0.53f, 0.81f, 0.92f);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet sprite) {
            this.sprite = sprite;
        }

        @Override
        public Particle createParticle(
                SimpleParticleType type,
                ClientLevel level,
                double x,
                double y,
                double z,
                double xSpeed,
                double ySpeed,
                double zSpeed
        ) {
            SkyBluePortalParticle particle = new SkyBluePortalParticle(level, x, y, z, xSpeed, ySpeed, zSpeed);
            particle.pickSprite(this.sprite);
            return particle;
        }
    }
}
