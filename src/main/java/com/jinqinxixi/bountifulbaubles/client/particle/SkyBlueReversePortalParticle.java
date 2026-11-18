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
 * 天蓝色逆向传送门粒子。
 * <p>
 * 1.20.1 的 {@code ReversePortalParticle} 构造函数是包私有，无法跨包继承，
 * 因此这里继承 {@link PortalParticle} 并复制逆向传送门的运动逻辑（线性向外扩散）。
 */
@OnlyIn(Dist.CLIENT)
public class SkyBlueReversePortalParticle extends PortalParticle {

    protected SkyBlueReversePortalParticle(
            ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed
    ) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.setColor(0.53f, 0.81f, 0.92f);
        this.quadSize *= 1.5F;
        this.lifetime = (int) (Math.random() * 2.0) + 60;
    }

    @Override
    public float getQuadSize(float scaleFactor) {
        float f = 1.0F - ((float) this.age + scaleFactor) / ((float) this.lifetime * 1.5F);
        return this.quadSize * f;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            float f = (float) this.age / (float) this.lifetime;
            this.x = this.x + this.xd * (double) f;
            this.y = this.y + this.yd * (double) f;
            this.z = this.z + this.zd * (double) f;
            this.setPos(this.x, this.y, this.z);
        }
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
            SkyBlueReversePortalParticle particle = new SkyBlueReversePortalParticle(level, x, y, z, xSpeed, ySpeed, zSpeed);
            particle.pickSprite(this.sprite);
            return particle;
        }
    }
}
