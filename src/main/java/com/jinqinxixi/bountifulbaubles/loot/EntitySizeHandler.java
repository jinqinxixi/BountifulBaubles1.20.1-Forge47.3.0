//package com.jinqinxixi.bountifulbaubles.Loot;
//
//import net.minecraft.world.entity.*;
//import net.minecraft.world.entity.player.Player;
//import net.minecraftforge.event.entity.EntityEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//
//@Mod.EventBusSubscriber(modid = "bountifulbaubles")
//public class EntitySizeHandler {
//    private static final float SCALE = 0.25F;
//
//    @SubscribeEvent
//    public static void onEntitySize(EntityEvent.Size event) {
//        if(event.getEntity() instanceof Player) {
//            EntityDimensions oldSize = event.getNewSize();
//            EntityDimensions newSize = oldSize.scale(SCALE);
//
//            // 这一步会同时处理碰撞箱和眼睛高度
//            event.setNewSize(newSize);
//            event.setNewEyeHeight(event.getNewEyeHeight() * SCALE);
//        }
//    }
//}