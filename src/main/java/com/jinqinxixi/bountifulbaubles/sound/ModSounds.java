package com.jinqinxixi.bountifulbaubles.sound;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, "bountifulbaubles");

    public static final RegistryObject<SoundEvent> DARK_DAGGER_EXECUTE =
            registerSoundEvent("dark_dagger_execute");

    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation location = new ResourceLocation("bountifulbaubles", name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(location));
    }
}