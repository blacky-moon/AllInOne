package org.arcticquests.dev.oneironaut.oneironautt;

import net.minecraft.resources.ResourceLocation;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent;

@EventBusSubscriber(modid = Oneironaut.MODID, value = Dist.CLIENT)
public class OneironautClientEffects {
    @SubscribeEvent
    public static void onRegisterDimensionEffects(RegisterDimensionSpecialEffectsEvent event) {
        event.register(ResourceLocation.fromNamespaceAndPath("oneironaut", "noosphere"), new NoosphereDimensionEffects());
        event.register(ResourceLocation.fromNamespaceAndPath("oneironaut", "deep_noosphere"), new DeepNoosphereDimensionEffects());
        Oneironaut.LOGGER.info("Registered Noosphere/DeepNoosphere dimension effects");
    }
}