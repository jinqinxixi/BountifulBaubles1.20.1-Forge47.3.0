package com.jinqinxixi.bountifulbaubles.item.Baubles;

import com.jinqinxixi.bountifulbaubles.config.ModConfig;
import com.jinqinxixi.bountifulbaubles.system.modifier.ModifiableBaubleItem;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
public class MadAuraItem extends ModifiableBaubleItem {
    private static final String COOLDOWN_KEY = "MadAuraCooldown";
    private static final String TAG_IS_INITIALIZED = "IsInitialized";
    private static final ModifiableBaubleItem.Modifier[] MODIFIERS = ModifiableBaubleItem.Modifier.values();

    public MadAuraItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public ModifiableBaubleItem.Modifier[] getModifiers() {
        return MODIFIERS;
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        // 调用父类的onEquip方法，它会处理修饰符的比较和应用
        super.onEquip(slotContext, prevStack, stack);

        // 只处理首次装备的初始化
        if (!stack.getOrCreateTag().getBoolean(TAG_IS_INITIALIZED)) {
            stack.getOrCreateTag().putBoolean(TAG_IS_INITIALIZED, true);
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        // 调用父类的onUnequip方法，它会处理修饰符的移除
        super.onUnequip(slotContext, newStack, stack);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (slotContext.entity() instanceof Player player) {
            CompoundTag tag = stack.getOrCreateTag();
                    if (tag.contains(COOLDOWN_KEY)) {
                        long cooldownEnd = tag.getLong(COOLDOWN_KEY);
                        if (slotContext.entity().level().getGameTime() >= cooldownEnd) {
                            tag.remove(COOLDOWN_KEY);
                        }
                    }
                }
            }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        // 检查玩家是否装备了疯狂光环
        CuriosApi.getCuriosInventory(player).ifPresent(handler -> {
            handler.findFirstCurio(item -> item.getItem() instanceof MadAuraItem).ifPresent(result -> {
                ItemStack stack = result.stack();
                CompoundTag tag = stack.getOrCreateTag();

                // 检查是否在冷却中
                if (!tag.contains(COOLDOWN_KEY)) {
                    // 抵消所有伤害
                    event.setCanceled(true);

                    // 播放音效
                    player.level().playSound(null,
                            player.getX(), player.getY(), player.getZ(),
                            SoundEvents.ANVIL_LAND,
                            SoundSource.PLAYERS,
                            0.5F,
                            1.5F);

                    // 设置冷却时间
                    tag.putLong(COOLDOWN_KEY, player.level().getGameTime() + ModConfig.getMadAuraCooldown());
                }
            });
        });
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.madaura.effects",
                        ModConfig.getMadAuraCooldown() / 20.0f)  // 转换为秒
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