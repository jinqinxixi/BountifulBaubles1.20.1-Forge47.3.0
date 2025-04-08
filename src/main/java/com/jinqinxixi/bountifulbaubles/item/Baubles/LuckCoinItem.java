package com.jinqinxixi.bountifulbaubles.item.Baubles;

import com.jinqinxixi.bountifulbaubles.config.ModConfig;
import com.jinqinxixi.bountifulbaubles.system.modifier.ModifiableBaubleItem;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber
public class LuckCoinItem extends ModifiableBaubleItem {
    private static final Random RANDOM = new Random();

    public LuckCoinItem(Properties properties) {
        super(properties);
    }

    @Override
    public ModifiableBaubleItem.Modifier[] getModifiers() {
        return ModifiableBaubleItem.Modifier.values();
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
    public static void onLivingHurt(LivingHurtEvent event) {
        // 检查是否是玩家造成的伤害
        if (!(event.getSource().getEntity() instanceof Player player)) {
            return;
        }

        // 检查玩家是否装备了幸运硬币
        boolean hasLuckCoin = CuriosApi.getCuriosInventory(player).resolve()
                .flatMap(handler -> handler.findFirstCurio(item -> item.getItem() instanceof LuckCoinItem))
                .isPresent();

        if (!hasLuckCoin) {
            return;
        }

        // 获取配置值
        List<Double> bonuses = ModConfig.getLuckCoinDamageBonuses();
        List<Double> probabilities = ModConfig.getLuckCoinProbabilities();

        // 确保两个列表长度相同
        if (bonuses.size() != probabilities.size()) {
            return;
        }

        // 生成随机数并确定加成
        double roll = RANDOM.nextDouble();
        double cumulativeProbability = 0.0;

        for (int i = 0; i < probabilities.size(); i++) {
            cumulativeProbability += probabilities.get(i);
            if (roll < cumulativeProbability) {
                // 应用伤害加成
                float bonus = (float)(1.0 + bonuses.get(i));
                event.setAmount(event.getAmount() * bonus);
                break;
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        List<Double> bonuses = ModConfig.getLuckCoinDamageBonuses();
        List<Double> probabilities = ModConfig.getLuckCoinProbabilities();

        tooltip.add(Component.translatable("tooltip.bountifulbaubles.luckcoin.title")
                .withStyle(ChatFormatting.BLUE));

        for (int i = 0; i < bonuses.size() && i < probabilities.size(); i++) {
            tooltip.add(Component.translatable("tooltip.bountifulbaubles.luckcoin.bonus_line",
                            probabilities.get(i) * 100,
                            bonuses.get(i) * 100)
                    .withStyle(ChatFormatting.BLUE));
        }

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