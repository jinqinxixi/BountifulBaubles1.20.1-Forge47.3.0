package com.jinqinxixi.bountifulbaubles.item.Baubles;

import com.jinqinxixi.bountifulbaubles.config.ModConfig;
import com.jinqinxixi.bountifulbaubles.system.modifier.ModifiableBaubleItem;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber
public class ButchersCleaverItem extends ModifiableBaubleItem {
    private static final Random RANDOM = new Random();

    public ButchersCleaverItem(Properties properties) {
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
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof Player killer && !killer.level().isClientSide) {
            boolean hasButchersCleaver = CuriosApi.getCuriosInventory(killer)
                    .resolve()
                    .flatMap(handler -> handler.findFirstCurio(item -> item.getItem() instanceof ButchersCleaverItem))
                    .isPresent();

            if (hasButchersCleaver && RANDOM.nextFloat() < ModConfig.BUTCHERS_CLEAVER_DROP_CHANCE.get()) {
                LivingEntity victim = event.getEntity();
                ItemStack headStack = findEntityHead(victim);
                if (!headStack.isEmpty()) {
                    victim.spawnAtLocation(headStack);
                }
            }
        }
    }

    private static ItemStack findEntityHead(LivingEntity entity) {
        // 特殊处理玩家头颅
        if (entity instanceof Player player) {
            ItemStack head = new ItemStack(Items.PLAYER_HEAD);
            head.getOrCreateTag().putString("SkullOwner", player.getName().getString());
            return head;
        }

        // 获取实体的注册名称
        ResourceLocation entityId = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
        if (entityId == null) return ItemStack.EMPTY;

        // 构造可能的头颅物品ID模式
        String[] possiblePatterns = {
                // 通用模式
                entityId.getNamespace() + ":head_" + entityId.getPath(),
                entityId.getNamespace() + ":" + entityId.getPath() + "_head",
                entityId.getNamespace() + ":" + entityId.getPath() + "_skull",

                // 特殊模式（用于兼容不同模组的命名习惯）
                entityId.getNamespace() + ":skull_" + entityId.getPath(),
                entityId.getNamespace() + ":" + entityId.getPath() + "_trophy",
                entityId.getNamespace() + ":trophy_" + entityId.getPath()
        };

        // 遍历所有可能的物品ID
        for (String pattern : possiblePatterns) {
            ResourceLocation itemId = new ResourceLocation(pattern);
            Item item = ForgeRegistries.ITEMS.getValue(itemId);
            if (item != null && !item.equals(Items.AIR)) {
                return new ItemStack(item);
            }
        }

        // 检查物品注册表中是否有包含实体名称的头颅物品
        String entityName = entityId.getPath();
        for (Item item : ForgeRegistries.ITEMS) {
            ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(item);
            if (itemId != null) {
                String itemPath = itemId.getPath().toLowerCase();
                // 检查物品名称是否包含实体名称和头颅相关关键词
                if ((itemPath.contains(entityName) || entityName.contains(itemPath)) &&
                        (itemPath.contains("head") || itemPath.contains("skull") || itemPath.contains("trophy"))) {
                    return new ItemStack(item);
                }
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.bountifulbaubles.butchers_cleaver.effect")
                .withStyle(ChatFormatting.BLUE));
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