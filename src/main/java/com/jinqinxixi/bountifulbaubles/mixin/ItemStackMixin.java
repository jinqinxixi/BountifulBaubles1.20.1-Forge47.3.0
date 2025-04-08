package com.jinqinxixi.bountifulbaubles.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//@Mixin(GameRenderer.class)
//public abstract class GameRendererMixin {
//
//    @Inject(
//            method = "getNightVisionScale",
//            at = @At("HEAD"),
//            cancellable = true
//    )
//    private static void onGetNightVisionScale(LivingEntity living, float partialTicks, CallbackInfoReturnable<Float> callback) {
//        // 硬编码参数
//        final double MAX_BRIGHTNESS = 1.0D;       // 最大亮度
//        final boolean FADE_OUT = false;           // 禁用渐变效果
//        final int FADE_TICKS = 200;               // 渐变开始时间（10秒）
//
//        double brightness = MAX_BRIGHTNESS;
//
//        if (FADE_OUT && living.hasEffect(NIGHT_VISION)) {
//            int duration = living.getEffect(NIGHT_VISION).getDuration();
//            if (duration <= FADE_TICKS) {
//                brightness = duration * (MAX_BRIGHTNESS / FADE_TICKS);
//            }
//        }
//
//        callback.setReturnValue((float) brightness);
//        callback.cancel();
//    }
//}
@Mixin(MinecraftServer.class)
public class ItemStackMixin {
    @Inject(at = @At("HEAD"),method = "loadLevel")
    private void init(CallbackInfo info){
        System.out.println("LoadLevel---");
    }
}