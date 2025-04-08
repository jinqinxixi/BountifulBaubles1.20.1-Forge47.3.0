//package com.jinqinxixi.bountifulbaubles.client;
//
//import com.jinqinxixi.bountifulbaubles.Events.ModEvents;
//import com.jinqinxixi.bountifulbaubles.Items.Baubles.GluttonyPendantItem;
//import net.minecraft.network.chat.Component;
//import net.minecraft.network.chat.MutableComponent;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.item.Item;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.food.FoodProperties;
//import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//import net.minecraft.ChatFormatting;
//
//@Mod.EventBusSubscriber
//public class FoodInfoDisplayEvent {
//
//    @SubscribeEvent
//    public static void onFoodEaten(LivingEntityUseItemEvent.Finish event) {
//        if (!(event.getEntity() instanceof Player player)) return;
//        if (!event.getItem().isEdible()) return;
//
//        ItemStack foodStack = event.getItem();
//        Item foodItem = foodStack.getItem();
//        FoodProperties foodProperties = foodItem.getFoodProperties();
//
//        // 检查食物属性是否存在
//        if (foodProperties == null) return;
//
//        // 获取食物属性
//        int nutrition = foodProperties.getNutrition();        // 饥饿值
//        float saturation = foodProperties.getSaturationModifier(); // 饱和度修饰符
//        float totalSaturation = nutrition * saturation * 2.0f;    // 实际饱和度
//
//        // 创建消息组件
//        MutableComponent message = Component.literal("§6▶ ")
//                .append(Component.literal(foodStack.getHoverName().getString())
//                        .withStyle(ChatFormatting.YELLOW))
//                .append(Component.literal(" §7("))
//                .append(Component.literal("饥饿值: " + nutrition)
//                        .withStyle(ChatFormatting.GREEN))
//                .append(Component.literal(" | "))
//                .append(Component.literal("饱和度: " + String.format("%.1f", totalSaturation))
//                        .withStyle(ChatFormatting.AQUA))
//                .append(Component.literal("§7)"));
//
//        // 如果玩家装备了贪食吊坠，显示额外信息
//        if (ModEvents.hasCurioItem(player, GluttonyPendantItem.class)) {
//            int sinLevel = ModEvents.calculateSinLevel(nutrition, saturation);
//            message.append(Component.literal("\n§7将获得 ")
//                    .append(Component.literal("等级 " + (sinLevel - 1))
//                            .withStyle(ChatFormatting.RED))
//                    .append(Component.literal(" §7的罪恶效果")));
//        }
//
//        // 发送消息给玩家
//        player.sendSystemMessage(message);
//    }
//}