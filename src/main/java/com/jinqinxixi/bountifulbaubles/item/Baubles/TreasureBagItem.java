package com.jinqinxixi.bountifulbaubles.item.Baubles;

import com.jinqinxixi.bountifulbaubles.BountifulBaublesMod;
import com.jinqinxixi.bountifulbaubles.config.ModConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TreasureBagItem extends Item {
    private static final Random RANDOM = new Random();
    private static List<WeightedItem> possibleItems = null;

    // 内部类来存储物品及其权重
    private static class WeightedItem {
        final RegistryObject<Item> item;
        final int weight;

        WeightedItem(RegistryObject<Item> item, int weight) {
            this.item = item;
            this.weight = weight;
        }
    }

    public TreasureBagItem(Properties properties) {
        super(properties);
    }

    private void initPossibleItems() {
        if (possibleItems == null) {
            possibleItems = new ArrayList<>();
            try {
                String[] configItems = ModConfig.TREASURE_BAG_ITEMS.get().split(";");
                for (String entry : configItems) {
                    try {
                        String[] parts = entry.trim().split(",");
                        if (parts.length != 2) {
                            BountifulBaublesMod.LOGGER.error("Invalid format in treasure bag config: " + entry);
                            continue;
                        }

                        String itemId = parts[0].trim();
                        int weight = Integer.parseInt(parts[1].trim());

                        ResourceLocation resourceLocation = new ResourceLocation(itemId);
                        Item item = ForgeRegistries.ITEMS.getValue(resourceLocation);
                        if (item != null) {
                            possibleItems.add(new WeightedItem(
                                    RegistryObject.create(resourceLocation, ForgeRegistries.ITEMS),
                                    weight
                            ));
                        } else {
                            BountifulBaublesMod.LOGGER.error("Item not found in registry: " + itemId);
                        }
                    } catch (NumberFormatException e) {
                        BountifulBaublesMod.LOGGER.error("Invalid weight in treasure bag config: " + entry);
                    } catch (Exception e) {
                        BountifulBaublesMod.LOGGER.error("Invalid item entry in treasure bag config: " + entry);
                    }
                }
            } catch (Exception e) {
                BountifulBaublesMod.LOGGER.error("Error loading treasure bag config", e);
            }
        }
    }

    private RegistryObject<Item> getRandomItem() {
        if (possibleItems == null || possibleItems.isEmpty()) {
            return null;
        }

        int totalWeight = possibleItems.stream().mapToInt(item -> item.weight).sum();
        int randomWeight = RANDOM.nextInt(totalWeight);
        int currentWeight = 0;

        for (WeightedItem weightedItem : possibleItems) {
            currentWeight += weightedItem.weight;
            if (randomWeight < currentWeight) {
                return weightedItem.item;
            }
        }

        return possibleItems.get(possibleItems.size() - 1).item;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            initPossibleItems();

            if (possibleItems != null && !possibleItems.isEmpty()) {
                RegistryObject<Item> randomItem = getRandomItem();
                if (randomItem != null) {
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
            } else {
                BountifulBaublesMod.LOGGER.error("Treasure bag has no possible items configured!");
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