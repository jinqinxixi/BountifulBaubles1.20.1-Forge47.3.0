    package com.jinqinxixi.bountifulbaubles.item.Baubles;

    import com.jinqinxixi.bountifulbaubles.system.modifier.ModifiableBaubleItem;
    import net.minecraft.ChatFormatting;
    import net.minecraft.network.chat.Component;
    import net.minecraft.world.item.ItemStack;
    import net.minecraft.world.item.TooltipFlag;
    import net.minecraft.world.level.Level;
    import top.theillusivec4.curios.api.SlotContext;

    import javax.annotation.Nullable;
    import java.util.List;

    public class GluttonyPendantItem extends ModifiableBaubleItem {

        private static final ModifiableBaubleItem.Modifier[] MODIFIERS = ModifiableBaubleItem.Modifier.values();
        @Override
        public ModifiableBaubleItem.Modifier[] getModifiers() {
            return MODIFIERS;
        }

        public GluttonyPendantItem(Properties pProperties) {
            super(pProperties);
        }

        @Override
        public void curioTick(SlotContext slotContext, ItemStack stack) {
            // 可选的每tick逻辑
        }
        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
            tooltip.add(Component.translatable("tooltip.bountifulbaubles.gluttony_pendant.effect")
                    .withStyle(ChatFormatting.BLUE));
            super.appendHoverText(stack, level, tooltip, flag); // 调用基类方法显示修饰符信息
        }
        @Override
        public int getEnchantmentValue() {
            return 0; // 附魔等级为0
        }

        // 禁止任何形式的附魔（包括铁砧）
        @Override
        public boolean isEnchantable(ItemStack stack) {
            return false;
        }
    }
