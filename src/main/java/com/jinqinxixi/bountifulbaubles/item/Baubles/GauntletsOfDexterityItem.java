package com.jinqinxixi.bountifulbaubles.item.Baubles;

import com.jinqinxixi.bountifulbaubles.config.ModConfig;
import com.jinqinxixi.bountifulbaubles.system.modifier.ModifiableBaubleItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.List;
import java.util.UUID;

public class GauntletsOfDexterityItem extends ModifiableBaubleItem {
    private static final ModifiableBaubleItem.Modifier[] MODIFIERS = ModifiableBaubleItem.Modifier.values();
    // 固定修饰符专用UUID（统一用此UUID保证不叠加）
    private static final UUID FIXED_ATTACK_SPEED_UUID = UUID.fromString("6a3e4d5b-1c2d-4f8a-9e7f-0d3a2b1c4d5e");
    @Override
    public ModifiableBaubleItem.Modifier[] getModifiers() {
        return MODIFIERS;
    }
    public GauntletsOfDexterityItem(Properties properties) {
        super(properties);
    }

    // ========== 属性操作 ==========
    @Override
    public void applyModifier(Player player, ItemStack stack) {
        // 先处理随机修饰符
        super.applyModifier(player, stack);

        // 再处理固定攻速
        double fixedValue = ModConfig.getGauntletsAttackSpeed();
        AttributeInstance attr = player.getAttribute(Attributes.ATTACK_SPEED);
        if (attr == null) return;

        // 检查是否已存在固定加成
        AttributeModifier existing = attr.getModifier(FIXED_ATTACK_SPEED_UUID);
        if (existing == null) {
            attr.addPermanentModifier(createFixedModifier(fixedValue));
        } else if (existing.getAmount() != fixedValue) {
            // 配置热更新处理
            attr.removeModifier(FIXED_ATTACK_SPEED_UUID);
            attr.addPermanentModifier(createFixedModifier(fixedValue));
        }
    }

    @Override
    public void removeModifier(Player player, ItemStack stack) {
        // 先移除随机修饰符
        super.removeModifier(player, stack);

        // 再移除固定攻速（仅当没有其他同类型物品时）
        if (!hasSameItemEquipped(player)) {
            AttributeInstance attr = player.getAttribute(Attributes.ATTACK_SPEED);
            if (attr != null) {
                attr.removeModifier(FIXED_ATTACK_SPEED_UUID);
            }
        }
    }

    // ========== 辅助方法 ==========
    private AttributeModifier createFixedModifier(double value) {
        return new AttributeModifier(
                FIXED_ATTACK_SPEED_UUID,
                "bountifulbaubles.fixed_attack_speed",
                value,
                AttributeModifier.Operation.MULTIPLY_TOTAL
        );
    }

    private boolean hasSameItemEquipped(Player player) {
        return CuriosApi.getCuriosInventory(player)
                .resolve()
                .map(handler -> {
                    int count = 0;
                    // 遍历所有Curios槽位类型
                    for (String identifier : handler.getCurios().keySet()) {
                        ICurioStacksHandler stackHandler = handler.getCurios().get(identifier);
                        if (stackHandler != null) {
                            // 遍历槽位中的物品
                            for (int i = 0; i < stackHandler.getSlots(); i++) {
                                ItemStack stack = stackHandler.getStacks().getStackInSlot(i);
                                // 检测是否同类物品
                                if (stack.getItem() instanceof GauntletsOfDexterityItem) {
                                    count++;
                                    // 发现第二个立即返回true
                                    if (count >= 2) return true;
                                }
                            }
                        }
                    }
                    return count >= 2;
                })
                .orElse(false);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        // 显示固定加成（动态读取配置）
        double speed = ModConfig.getGauntletsAttackSpeed() * 100;
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.gauntlets_of_dexterity.effect")
                .withStyle(ChatFormatting.BLUE));
        super.appendHoverText(stack, level, tooltip, flag); // 调用基类方法显示修饰符信息
    }
}
