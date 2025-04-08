package com.jinqinxixi.bountifulbaubles.system.effect;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;

import java.util.UUID;
import java.util.function.Consumer;

import static com.jinqinxixi.bountifulbaubles.BountifulBaublesMod.MOD_ID;

public class SinfulEffect extends MobEffect {
    private static final UUID DAMAGE_UUID = UUID.fromString("a3b4c5d6-7e8f-49a1-b2c3-d4e5f6a7b8c9");
    private static final UUID ARMOR_UUID = UUID.fromString("d9e8f7a6-b5c4-4d3e-8f1a-9b0c1d2e3f4a");
    private static final UUID TOUGHNESS_UUID = UUID.fromString("1a2b3c4d-5e6f-4a3b-8c7d-6e5f4a3b2c1d");

    public SinfulEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x8B0000); // 深红色效果粒子
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.level().isClientSide && entity instanceof Player player) {
            // 先清除旧修饰器防止重复添加
            removeModifiers(player);
            applyModifiers(player, amplifier + 1);
        }
    }

    private void applyModifiers(Player player, int level) {
        AttributeInstance damageAttr = player.getAttribute(Attributes.ATTACK_DAMAGE);
        AttributeInstance armorAttr = player.getAttribute(Attributes.ARMOR);
        AttributeInstance toughnessAttr = player.getAttribute(Attributes.ARMOR_TOUGHNESS);

        // 攻击伤害修饰器（乘法叠加）
        AttributeModifier damageModifier = new AttributeModifier(
                DAMAGE_UUID,
                "sinful_damage",
                0.25 * level,
                AttributeModifier.Operation.MULTIPLY_TOTAL
        );

        // 护甲修饰器（加法叠加）
        AttributeModifier armorModifier = new AttributeModifier(
                ARMOR_UUID,
                "sinful_armor",
                3.0 * level,
                AttributeModifier.Operation.ADDITION
        );

        // 护甲韧性修饰器（加法叠加）
        AttributeModifier toughnessModifier = new AttributeModifier(
                TOUGHNESS_UUID,
                "sinful_toughness",
                1.0 * level,
                AttributeModifier.Operation.ADDITION
        );

        if (damageAttr != null) {
            if (!damageAttr.hasModifier(damageModifier)) {
                damageAttr.addTransientModifier(damageModifier);
            }
        }

        if (armorAttr != null) {
            if (!armorAttr.hasModifier(armorModifier)) {
                armorAttr.addTransientModifier(armorModifier);
            }
        }

        if (toughnessAttr != null) {
            if (!toughnessAttr.hasModifier(toughnessModifier)) {
                toughnessAttr.addTransientModifier(toughnessModifier);
            }
        }
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributes, int amplifier) {
        super.removeAttributeModifiers(entity, attributes, amplifier);
        if (entity instanceof Player player) {
            removeModifiers(player);
        }
    }

    private void removeModifiers(Player player) {
        AttributeInstance damageAttr = player.getAttribute(Attributes.ATTACK_DAMAGE);
        AttributeInstance armorAttr = player.getAttribute(Attributes.ARMOR);
        AttributeInstance toughnessAttr = player.getAttribute(Attributes.ARMOR_TOUGHNESS);

        if (damageAttr != null) {
            damageAttr.removeModifier(DAMAGE_UUID);
        }
        if (armorAttr != null) {
            armorAttr.removeModifier(ARMOR_UUID);
        }
        if (toughnessAttr != null) {
            toughnessAttr.removeModifier(TOUGHNESS_UUID);
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true; // 每tick都生效
    }
    @Override
    public void initializeClient(Consumer<IClientMobEffectExtensions> consumer) {
        consumer.accept(new IClientMobEffectExtensions() {
            @Override
            public boolean isVisibleInGui(MobEffectInstance effect) {
                return true; // 确保在GUI中可见
            }

            public ResourceLocation getIconTexture(MobEffectInstance instance) {
                return new ResourceLocation(MOD_ID, "textures/mob_effect/sinful.png");
            }
        });
    }
}
