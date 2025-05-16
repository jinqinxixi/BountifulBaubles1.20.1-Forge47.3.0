package com.jinqinxixi.bountifulbaubles.item.Baubles;

import com.jinqinxixi.bountifulbaubles.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TreasureBagItem extends Item {
    private static final List<RegistryObject<Item>> POSSIBLE_ITEMS = new ArrayList<>();
    private static final Random RANDOM = new Random();

    public TreasureBagItem(Properties properties) {
        super(properties);
        // 初始化可能的物品列表
        if (POSSIBLE_ITEMS.isEmpty()) {
            POSSIBLE_ITEMS.add(ModItems.MINDS_EYE);
            POSSIBLE_ITEMS.add(ModItems.LUCK_COIN);
            POSSIBLE_ITEMS.add(ModItems.DRAGON_BREATH);
            POSSIBLE_ITEMS.add(ModItems.ICE_SHARD);
            POSSIBLE_ITEMS.add(ModItems.THA_SPIDER);
            POSSIBLE_ITEMS.add(ModItems.CREEPO);
            POSSIBLE_ITEMS.add(ModItems.THA_WIZARD);
            POSSIBLE_ITEMS.add(ModItems.BOOK_O_ENCHANTINGITEM);
            POSSIBLE_ITEMS.add(ModItems.WARM_VOID);
            POSSIBLE_ITEMS.add(ModItems.GOLDEN_MELON);
            POSSIBLE_ITEMS.add(ModItems.FIRE_MIND);
            POSSIBLE_ITEMS.add(ModItems.DROP_SPINDLE);
            POSSIBLE_ITEMS.add(ModItems.DARK_EGG);
            POSSIBLE_ITEMS.add(ModItems.MOSSY_RING);
            POSSIBLE_ITEMS.add(ModItems.MOSSY_BELT);
            POSSIBLE_ITEMS.add(ModItems.MAD_AURA);
            POSSIBLE_ITEMS.add(ModItems.TURTLE_SHELL);
            POSSIBLE_ITEMS.add(ModItems.DARK_DAGGER);
            POSSIBLE_ITEMS.add(ModItems.EMBER);
            POSSIBLE_ITEMS.add(ModItems.WITHER_NAIL);
            POSSIBLE_ITEMS.add(ModItems.SERPENT_TOOTH);
            POSSIBLE_ITEMS.add(ModItems.BLAZE_HEART);
            POSSIBLE_ITEMS.add(ModItems.STARFISH);
            POSSIBLE_ITEMS.add(ModItems.GOLDEN_SKULL);
            POSSIBLE_ITEMS.add(ModItems.BUTCHERS_CLEAVER);
            POSSIBLE_ITEMS.add(ModItems.KARMA);
            POSSIBLE_ITEMS.add(ModItems.OXALIS);
            POSSIBLE_ITEMS.add(ModItems.GLORY_SHARDS);
            POSSIBLE_ITEMS.add(ModItems.RUBY_HEART);
            POSSIBLE_ITEMS.add(ModItems.ROCK_CANDY);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            // 随机选择一个物品
            RegistryObject<Item> randomItem = POSSIBLE_ITEMS.get(RANDOM.nextInt(POSSIBLE_ITEMS.size()));
            ItemStack rewardStack = new ItemStack(randomItem.get());

            // 给予物品
            if (!player.getInventory().add(rewardStack)) {
                player.drop(rewardStack, false);
            }

            // 播放音效
            level.playSound(null,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    SoundEvents.BUNDLE_DROP_CONTENTS,
                    SoundSource.PLAYERS,
                    0.5F,
                    1.0F);

            // 消耗物品
            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }
        }

        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.treasure_bag")
                .withStyle(ChatFormatting.BLUE));
    }

    @Override
    public int getEnchantmentValue() {
        return 0;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }
}