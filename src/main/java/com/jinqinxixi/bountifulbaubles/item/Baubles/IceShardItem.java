package com.jinqinxixi.bountifulbaubles.item.Baubles;

import com.jinqinxixi.bountifulbaubles.system.modifier.ModifiableBaubleItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber
public class IceShardItem extends ModifiableBaubleItem {

    // 冰霜行者效果的范围
    private static final int FROST_RANGE = 2; // 5x5的区域

    public IceShardItem(Properties properties) {
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
            public List<Component> getAttributesTooltip(List<Component> tooltips) {
                return Collections.emptyList(); // 在父类中统一隐藏属性提示
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

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) {
            return;
        }

        Player player = event.player;

        // 检查玩家是否装备了冰霜碎片
        boolean hasIceShard = CuriosApi.getCuriosInventory(player)
                .resolve()
                .flatMap(handler -> handler.findFirstCurio(item -> item.getItem() instanceof IceShardItem))
                .isPresent();

        if (!hasIceShard) {
            return;
        }

        // 只在玩家站在水面上时创建霜冰
        if (isAboveWater(player) && !player.isInWater()) {
            createFrostPath(player);
        }
    }

    private static boolean canFreezeWater(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);

        // 检查是否是水方块
        if (!state.is(Blocks.WATER)) {
            return false;
        }

        // 检查是否是水源方块
        if (!state.getFluidState().isSource()) {
            return false;
        }

        // 检查水方块上方是否有空间
        return level.getBlockState(pos.above()).isAir();
    }

    private static boolean isAboveWater(Player player) {
        BlockPos pos = player.blockPosition().below();
        return canFreezeWater(player.level(), pos);
    }

    private static void createFrostPath(Player player) {
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        BlockPos playerPos = player.blockPosition();

        // 在玩家脚下和周围创建霜冰
        for (int x = -FROST_RANGE; x <= FROST_RANGE; x++) {
            for (int z = -FROST_RANGE; z <= FROST_RANGE; z++) {
                BlockPos pos = playerPos.offset(x, -1, z);
                if (canFreezeWater(serverLevel, pos)) {
                    serverLevel.setBlockAndUpdate(pos, Blocks.FROSTED_ICE.defaultBlockState());
                }
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.ice_shard.effect")
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