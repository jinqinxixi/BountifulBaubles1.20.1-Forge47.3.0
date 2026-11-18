package com.jinqinxixi.bountifulbaubles.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 纯天蓝粉尘粒子：复制原版 DustParticle 的运动/大小逻辑，但颜色固定纯天蓝，
 * 不经过 randomizeColor 的 RGB 三通道独立扰动（那会让天蓝里混入紫色）。
 */
@OnlyIn(Dist.CLIENT)
public class SkyBlueDustParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    protected SkyBlueDustParticle(
            ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites
    ) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.friction = 0.96F;
        this.speedUpWhenYMotionIsBlocked = true;
        this.sprites = sprites;
        this.xd *= 0.1F;
        this.yd *= 0.1F;
        this.zd *= 0.1F;
        this.quadSize = this.quadSize * 0.75F * 0.5F; // 大小 0.5
        this.lifetime = (int) Math.max((float) (8.0 / (this.random.nextDouble() * 0.8 + 0.2)) * 0.5F, 1.0F);
        this.setColor(0.53f, 0.81f, 0.92f); // 固定纯天蓝
        this.setSpriteFromAge(sprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public float getQuadSize(float scaleFactor) {
        return this.quadSize * Mth.clamp(((float) this.age + scaleFactor) / (float) this.lifetime * 32.0F, 0.0F, 1.0F);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
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
            return new SkyBlueDustParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprite);
        }
    }
}
