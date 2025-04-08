package com.jinqinxixi.bountifulbaubles.item.Baubles;

import com.jinqinxixi.bountifulbaubles.config.ModConfig;
import com.jinqinxixi.bountifulbaubles.sound.ModSounds;
import com.jinqinxixi.bountifulbaubles.system.modifier.ModifiableBaubleItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DarkDaggerItem extends ModifiableBaubleItem {

    private static final ModifiableBaubleItem.Modifier[] MODIFIERS = ModifiableBaubleItem.Modifier.values();

    public DarkDaggerItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public ModifiableBaubleItem.Modifier[] getModifiers() {
        return MODIFIERS;
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        // 检查是否是玩家造成的伤害
        if (!(event.getSource().getEntity() instanceof Player player)) {
            return;
        }

        // 检查目标是否是活着的实体
        LivingEntity target = event.getEntity();
        if (target == null || target instanceof Player) { // 不对玩家生效
            return;
        }

        // 检查玩家是否装备了暗影匕首
        CuriosApi.getCuriosInventory(player).ifPresent(handler -> {
            handler.findFirstCurio(item -> item.getItem() instanceof DarkDaggerItem).ifPresent(result -> {
                // 计算目标当前生命值百分比
                double healthPercentage = target.getHealth() / target.getMaxHealth();

                // 使用配置的阈值和伤害值
                if (healthPercentage <= ModConfig.getDarkDaggerExecuteThreshold()) {
                    event.setAmount((float)ModConfig.getDarkDaggerExecuteDamage());
                    playExecuteEffects(player, target);
                }
            });
        });
    }

    private static void playExecuteEffects(Player player, LivingEntity target) {
        Level level = player.level();

        // 创建粒子效果
        if (level instanceof ServerLevel serverLevel) {
            // 创建环形粒子效果
            double radius = 1.0;
            int particleCount = 36;
            for (int i = 0; i < particleCount; i++) {
                double angle = 2.0 * Math.PI * i / particleCount;
                double x = target.getX() + radius * Math.cos(angle);
                double z = target.getZ() + radius * Math.sin(angle);

                // 发送灵魂粒子
                serverLevel.sendParticles(
                        net.minecraft.core.particles.ParticleTypes.SOUL,
                        x,
                        target.getY() + 1.0,
                        z,
                        1,
                        0, 0.1D, 0,
                        0.1D
                );

                // 发送暗色烟雾粒子
                serverLevel.sendParticles(
                        net.minecraft.core.particles.ParticleTypes.SMOKE,
                        x,
                        target.getY() + 0.5,
                        z,
                        1,
                        0, 0.05D, 0,
                        0.1D
                );
            }

            // 向上升的灵魂粒子
            for (int i = 0; i < 15; i++) {
                double x = target.getX() + (player.getRandom().nextDouble() - 0.5D) * 1.0D;
                double z = target.getZ() + (player.getRandom().nextDouble() - 0.5D) * 1.0D;

                serverLevel.sendParticles(
                        net.minecraft.core.particles.ParticleTypes.SOUL,
                        x,
                        target.getY() + player.getRandom().nextDouble() * 2.0D,
                        z,
                        1,
                        0, 0.2D, 0,
                        0.05D
                );
            }
        }

        // 播放音效
        level.playSound(null,
                target.getX(),
                target.getY(),
                target.getZ(),
                ModSounds.DARK_DAGGER_EXECUTE.get(), // 使用注册的自定义音效
                SoundSource.PLAYERS,
                1.0F,
                1.0F);
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        super.onEquip(slotContext, prevStack, stack);
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        super.onUnequip(slotContext, newStack, stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.dark_dagger.effect",
                        (int)(ModConfig.getDarkDaggerExecuteThreshold() * 100))
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