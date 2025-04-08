package com.jinqinxixi.bountifulbaubles.compat.firstaid;

import com.jinqinxixi.bountifulbaubles.item.Baubles.PhylacteryCharmItem;
import ichttt.mods.firstaid.FirstAid;
import ichttt.mods.firstaid.api.damagesystem.AbstractDamageablePart;
import ichttt.mods.firstaid.api.event.FirstAidLivingDamageEvent;
import ichttt.mods.firstaid.common.network.MessageUpdatePart;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PhylacteryCharmFirstAidCompat {
    public static final String FIRSTAID_MODID = "firstaid";
    private static final UUID PHYLACTERY_UUID = UUID.fromString("bba5e6f7-b8c9-d0e1-f2a3-b4c5d6e7f8a9");

    public static boolean isLoaded() {
        return ModList.get().isLoaded(FIRSTAID_MODID);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onFirstAidLivingDamageLow(FirstAidLivingDamageEvent event) {
        if (event.getEntity() == null || event.getEntity().level().isClientSide()) return;
        if (event.getUndistributedDamage() > 1000) return;
        if (!PhylacteryCharmItem.isEquipped(event.getEntity())) return;

        Player player = event.getEntity();
        boolean failed = false;
        List<AbstractDamageablePart> parts = new ArrayList<>();
        for (AbstractDamageablePart part : event.getAfterDamage()) {
            if (part.canCauseDeath && part.currentHealth <= 0) {
                if (part.getMaxHealth() >= 4) parts.add(part);
                else failed = true;
            }
        }

        if (!failed && !parts.isEmpty()) {
            AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
            AttributeModifier modifier = maxHealth.getModifier(PHYLACTERY_UUID);
            double prevMaxHealthDamage = 0.0;
            if (modifier != null) {
                prevMaxHealthDamage = modifier.getAmount();
            }

            double curMaxHealth = maxHealth.getBaseValue();
            for (AttributeModifier mod : maxHealth.getModifiers(AttributeModifier.Operation.ADDITION)) {
                curMaxHealth += mod.getAmount();
            }
            double originalMaxHealth = curMaxHealth - prevMaxHealthDamage;

            double healthToRemove = (originalMaxHealth * 0.3D) + (double) (parts.size() * 2) + event.getUndistributedDamage();

            if (healthToRemove > curMaxHealth - 2) return;

            for (AbstractDamageablePart part : parts) {
                part.heal(1.0F, null, false);
                if (event.getEntity() instanceof ServerPlayer) {
                    FirstAid.NETWORKING.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                            new MessageUpdatePart(part));
                }
            }

            if (modifier != null) {
                maxHealth.removeModifier(modifier);
            }

            modifier = new AttributeModifier(PHYLACTERY_UUID, "Phylactery MaxHP drain",
                    prevMaxHealthDamage - healthToRemove, AttributeModifier.Operation.ADDITION);
            maxHealth.addPermanentModifier(modifier);

            // 传送玩家
            if (player instanceof ServerPlayer serverPlayer) {
                PhylacteryCharmItem.performTeleport(serverPlayer);
            }
        }
    }
}