package com.jinqinxixi.bountifulbaubles.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class BaubleUtils {
    public static void destroyNearbyWebs(Player player) {
        BlockPos pos = player.blockPosition();
        Level level = player.level();

        // 检测玩家周围的方块
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos blockPos = pos.offset(x, y, z);
                    if (level.getBlockState(blockPos).is(Blocks.COBWEB)) {
                        level.destroyBlock(blockPos, true); // 破坏蜘蛛网并掉落物品
                    }
                }
            }
        }
    }
}