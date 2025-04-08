package com.jinqinxixi.bountifulbaubles.item.Baubles;

import com.jinqinxixi.bountifulbaubles.system.modifier.ModifiableBaubleItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber
public class AutoTorchItem extends ModifiableBaubleItem {

    private static final ModifiableBaubleItem.Modifier[] MODIFIERS = ModifiableBaubleItem.Modifier.values();
    @Override
    public Modifier[] getModifiers() {
        return MODIFIERS;
    }

    public AutoTorchItem(Properties properties) {
        super(properties);
    }
    @Override
    public boolean isFoil(ItemStack stack) {
        return true; // 始终显示附魔光效
    }
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent event) {
        if (event.phase != PlayerTickEvent.Phase.START) return;

        Player player = event.player;
        Level level = player.level();

        if (level.isClientSide || !player.isAlive() || player.tickCount % 20 != 0) return;

        CuriosApi.getCuriosInventory(player).ifPresent(curios -> {
            for (Map.Entry<String, ICurioStacksHandler> entry : curios.getCurios().entrySet()) {
                ICurioStacksHandler handler = entry.getValue();

                for (int i = 0; i < handler.getSlots(); i++) {
                    ItemStack stack = handler.getStacks().getStackInSlot(i);

                    if (stack.getItem() instanceof AutoTorchItem) {
                        processTorchPlacement(player, level);
                    }
                }
            }
        });
    }

    private static void processTorchPlacement(Player player, Level level) {
        BlockPos torchPos = player.blockPosition();

        if (shouldPlaceTorch(level, torchPos)) {
            level.setBlockAndUpdate(torchPos, Blocks.TORCH.defaultBlockState());
            level.playSound(null, torchPos,
                    SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    private static boolean shouldPlaceTorch(Level level, BlockPos pos) {
        return level.getMaxLocalRawBrightness(pos) <= 6 &&
                level.getFluidState(pos).isEmpty() &&
                (level.isEmptyBlock(pos) || level.getBlockState(pos).canBeReplaced()) &&
                Blocks.TORCH.defaultBlockState().canSurvive(level, pos);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.auto_torch.function").withStyle(ChatFormatting.BLUE));
        super.appendHoverText(stack, level, tooltip, flag); // 调用基类方法显示修饰符信息
    }
}
