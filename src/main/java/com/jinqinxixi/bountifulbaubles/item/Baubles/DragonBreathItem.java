package com.jinqinxixi.bountifulbaubles.item.Baubles;

import com.jinqinxixi.bountifulbaubles.system.modifier.ModifiableBaubleItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

@Mod.EventBusSubscriber
public class DragonBreathItem extends ModifiableBaubleItem {

    public DragonBreathItem(Properties properties) {
        super(properties);
    }

    @Override
    public ModifiableBaubleItem.Modifier[] getModifiers() {
        return ModifiableBaubleItem.Modifier.values();
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable net.minecraft.nbt.CompoundTag nbt) {
        return CuriosApi.createCurioProvider(new ICurio() {
            @Override
            public ItemStack getStack() {
                return stack;
            }

            @Override
            public void onEquip(SlotContext slotContext, ItemStack prevStack) {
                if (slotContext.entity() instanceof Player player) {
                    applyModifier(player, stack);
                }
            }

            @Override
            public void onUnequip(SlotContext slotContext, ItemStack newStack) {
                if (slotContext.entity() instanceof Player player) {
                    removeModifier(player, stack);
                }
            }

            @Override
            public boolean canEquip(SlotContext slotContext) {
                return true;
            }
        });
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBlockDrop(BlockEvent.BreakEvent event) {
        // 客户端直接返回
        if (event.getLevel().isClientSide()) {
            return;
        }

        Player player = event.getPlayer();
        // 确保在服务器端
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }

        BlockPos pos = event.getPos();

        // 检查是否使用了正确的工具
        ItemStack tool = player.getMainHandItem();
        if (!event.getState().canHarvestBlock(serverLevel, pos, player)) {
            return;
        }

        // 检查工具是否有精准采集
        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, tool) > 0) {
            return;
        }

        // 检查玩家是否装备了龙息饰品
        boolean hasBreath = CuriosApi.getCuriosInventory(player)
                .resolve()
                .flatMap(handler -> handler.findFirstCurio(item -> item.getItem() instanceof DragonBreathItem))
                .isPresent();

        if (!hasBreath) {
            return;
        }

        // 使用 ServerLevel 获取掉落物
        List<ItemStack> drops = Block.getDrops(event.getState(), serverLevel, pos, null, player, player.getMainHandItem());

        // 如果没有掉落物，直接返回
        if (drops.isEmpty()) {
            return;
        }

        // 处理每个掉落物
        for (ItemStack drop : drops) {
            // 检查是否可以烧炼
            Optional<SmeltingRecipe> recipe = serverLevel.getRecipeManager()
                    .getAllRecipesFor(RecipeType.SMELTING)
                    .stream()
                    .filter(r -> r.getIngredients().get(0).test(drop))
                    .findFirst();

            if (recipe.isPresent()) {
                // 创建烧炼后的物品
                ItemStack smeltedItem = recipe.get().getResultItem(serverLevel.registryAccess()).copy();
                smeltedItem.setCount(drop.getCount()); // 保持原始数量

                // 在方块位置生成物品实体
                Block.popResource(serverLevel, pos, smeltedItem);

                // 防止原始掉落
                event.setCanceled(true);
                // 确保方块被破坏
                serverLevel.removeBlock(pos, false);

                // 播放熔炼音效和粒子效果
                serverLevel.levelEvent(2001, pos, Block.getId(event.getState()));
                return; // 处理完后退出
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.dragon_breath.effect")
                .withStyle(ChatFormatting.BLUE));
        super.appendHoverText(stack, level, tooltip, flag);
    }
    // 1. 禁止铁砧/指令附魔
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    // 2. 附魔等级设为0（防止附魔台操作）
    @Override
    public int getEnchantmentValue() {
        return 0;
    }
}