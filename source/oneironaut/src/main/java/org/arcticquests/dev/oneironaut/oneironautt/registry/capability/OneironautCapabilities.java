package org.arcticquests.dev.oneironaut.oneironautt.registry.capability;

import at.petrak.hexcasting.forge.cap.HexCapabilities;
import at.petrak.hexcasting.forge.cap.adimpl.CapStaticMediaHolder;
import com.mojang.serialization.Codec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut;
import org.arcticquests.dev.oneironaut.oneironautt.item.PseudoamethystShard;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautItemRegistry;

import java.util.function.Supplier;

public final class OneironautCapabilities {
    private OneironautCapabilities() {}

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Oneironaut.MODID);

    public static final Supplier<AttachmentType<Boolean>> WISP_DECORATIVE =
            ATTACHMENT_TYPES.register("wisp_decorative", () ->
                    AttachmentType.builder(() -> false)
                            .serialize(Codec.BOOL)
                            .build()
            );

    public static void register(IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
    }

    public static void registerCaps(RegisterCapabilitiesEvent evt) {
        evt.registerItem(
                HexCapabilities.Item.MEDIA,
                (stack, ctx) -> new CapStaticMediaHolder(() -> (long) PseudoamethystShard.PSEUDOSHARD_UNIT, 1500, stack),
                OneironautItemRegistry.PSUEDOAMETHYST_SHARD.get()
        );
    }
}