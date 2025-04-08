package com.jinqinxixi.bountifulbaubles.system.wormhole;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT) // 确保仅客户端加载
public class PlayerSelectionScreen extends Screen {
    private final List<String> players;
    private static final int BUTTON_WIDTH = 80;
    private static final int BUTTON_HEIGHT = 15;
    private static final int MARGIN = 4;

    public PlayerSelectionScreen(List<String> players) {
        super(Component.translatable("gui.player_selection.title"));
        this.players = players;
    }

    @Override
    protected void init() {
        super.init();

        // 计算起始Y坐标
        int startY = (this.height - (players.size() * (BUTTON_HEIGHT + MARGIN))) / 2;
        if (startY < 20) startY = 20;

        // 添加玩家按钮
        for (int i = 0; i < players.size(); i++) {
            String playerName = players.get(i);
            this.addRenderableWidget(
                    Button.builder(Component.literal(playerName), button -> handleSelection(playerName))
                            .pos((width - BUTTON_WIDTH) / 2, startY + i * (BUTTON_HEIGHT + MARGIN))
                            .size(BUTTON_WIDTH, BUTTON_HEIGHT)
                            .build()
            );
        }

        // 添加取消按钮
        this.addRenderableWidget(
                Button.builder(Component.translatable("gui.cancel"), button -> onClose())
                        .pos((width - BUTTON_WIDTH) / 2, height - 30)
                        .size(BUTTON_WIDTH, BUTTON_HEIGHT)
                        .build()
        );
    }

    private void handleSelection(String playerName) {
        PacketHandler.INSTANCE.sendToServer(new PacketHandler.PlayerSelectedPacket(playerName));
        this.onClose();
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        // 绘制标题
        gui.drawCenteredString(font, title, width/2, 10, 0xFFFFFF);
        super.render(gui, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false; // 游戏不会在打开此界面时暂停
    }
}
