package com.jinqinxixi.bountifulbaubles.Potion;

import com.jinqinxixi.bountifulbaubles.item.Baubles.MagicMirrorItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class RecallPotion extends MagicMirrorItem {

    private static final int USE_DURATION = 32;// 长按时间
    private static final int COOLDOWN_TICKS = 3;


    @Override
    public int getUseDuration(ItemStack stack) {
        return USE_DURATION;
    }


    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }
    public RecallPotion() {
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
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof ServerPlayer player) {
            if (checkCooldown(player)) {
                teleportToSpawn(player);
                setCooldown(player);
            }

            // 强制返还空瓶逻辑
            if (!player.getAbilities().instabuild) { // 非创造模式
                ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
                if (!player.getInventory().add(bottle)) {
                    player.drop(bottle, false); // 物品栏满则掉落
                }
            }

            // 处理堆叠消耗
            if (stack.getCount() > 1) {
                stack.shrink(1);
                return stack;
            }
        }
        return ItemStack.EMPTY; // 堆叠为1时直接消耗
    }

    protected void setCooldown(Player player) {
        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
    }
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.recall_potion.effect")
                .withStyle(ChatFormatting.BLUE));
    }
}
