package com.jinqinxixi.bountifulbaubles.item.Baubles;

import com.jinqinxixi.bountifulbaubles.system.modifier.ModifiableBaubleItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber
public class ThaSpiderItem extends ModifiableBaubleItem {

    public ThaSpiderItem(Properties properties) {
        super(properties);
    }

    // 设置可用的修饰符
    private static final ModifiableBaubleItem.Modifier[] MODIFIERS = ModifiableBaubleItem.Modifier.values();

    @Override
    public ModifiableBaubleItem.Modifier[] getModifiers() {
        return MODIFIERS;
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag unused) {
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

            @Override
            public boolean canUnequip(SlotContext slotContext) {
                return true;
            }
        });
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Player player = event.player;

        // 检查玩家是否装备了蜘蛛饰品
        boolean hasSpiderRing = CuriosApi.getCuriosInventory(player)
                .resolve()
                .flatMap(handler -> handler.findFirstCurio(item -> item.getItem() instanceof ThaSpiderItem))
                .isPresent();

        if (hasSpiderRing) {
            handleWallClimb(player);
        }
    }

        private static void handleWallClimb(Player player) {
            // 检查玩家是否在墙上且按着W键
            if (!player.onGround() && !player.isInWater() && !player.isInLava() && player.zza > 0) {
                if (isPlayerTouchingWall(player)) {
                    Vec3 motion = player.getDeltaMovement();

                    // 检查玩家是否在潜行
                    if (player.isShiftKeyDown()) {
                        // 如果玩家正在潜行，将垂直速度设为0，保持在当前位置
                        player.setDeltaMovement(motion.x, 0, motion.z);
                    } else {
                        // 正常爬墙
                        double upwardSpeed = 0.11;
                        player.setDeltaMovement(motion.x, upwardSpeed, motion.z);
                    }

                    // 重置下落距离以防止摔落伤害
                    player.resetFallDistance();

                    // 减小水平移动以增加稳定性
                    double drag = 0.7;
                    player.setDeltaMovement(player.getDeltaMovement().multiply(drag, 1.0, drag));
                }
            } else {
                // 如果玩家贴着墙但没有按W键，检查是否在潜行
                if (isPlayerTouchingWall(player) && player.isShiftKeyDown()) {
                    // 如果玩家在潜行，保持在当前位置
                    player.setDeltaMovement(player.getDeltaMovement().multiply(0.7, 0, 0.7));
                    player.resetFallDistance();
                }
            }
        }

    private static boolean isPlayerTouchingWall(Player player) {
        AABB boundingBox = player.getBoundingBox();
        // 扩大检测范围
        AABB checkBox = boundingBox.inflate(0.15, 0, 0.15);
        Level level = player.level();

        int minX = (int) Math.floor(checkBox.minX);
        int maxX = (int) Math.ceil(checkBox.maxX);
        int minY = (int) Math.floor(boundingBox.minY);
        int maxY = (int) Math.ceil(boundingBox.maxY);
        int minZ = (int) Math.floor(checkBox.minZ);
        int maxZ = (int) Math.ceil(checkBox.maxZ);

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        // 检查玩家周围的所有方向
        double[] checkPoints = {
                player.getX() - 0.35, player.getX() + 0.35, // X轴方向
                player.getZ() - 0.35, player.getZ() + 0.35  // Z轴方向
        };

        for (int y = minY; y <= maxY; y++) {
            // 检查X轴方向
            pos.set(checkPoints[0], y, player.getZ());
            if (isValidWall(level, pos)) return true;

            pos.set(checkPoints[1], y, player.getZ());
            if (isValidWall(level, pos)) return true;

            // 检查Z轴方向
            pos.set(player.getX(), y, checkPoints[2]);
            if (isValidWall(level, pos)) return true;

            pos.set(player.getX(), y, checkPoints[3]);
            if (isValidWall(level, pos)) return true;
        }

        return false;
    }

    // 辅助方法：检查指定位置是否是有效的墙
    private static boolean isValidWall(Level level, BlockPos pos) {
        BlockState blockState = level.getBlockState(pos);
        if (!blockState.isAir() && blockState.isSolid() &&
                !(blockState.getBlock() instanceof LadderBlock) &&
                !(blockState.getBlock() instanceof VineBlock)) {
            VoxelShape shape = blockState.getCollisionShape(level, pos);
            return !shape.isEmpty();
        }
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.tha_spider.effect")
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