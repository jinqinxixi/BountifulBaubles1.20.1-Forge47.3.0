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
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber
public class RubyHeartItem extends ModifiableBaubleItem {

    public RubyHeartItem(Properties properties) {
        super(properties);
    }

    private static final Modifier[] MODIFIERS = Modifier.values();

    @Override
    public Modifier[] getModifiers() {
        return MODIFIERS;
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag unused) {
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
    public static void onLivingDamage(LivingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        // 检查是否装备了Ruby Heart饰品
        boolean hasRubyHeart = CuriosApi.getCuriosInventory(player)
                .resolve()
                .flatMap(handler -> handler.findFirstCurio(item -> item.getItem() instanceof RubyHeartItem))
                .map(found -> {
                    ItemStack rubyHeart = found.stack();
                    handleDamageProtection(event, player, rubyHeart);
                    return true;
                })
                .orElse(false);
    }

    private static void handleDamageProtection(LivingDamageEvent event, Player player, ItemStack rubyHeart) {
        // 检查当前生命值是否高于阈值
        float currentHealthPercent = player.getHealth() / player.getMaxHealth();
        if (currentHealthPercent <= ModConfig.RUBY_HEART_HEALTH_THRESHOLD.get()) return;

        // 检查是否在冷却中
        if (rubyHeart.hasTag() && rubyHeart.getTag().contains("LastUsed")) {
            long lastUsed = rubyHeart.getTag().getLong("LastUsed");
            if (System.currentTimeMillis() - lastUsed < ModConfig.RUBY_HEART_COOLDOWN.get() * 1000) return;
        }

        // 计算伤害后的生命值
        float damageAmount = event.getAmount();
        float healthAfterDamage = player.getHealth() - damageAmount;

        // 如果伤害会导致死亡
        if (healthAfterDamage <= 0) {
            // 取消原伤害，设置新的伤害值使玩家保留1点生命
            event.setCanceled(true);
            player.setHealth(1);

            // 记录使用时间
            CompoundTag tag = rubyHeart.getOrCreateTag();
            tag.putLong("LastUsed", System.currentTimeMillis());

            // 显示效果
            player.displayClientMessage(
                    Component.translatable("message.bountifulbaubles.ruby_heart.activated")
                            .withStyle(ChatFormatting.RED),
                    true
            );
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.ruby_heart.effect")
                .withStyle(ChatFormatting.BLUE));

        // 添加当前阈值信息
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.ruby_heart.threshold",
                        String.format("%.0f", ModConfig.RUBY_HEART_HEALTH_THRESHOLD.get() * 100))
                .withStyle(ChatFormatting.BLUE));

        // 添加冷却时间信息
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.ruby_heart.cooldown",
                        ModConfig.RUBY_HEART_COOLDOWN.get())
                .withStyle(ChatFormatting.DARK_GREEN));

        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public int getEnchantmentValue() {
        return 0;
    }
}