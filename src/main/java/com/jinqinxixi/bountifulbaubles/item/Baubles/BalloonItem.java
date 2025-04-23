package com.jinqinxixi.bountifulbaubles.item.Baubles;

import com.jinqinxixi.bountifulbaubles.system.modifier.ModifiableBaubleItem;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber
public class BalloonItem extends ModifiableBaubleItem {
    public BalloonItem(Properties properties) {
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

            // ===== 集成父类逻辑 =====
            @Override
            public void onEquip(SlotContext slotContext, ItemStack prevStack) {
                LivingEntity entity = slotContext.entity();
                if (entity instanceof Player player) {
                    // 先执行父类属性修正逻辑
                    applyModifier(player, stack); // <-- 新增父类方法调用
                    // 再执行子类跳跃效果更新
                    updateJumpEffect(player);
                }
            }

            @Override
            public void onUnequip(SlotContext slotContext, ItemStack newStack) {
                LivingEntity entity = slotContext.entity();
                if (entity instanceof Player player) {
                    // 先移除父类属性修正
                    removeModifier(player, stack); // <-- 新增父类方法调用
                    // 再更新跳跃效果
                    updateJumpEffect(player);
                }
            }

            // ===== 必须实现的方法 =====
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
            updateJumpEffect(player);
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof Player player && event.getSource().is(DamageTypes.FALL)) {
            CuriosApi.getCuriosInventory(player).resolve().ifPresent(curios -> {
                // 检查是否存在HorseshoeBalloon
                boolean hasHorseshoe = curios.findFirstCurio(stack ->
                        stack.getItem() instanceof HorseshoeBalloonItem).isPresent();
                if (hasHorseshoe) return;

                // 计算Balloon数量并乘算减免
                int balloons = curios.findCurios(stack ->
                        stack.getItem() instanceof BalloonItem).size();
                if (balloons > 0) {
                    float multiplier = (float) Math.pow(0.5F, balloons);
                    event.setAmount(event.getAmount() * multiplier);
                }
            });
        }
    }

    static void updateJumpEffect(Player player) {
        // 统计所有Balloon和HorseshoeBalloon的数量
        int balloons = CuriosApi.getCuriosInventory(player).resolve()
                .map(curios -> curios.findCurios(item -> item.getItem() instanceof BalloonItem).size())
                .orElse(0);
        int horseshoes = CuriosApi.getCuriosInventory(player).resolve()
                .map(curios -> curios.findCurios(item -> item.getItem() instanceof HorseshoeBalloonItem).size())
                .orElse(0);
        int total = balloons + horseshoes;

        // 如果没有佩戴任何气球或马蹄气球，跳过更新逻辑
        if (total == 0) {
            return;
        }

        // 移除旧的跳跃效果（仅移除由饰品提供的跳跃效果）
        MobEffectInstance currentEffect = player.getEffect(MobEffects.JUMP);
        if (currentEffect != null && currentEffect.isAmbient()) {
            player.removeEffect(MobEffects.JUMP);
        }

        // 单个气球或马蹄气球提供跳跃提升II（2级），每多一个增加1级
        int newAmplifier = 1 + (total - 1); // 初始为2级，每多一个增加1级
        // 应用新的跳跃效果
        player.addEffect(new MobEffectInstance(
                MobEffects.JUMP, 40, newAmplifier, false, false, true));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.balloon.effects").withStyle(ChatFormatting.BLUE));
        super.appendHoverText(stack, level, tooltip, flag); // 调用基类方法显示修饰符信息
    }
}