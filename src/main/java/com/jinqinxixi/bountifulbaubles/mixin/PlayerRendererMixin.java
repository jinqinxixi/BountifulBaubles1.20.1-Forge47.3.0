package com.jinqinxixi.bountifulbaubles.mixin;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin {
//    @Unique
//    private static final float TARGET_SCALE = 0.25f;
//
//    @Inject(
//            method = "render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
//            at = @At("HEAD")
//    )
//    private void onRenderStart(AbstractClientPlayer player, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
//        poseStack.pushPose();
//        poseStack.scale(TARGET_SCALE, TARGET_SCALE, TARGET_SCALE);
//    }
//
//    @Inject(
//            method = "render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
//            at = @At("RETURN")
//    )
//    private void onRenderEnd(AbstractClientPlayer player, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
//        poseStack.popPose();
//    }
//
//    // 更好的缩放覆盖方式
//    @ModifyVariable(
//            method = "scale(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;F)V",
//            at = @At("HEAD"),
//            argsOnly = true
//    )
//    private float overrideScale(float original) {
//        return 1.0f;
//    }
}