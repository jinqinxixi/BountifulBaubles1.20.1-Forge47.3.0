package com.jinqinxixi.bountifulbaubles.item.Baubles;

import com.jinqinxixi.bountifulbaubles.config.ModConfig;
import com.jinqinxixi.bountifulbaubles.system.modifier.ModifiableBaubleItem;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
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
import java.util.UUID;

@Mod.EventBusSubscriber
public class RockCandyItem extends ModifiableBaubleItem {
    private static final UUID SPEED_MODIFIER_UUID = UUID.fromString("9e8f0f0f-0f0f-0f0f-0f0f-0f0f0f0f0f0f");

    public RockCandyItem(Properties properties) {
        super(properties);
    }

    private static final Modifier[] MODIFIERS = Modifier.values();

    @Override
    public Modifier[] getModifiers() {
        return MODIFIERS;
    }

    // 添加一个辅助方法来检查玩家是否已经有速度效果
    private boolean hasSpeedModifier(Player player) {
        return player.getAttribute(Attributes.MOVEMENT_SPEED)
                .getModifier(SPEED_MODIFIER_UUID) != null;
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
                    // 只有当没有速度效果时才添加
                    if (!hasSpeedModifier(player)) {
                        player.getAttribute(Attributes.MOVEMENT_SPEED).addTransientModifier(
                                new AttributeModifier(SPEED_MODIFIER_UUID,
                                        "Rock Candy Speed Boost",
                                        ModConfig.ROCK_CANDY_SPEED_MULTIPLIER.get(),
                                        AttributeModifier.Operation.MULTIPLY_TOTAL));
                    }
                }
            }

            @Override
            public void onUnequip(SlotContext slotContext, ItemStack newStack) {
                if (slotContext.entity() instanceof Player player) {
                    removeModifier(player, stack);

                    // 检查是否还有其他装备的Rock Candy
                    boolean hasOtherRockCandy = CuriosApi.getCuriosInventory(player)
                            .resolve()
                            .map(handler -> handler.findCurios(item ->
                                    item.getItem() instanceof RockCandyItem).size() > 0) // 改为 > 0
                            .orElse(false);

                    // 只有当没有其他Rock Candy时才移除速度效果
                    if (!hasOtherRockCandy) {
                        player.getAttribute(Attributes.MOVEMENT_SPEED)
                                .removeModifier(SPEED_MODIFIER_UUID);
                    }
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
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        // 只检查是否至少装备了一个Rock Candy，不管数量多少
        boolean hasRockCandy = CuriosApi.getCuriosInventory(player)
                .resolve()
                .flatMap(handler -> handler.findFirstCurio(item ->
                        item.getItem() instanceof RockCandyItem))
                .isPresent();

        // 如果装备了Rock Candy，伤害翻倍（只计算一次）
        if (hasRockCandy) {
            event.setAmount(event.getAmount() * ModConfig.ROCK_CANDY_DAMAGE_MULTIPLIER.get().floatValue());
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        // 显示当前的速度加成
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.rock_candy.speed_boost",
                        String.format("%.0f", ModConfig.ROCK_CANDY_SPEED_MULTIPLIER.get() * 100))
                .withStyle(ChatFormatting.BLUE));

        // 显示当前的伤害倍率
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.rock_candy.damage_warning",
                        String.format("%.1f", ModConfig.ROCK_CANDY_DAMAGE_MULTIPLIER.get()))
                .withStyle(ChatFormatting.RED));

        // 添加一个提示，说明效果不会叠加
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.rock_candy.no_stack")
                .withStyle(ChatFormatting.GRAY));

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