package com.jinqinxixi.bountifulbaubles.network.handler;

import com.jinqinxixi.bountifulbaubles.item.Baubles.InfiniteTotemOfUndyingItem;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientPacketHandler {
    public static void handleInfiniteTotemCooldown(NetworkHandler.InfiniteTotemCooldownPacket packet) {
        Player clientPlayer = Minecraft.getInstance().player;
        if (clientPlayer != null) {
            CompoundTag data = clientPlayer.getPersistentData();
            if (packet.cooldown > 0) {
                data.putInt(InfiniteTotemOfUndyingItem.PUBLIC_COOLDOWN_TAG, packet.cooldown);
            } else {
                data.remove(InfiniteTotemOfUndyingItem.PUBLIC_COOLDOWN_TAG);
            }
        }
    }

    public static void spawnTotemParticles(double x, double y, double z) {
        Minecraft minecraft = Minecraft.getInstance();
        Level world = minecraft.level;
        if (world != null) {
            // ===== 核心参数配置 =====
            final int PARTICLE_COUNT = 250;       // 总粒子数（原150）
            final double BASE_SPEED = 1.8;       // 基础速度（原0.8）
            final double SPEED_VARIATION = 0.6;   // 速度随机变化量（原0.3）
            final double VERTICAL_BIAS = 0.2;    // 垂直方向偏重（原0.5）
            final double SPREAD_RADIUS = 0.5;    // 起始扩散半径（原0.2）

            // ===== 主粒子爆发 =====
            for (int i = 0; i < PARTICLE_COUNT; ++i) {
                // 生成随机角度（更均匀的球面分布）
                double theta = world.random.nextDouble() * 2 * Math.PI;
                double phi = Math.acos(1 - 2 * world.random.nextDouble()); // 均匀球面分布

                // 转换为方向向量
                double dirX = Math.sin(phi) * Math.cos(theta);
                double dirY = Math.sin(phi) * Math.sin(theta);
                double dirZ = Math.cos(phi);

                // 削弱垂直方向影响
                dirY = dirY * (1 - VERTICAL_BIAS);

                // 速度计算（更快更远）
                double speed = (BASE_SPEED + (world.random.nextDouble() * SPEED_VARIATION))
                        * (1 + world.random.nextDouble()); // 随机加速

                // 起始位置扩散（更宽的初始范围）
                double offset = SPREAD_RADIUS * world.random.nextDouble();
                double posX = x + dirX * offset;
                double posY = y + 1.0 + dirY * offset;
                double posZ = z + dirZ * offset;

                // 添加粒子
                world.addParticle(
                        ParticleTypes.TOTEM_OF_UNDYING,
                        posX, posY, posZ,
                        dirX * speed,
                        dirY * speed,
                        dirZ * speed
                );
            }

            // ===== 冲击波增强 =====
            final int SHOCKWAVE_COUNT = 80;       // 冲击波粒子数（原30）
            final double SHOCKWAVE_SPEED = 3.0;  // 冲击波速度（原1.5）

            for (int i = 0; i < SHOCKWAVE_COUNT; ++i) {
                double angle = world.random.nextDouble() * 2 * Math.PI;
                double speed = SHOCKWAVE_SPEED * (0.8 + world.random.nextDouble() * 0.4);

                world.addParticle(
                        ParticleTypes.FLAME,
                        x, y + 1.0, z,
                        Math.cos(angle) * speed,
                        world.random.nextDouble() * 0.8 - 0.4, // 带随机垂直速度
                        Math.sin(angle) * speed
                );
            }
        }
    }
}