//package com.jinqinxixi.bountifulbaubles.client;
//
//import com.jinqinxixi.bountifulbaubles.BountifulBaublesMod;
//import net.minecraft.ChatFormatting;
//import net.minecraft.network.chat.Component;
//import net.minecraft.world.damagesource.DamageSource;
//import net.minecraft.world.damagesource.DamageTypes;
//import net.minecraft.world.entity.player.Player;
//import net.minecraftforge.event.entity.living.LivingDamageEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//
//@Mod.EventBusSubscriber(modid = BountifulBaublesMod.MOD_ID)
//public class DamageDebugHandler {
//
//    @SubscribeEvent
//    public static void onPlayerDamage(LivingDamageEvent event) {
//        if (event.getEntity() instanceof Player player && !player.level().isClientSide()) {
//            DamageSource source = event.getSource();
//            float amount = event.getAmount();
//
//            // 构建消息
//            Component message = Component.literal("受到伤害: ")
//                    .withStyle(ChatFormatting.RED)
//                    .append(Component.literal(String.format("%.2f", amount))
//                            .withStyle(ChatFormatting.GOLD))
//                    .append(Component.literal(" 点"))
//                    .append(Component.literal("\n类型: ")
//                            .withStyle(ChatFormatting.YELLOW))
//                    .append(Component.literal(getDamageTypeInfo(source))
//                            .withStyle(ChatFormatting.WHITE));
//
//            // 发送消息给玩家
//            player.sendSystemMessage(message);
//        }
//    }
//
//    private static String getDamageTypeInfo(DamageSource source) {
//        StringBuilder info = new StringBuilder();
//
//        // 基本伤害类型
//        info.append("- ").append(source.getMsgId());
//
//        // 检查所有官方伤害类型
//        if (source.is(DamageTypes.IN_FIRE)) info.append(" (在火中)");
//        if (source.is(DamageTypes.LIGHTNING_BOLT)) info.append(" (闪电)");
//        if (source.is(DamageTypes.ON_FIRE)) info.append(" (着火)");
//        if (source.is(DamageTypes.LAVA)) info.append(" (岩浆)");
//        if (source.is(DamageTypes.HOT_FLOOR)) info.append(" (热地板)");
//        if (source.is(DamageTypes.IN_WALL)) info.append(" (窒息)");
//        if (source.is(DamageTypes.CRAMMING)) info.append(" (拥挤)");
//        if (source.is(DamageTypes.DROWN)) info.append(" (溺水)");
//        if (source.is(DamageTypes.STARVE)) info.append(" (饥饿)");
//        if (source.is(DamageTypes.CACTUS)) info.append(" (仙人掌)");
//        if (source.is(DamageTypes.FALL)) info.append(" (摔落)");
//        if (source.is(DamageTypes.FLY_INTO_WALL)) info.append(" (撞墙)");
//        if (source.is(DamageTypes.FELL_OUT_OF_WORLD)) info.append(" (虚空)");
//        if (source.is(DamageTypes.GENERIC)) info.append(" (通用)");
//        if (source.is(DamageTypes.MAGIC)) info.append(" (魔法)");
//        if (source.is(DamageTypes.WITHER)) info.append(" (凋零)");
//        if (source.is(DamageTypes.DRAGON_BREATH)) info.append(" (龙息)");
//        if (source.is(DamageTypes.DRY_OUT)) info.append(" (干燥)");
//        if (source.is(DamageTypes.SWEET_BERRY_BUSH)) info.append(" (浆果丛)");
//        if (source.is(DamageTypes.FREEZE)) info.append(" (冻伤)");
//        if (source.is(DamageTypes.STALAGMITE)) info.append(" (石笋)");
//        if (source.is(DamageTypes.FALLING_BLOCK)) info.append(" (下落方块)");
//        if (source.is(DamageTypes.FALLING_ANVIL)) info.append(" (下落铁砧)");
//        if (source.is(DamageTypes.FALLING_STALACTITE)) info.append(" (下落钟乳石)");
//        if (source.is(DamageTypes.STING)) info.append(" (蛰伤)");
//        if (source.is(DamageTypes.MOB_ATTACK)) info.append(" (生物攻击)");
//        if (source.is(DamageTypes.MOB_ATTACK_NO_AGGRO)) info.append(" (生物攻击无仇恨)");
//        if (source.is(DamageTypes.PLAYER_ATTACK)) info.append(" (玩家攻击)");
//        if (source.is(DamageTypes.ARROW)) info.append(" (箭矢)");
//        if (source.is(DamageTypes.TRIDENT)) info.append(" (三叉戟)");
//        if (source.is(DamageTypes.MOB_PROJECTILE)) info.append(" (生物投射物)");
//        if (source.is(DamageTypes.FIREWORKS)) info.append(" (烟花火箭)");
//        if (source.is(DamageTypes.FIREBALL)) info.append(" (火球)");
//        if (source.is(DamageTypes.UNATTRIBUTED_FIREBALL)) info.append(" (未归属火球)");
//        if (source.is(DamageTypes.WITHER_SKULL)) info.append(" (凋零头颅)");
//        if (source.is(DamageTypes.THROWN)) info.append(" (投掷物)");
//        if (source.is(DamageTypes.INDIRECT_MAGIC)) info.append(" (间接魔法)");
//        if (source.is(DamageTypes.THORNS)) info.append(" (荆棘)");
//        if (source.is(DamageTypes.EXPLOSION)) info.append(" (爆炸)");
//        if (source.is(DamageTypes.PLAYER_EXPLOSION)) info.append(" (玩家爆炸)");
//        if (source.is(DamageTypes.SONIC_BOOM)) info.append(" (音爆)");
//        if (source.is(DamageTypes.BAD_RESPAWN_POINT)) info.append(" (重生点爆炸)");
//        if (source.is(DamageTypes.OUTSIDE_BORDER)) info.append(" (世界边界)");
//        if (source.is(DamageTypes.GENERIC_KILL)) info.append(" (通用击杀)");
//
//        // 如果有直接伤害来源实体
//        if (source.getDirectEntity() != null && source.getDirectEntity() != source.getEntity()) {
//            info.append("\n- 直接来源: ").append(source.getDirectEntity().getName().getString());
//        }
//
//        // 如果有间接伤害来源实体
//        if (source.getEntity() != null && source.getEntity() != source.getDirectEntity()) {
//            info.append("\n- 间接来源: ").append(source.getEntity().getName().getString());
//        }
//
//        return info.toString();
//    }
//}