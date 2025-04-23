package com.jinqinxixi.bountifulbaubles.item.Baubles;

import com.jinqinxixi.bountifulbaubles.system.modifier.ModifiableBaubleItem;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
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
public class FireMindItem extends ModifiableBaubleItem {
    private static final double RANGE = 10.0D; // 范围10格
    private static final int FIRE_DURATION = 3; // 着火持续3秒

    public FireMindItem(Properties properties) {
        super(properties);
    }

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

            @Override
            public boolean canUnequip(SlotContext slotContext) {
                return true;
            }
        });
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.player.level().isClientSide) {
            Player player = event.player;

            // 检查玩家是否装备了火焰之心饰品
            boolean hasFireMind = CuriosApi.getCuriosInventory(player)
                    .resolve()
                    .flatMap(handler -> handler.findFirstCurio(item -> item.getItem() instanceof FireMindItem))
                    .isPresent();

            if (hasFireMind) {
                // 获取玩家视线方向
                Vec3 lookVec = player.getLookAngle();
                Vec3 eyePos = player.getEyePosition();

                // 计算检测范围的边界框
                AABB searchBox = player.getBoundingBox().inflate(RANGE);

                // 获取范围内的所有生物
                List<Entity> entities = player.level().getEntities(player, searchBox);

                for (Entity target : entities) {
                    if (target instanceof LivingEntity && target != player) {
                        // 检查目标是否已经着火
                        if (target.getRemainingFireTicks() > 0) {
                            continue; // 如果已经着火，跳过这个目标
                        }

                        // 计算到目标的向量
                        Vec3 toTarget = target.position().add(0, target.getBbHeight() * 0.5, 0).subtract(eyePos);
                        double distance = toTarget.length();

                        // 如果在范围内
                        if (distance <= RANGE) {
                            // 计算视线和目标向量的夹角
                            double dot = toTarget.normalize().dot(lookVec);

                            // 如果夹角足够小（玩家在看着目标）
                            if (dot > 0.98) { // 大约11度的视角
                                // 检查视线是否被方块阻挡
                                BlockHitResult result = player.level().clip(new ClipContext(
                                        eyePos,
                                        target.position().add(0, target.getBbHeight() * 0.5, 0),
                                        ClipContext.Block.COLLIDER,
                                        ClipContext.Fluid.NONE,
                                        player
                                ));

                                // 如果没有方块阻挡（视线可以到达目标）
                                if (result.getType() == BlockHitResult.Type.MISS ||
                                        result.getLocation().distanceToSqr(eyePos) > distance * distance) {
                                    // 设置目标着火
                                    target.setSecondsOnFire(FIRE_DURATION);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.fire_mind.effects")
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