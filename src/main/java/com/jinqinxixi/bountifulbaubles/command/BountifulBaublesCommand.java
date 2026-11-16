package com.jinqinxixi.bountifulbaubles.command;

import com.jinqinxixi.bountifulbaubles.config.ItemConfig;
import com.jinqinxixi.bountifulbaubles.config.ModConfig;
import com.jinqinxixi.bountifulbaubles.system.wormhole.PacketHandler;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.PacketDistributor;

import java.nio.file.Path;

/**
 * 配置热重载命令：{@code /bountifulbaubles reload}
 *
 * <p>重载两个配置文件而不需要重启游戏：
 * <ul>
 *   <li>{@code bountifulbaubles-common.toml} —— 掉率、铁砧、各类功能参数</li>
 *   <li>{@code bountifulbaubles-items.toml} —— 74 个饰品开关</li>
 * </ul>
 *
 * <p>重载后会把饰品开关状态推送给所有在线客户端，使 tooltip 与服务器保持一致。
 * 单人游戏里客户端与服务端是同一进程，一次命令两端同时生效。
 */
@Mod.EventBusSubscriber(modid = "bountifulbaubles", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BountifulBaublesCommand
{
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event)
    {
        event.getDispatcher().register(
                Commands.literal("bountifulbaubles")
                        .then(Commands.literal("reload")
                                .executes(ctx -> {
                                    Path dir = FMLPaths.CONFIGDIR.get();
                                    int items = ItemConfig.reload(dir);
                                    int tables = ModConfig.reload(dir);
                                    broadcastState();

                                    final String msg;
                                    if (items == -2 || tables == -2)
                                    {
                                        msg = "§e[BountifulBaubles] 配置文件不存在，已恢复为默认值。重启游戏后会重新生成配置文件。";
                                    }
                                    else if (items < 0 || tables < 0)
                                    {
                                        msg = "§c[BountifulBaubles] 重载失败，请检查日志。";
                                    }
                                    else
                                    {
                                        msg = "§a[BountifulBaubles] 配置已重载：饰品开关 " + items
                                                + " 项，战利品表 " + tables + " 个";
                                    }
                                    ctx.getSource().sendSuccess(() -> Component.literal(msg), true);
                                    return 1;
                                })));
    }

    /** 玩家进服时同步一次，避免客户端装的与服务端配置不同导致表现不一致。 */
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        if (event.getEntity() instanceof ServerPlayer player)
        {
            PacketHandler.INSTANCE.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new PacketHandler.SyncItemConfigPacket(ItemConfig.snapshot()));
        }
    }

    private static void broadcastState()
    {
        PacketHandler.INSTANCE.send(
                PacketDistributor.ALL.noArg(),
                new PacketHandler.SyncItemConfigPacket(ItemConfig.snapshot()));
    }
}
