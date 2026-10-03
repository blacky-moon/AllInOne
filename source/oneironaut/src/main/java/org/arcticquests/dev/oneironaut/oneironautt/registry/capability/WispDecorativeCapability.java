package org.arcticquests.dev.oneironaut.oneironautt.registry.capability;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.EntityCapability;
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut;

public final class WispDecorativeCapability {
    private WispDecorativeCapability() {}

    public static final EntityCapability<IBoolComponent, Void> INSTANCE =
            EntityCapability.createVoid(
                    ResourceLocation.fromNamespaceAndPath(Oneironaut.MODID, "wisp_decorative"),
                    IBoolComponent.class
            );
}