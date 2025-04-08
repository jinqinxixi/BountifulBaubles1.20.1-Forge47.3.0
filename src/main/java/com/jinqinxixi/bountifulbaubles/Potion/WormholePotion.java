package com.jinqinxixi.bountifulbaubles.Potion;

import com.jinqinxixi.bountifulbaubles.system.wormhole.PacketHandler;
import com.jinqinxixi.bountifulbaubles.item.Baubles.WormholeMirrorItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.List;

public class WormholePotion extends WormholeMirrorItem {

    private static final int COOLDOWN_TICKS = 2; // 冷却
    private static final int USE_DURATION = 32;    // 1秒长按时间


    public WormholePotion() {
        super(new Item.Properties()
                .stacksTo(16)  // 通常药水堆叠数为1
                .food(new FoodProperties.Builder()
                        .alwaysEat()  // 允许随时饮用
                        .nutrition(0) // 无饱食度
                        .saturationMod(0)
                        .build())
                .craftRemainder(Items.GLASS_BOTTLE) // 饮用后返回空瓶
                .rarity(Rarity.UNCOMMON)
        );
    }


    // 设置使用动作持续时间
    @Override
    public int getUseDuration(ItemStack stack) {
        return USE_DURATION;
    }

    // 定义使用动作为拉弓
    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }

        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            PacketHandler.INSTANCE.send(
                    PacketDistributor.PLAYER.with(() -> serverPlayer),
                    new PacketHandler.RequestPlayerListPacket(true) // 改为true表示来自药水
            );
        }
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }
    // 客户端打开GUI
    private void openPlayerListGUI(Player player) {
        if (player.level().isClientSide) {
            PacketHandler.INSTANCE.sendToServer(new PacketHandler.RequestPlayerListPacket(false));
        }
    }

    @Override
    protected ItemStack handleItemConsumption(ItemStack stack, ServerPlayer player) {
        if (!player.getAbilities().instabuild) {
            ItemStack bottleStack = new ItemStack(Items.GLASS_BOTTLE);

            if (stack.getCount() == 1) {
                return bottleStack; // 如果只有一个物品，直接返回空瓶
            } else {
                stack = stack.copy();
                stack.shrink(1);
                // 添加空瓶到背包，如果背包满了就掉在地上
                if (!player.getInventory().add(bottleStack)) {
                    player.drop(bottleStack, false);
                }
            }
        }
        return stack;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof Player player) {
            if (!level.isClientSide) {
                // 设置药水自身冷却（1刻）
                player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
            }
            if (level.isClientSide) {
                openPlayerListGUI(player);
            }
        }
        return stack; // 直接返回原始堆叠，让传送请求发送后再处理消耗
    }
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.wormhole_potion.effect")
                .withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.wormhole_potion.description")
                .withStyle(ChatFormatting.GREEN));
    }
}
