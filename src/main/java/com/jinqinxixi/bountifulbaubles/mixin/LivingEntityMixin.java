package com.jinqinxixi.bountifulbaubles.mixin;

import com.jinqinxixi.bountifulbaubles.config.ModConfig;
import com.jinqinxixi.bountifulbaubles.item.Baubles.ThaWizardItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import top.theillusivec4.curios.api.CuriosApi;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @ModifyVariable(
            method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z",
            at = @At("HEAD"),
            argsOnly = true
    )
    private MobEffectInstance modifyEffect(MobEffectInstance effect) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self instanceof Player player) {
            boolean hasWizardItem = CuriosApi.getCuriosInventory(player)
                    .resolve()
                    .flatMap(handler -> handler.findFirstCurio(item -> item.getItem() instanceof ThaWizardItem))
                    .isPresent();

            if (hasWizardItem && effect.getDuration() > 0) {
                return new MobEffectInstance(
                        effect.getEffect(),
                        (int) (effect.getDuration() * ModConfig.getThaWizardDurationMultiplier()),
                        effect.getAmplifier(),
                        effect.isAmbient(),
                        effect.isVisible(),
                        effect.showIcon()
                );
            }
        }
        return effect;
    }
}

//package com.jinqinxixi.bountifulbaubles.mixin;
//
//import com.jinqinxixi.bountifulbaubles.item.Baubles.ThaWizardItem;
//import net.minecraft.world.effect.MobEffectInstance;
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.entity.LivingEntity;
//import net.minecraft.world.entity.player.Player;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.ModifyArg;
//import top.theillusivec4.curios.api.CuriosApi;
//
//@Mixin(LivingEntity.class)
//public class LivingEntityMixin {
//
//    @ModifyArg(
//            method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z",
//            at = @At(value = "INVOKE",
//                    target = "Lnet/minecraft/world/effect/MobEffectInstance;<init>(Lnet/minecraft/world/effect/MobEffect;IIZZZLnet/minecraft/world/effect/MobEffectInstance;)V"),
//            index = 1
//    )
//    private int modifyEffectDuration(int duration) {
//        LivingEntity self = (LivingEntity) (Object) this;
//        if (self instanceof Player player) {
//            boolean hasWizardItem = CuriosApi.getCuriosInventory(player)
//                    .resolve()
//                    .flatMap(handler -> handler.findFirstCurio(item -> item.getItem() instanceof ThaWizardItem))
//                    .isPresent();
//
//            if (hasWizardItem && duration > 0) {
//                return (int) (duration * 1.5);
//            }
//        }
//        return duration;
//    }
//}