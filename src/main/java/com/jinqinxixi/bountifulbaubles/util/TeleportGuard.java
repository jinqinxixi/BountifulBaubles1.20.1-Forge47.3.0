package com.jinqinxixi.bountifulbaubles.util;

import com.jinqinxixi.bountifulbaubles.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/**
 * 传送限制检查（配置在 {@code bountifulbaubles-common.toml} 的 [Teleport Settings] 分组）。
 *
 * <p>两类行为：
 * <ul>
 *   <li><b>回城</b>——魔镜 / 虫洞镜 / 回忆药水 / 虫洞药水，传送回重生点或世界出生点</li>
 *   <li><b>传送到玩家</b>——虫洞镜选人 / 虫洞药水</li>
 * </ul>
 *
 * <p>距离一律按<b>水平距离</b>计算（忽略 Y 轴），避免挖矿到深层被误判为"跑远了"。
 * 跨维度时不同维度的坐标不可比较，因此只检查跨维度开关，不检查距离。
 */
public final class TeleportGuard
{
    private TeleportGuard()
    {
    }

    /**
     * 回城检查。
     *
     * @return {@code null} 表示允许；否则返回应提示给玩家的失败原因
     */
    public static Component checkRecall(ServerPlayer player, ServerLevel targetLevel, BlockPos targetPos)
    {
        boolean crossDimension = !player.level().dimension().equals(targetLevel.dimension());
        if (crossDimension)
        {
            return ModConfig.isRecallCrossDimensionAllowed()
                    ? null
                    : Component.translatable("msg.bountifulbaubles.teleport.cross_dimension_denied");
        }

        int max = ModConfig.getRecallMaxDistance();
        if (max <= 0)
        {
            return null;
        }
        double distance = horizontalDistance(player, targetPos.getX() + 0.5D, targetPos.getZ() + 0.5D);
        if (distance <= max)
        {
            return null;
        }
        return Component.translatable("msg.bountifulbaubles.teleport.too_far",
                (int) Math.round(distance), max);
    }

    /**
     * 传送到其他玩家的检查。
     *
     * @return {@code null} 表示允许；否则返回应提示给玩家的失败原因
     */
    public static Component checkPlayerTeleport(ServerPlayer player, ServerPlayer target)
    {
        boolean crossDimension = !player.level().dimension().equals(target.level().dimension());
        if (crossDimension)
        {
            return ModConfig.isPlayerTeleportCrossDimensionAllowed()
                    ? null
                    : Component.translatable("msg.bountifulbaubles.teleport.cross_dimension_denied");
        }

        int max = ModConfig.getPlayerTeleportMaxDistance();
        if (max <= 0)
        {
            return null;
        }
        double distance = horizontalDistance(player, target.getX(), target.getZ());
        if (distance <= max)
        {
            return null;
        }
        return Component.translatable("msg.bountifulbaubles.teleport.too_far",
                (int) Math.round(distance), max);
    }

    private static double horizontalDistance(ServerPlayer player, double x, double z)
    {
        double dx = player.getX() - x;
        double dz = player.getZ() - z;
        return Math.sqrt(dx * dx + dz * dz);
    }
}
