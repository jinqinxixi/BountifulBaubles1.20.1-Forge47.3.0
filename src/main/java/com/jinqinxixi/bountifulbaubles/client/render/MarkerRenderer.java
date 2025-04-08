package com.jinqinxixi.bountifulbaubles.client.render;

import com.jinqinxixi.bountifulbaubles.BountifulBaublesMod;
import com.jinqinxixi.bountifulbaubles.item.Baubles.MindsEyeItem;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = BountifulBaublesMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class MarkerRenderer {
    private static final ResourceLocation MARKER_TEXTURE = new ResourceLocation(BountifulBaublesMod.MOD_ID, "textures/misc/target_marker.png");
    private static final float MARKER_SIZE = 0.5f;

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) { // 改为在粒子后渲染
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return;
        }

        float partialTicks = event.getPartialTick();
        PoseStack poseStack = event.getPoseStack();

        for (Map.Entry<UUID, Map.Entry<Integer, Long>> entry : MindsEyeItem.getMarkedTargets().entrySet()) {
            Entity target = mc.level.getEntity(entry.getValue().getKey());
            if (target == null) continue;

            // 获取插值后的位置
            double interpEntityX = target.xo + (target.getX() - target.xo) * partialTicks;
            double interpEntityY = target.yo + (target.getY() - target.yo) * partialTicks;
            double interpEntityZ = target.zo + (target.getZ() - target.zo) * partialTicks;

            double entityHeight = target.getBbHeight();

            Vec3 cameraPos = mc.gameRenderer.getMainCamera().getPosition();

            poseStack.pushPose();

            // 设置标记位置
            poseStack.translate(
                    interpEntityX - cameraPos.x,
                    interpEntityY + entityHeight + 0.7 - cameraPos.y,
                    interpEntityZ - cameraPos.z
            );

            // 使标记面向玩家
            poseStack.mulPose(mc.gameRenderer.getMainCamera().rotation());

            // 渲染标记
            renderMarker(poseStack);

            poseStack.popPose();
        }
    }

    private static void renderMarker(PoseStack poseStack) {
        // 设置着色器和纹理
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, MARKER_TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        // 渲染四边形
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();

        float halfSize = MARKER_SIZE / 2.0f;

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        buffer.vertex(poseStack.last().pose(), -halfSize, -halfSize, 0).uv(0, 1).endVertex();
        buffer.vertex(poseStack.last().pose(), -halfSize, halfSize, 0).uv(0, 0).endVertex();
        buffer.vertex(poseStack.last().pose(), halfSize, halfSize, 0).uv(1, 0).endVertex();
        buffer.vertex(poseStack.last().pose(), halfSize, -halfSize, 0).uv(1, 1).endVertex();

        tesselator.end();
    }
}