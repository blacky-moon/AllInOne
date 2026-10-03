package org.arcticquests.dev.oneironaut.oneironautt;

import at.petrak.hexcasting.api.client.ScryingLensOverlayRegistry;
import at.petrak.hexcasting.common.items.ItemStaff;
import at.petrak.hexcasting.common.items.magic.ItemPackagedHex;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.arcticquests.dev.oneironaut.oneironautt.block.InactiveSlipwayBlock;
import org.arcticquests.dev.oneironaut.oneironautt.block.blockentity.HoverElevatorBlockEntity;
import org.arcticquests.dev.oneironaut.oneironautt.block.blockentity.WispBatteryEntity;
import org.arcticquests.dev.oneironaut.oneironautt.item.ItemLibraryCard;
import org.arcticquests.dev.oneironaut.oneironautt.item.ReverberationRod;
import org.arcticquests.dev.oneironaut.oneironautt.item.WispCaptureItem;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautBlockRegistry;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautItemRegistry;

import java.util.*;

public final class OneironautClient {
    private OneironautClient() {}

    public static long lastShiftingHoverTick = 0L;
    public static ItemStack lastHoveredShifting = null;

    private static Minecraft cachedClient = null;
    public static Minecraft getCachedClient() {
        return cachedClient;
    }

    private static float processObservationPredicate(ItemStack stack, ClientLevel world, LivingEntity holder, int holderID) {
        Minecraft client = Minecraft.getInstance();
        LocalPlayer cachedPlayer = client.player;

        final float OFF = 0.99f;
        final float ON = -0.01f;
        float output = ON;

        int fov = client.options.fov().get();
        double threshold = fov / (fov <= 85 ? 90.0 : 100.0);

        if (cachedPlayer != null) {
            if (stack.isFramed() && stack.getFrame() != null) {
                if (MiscAPIKt.vecProximity(stack.getFrame().position().subtract(cachedPlayer.getEyePosition()), cachedPlayer.getLookAngle()) <= threshold) {
                    output = OFF;
                }
            }
            if (stack.getEntityRepresentation() != null && stack.getEntityRepresentation() != cachedPlayer) {
                Vec3 holderCenterApprox = stack.getEntityRepresentation().position().add(stack.getEntityRepresentation().getEyePosition()).scale(0.5);
                if (MiscAPIKt.vecProximity(holderCenterApprox.subtract(cachedPlayer.getEyePosition()), cachedPlayer.getLookAngle()) <= threshold) {
                    output = OFF;
                }
            }
            if (holder == cachedPlayer && (holder.getItemInHand(InteractionHand.MAIN_HAND) == stack || holder.getItemInHand(InteractionHand.OFF_HAND) == stack)) {
                output = OFF;
            }
            if (cachedPlayer.containerMenu.getCarried() == stack ||
                    (lastShiftingHoverTick + 1 >= cachedPlayer.level().getGameTime() && lastHoveredShifting == stack)) {
                output = OFF;
            }
        }

        if (!client.isWindowActive()) output = ON;
        return output;
    }

    @EventBusSubscriber(modid = Oneironaut.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
    public static final class ModBus {
        private ModBus() {}

        @SubscribeEvent
        public static void onClientSetup(final FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                cachedClient = Minecraft.getInstance();
                ItemBlockRenderTypes.setRenderLayer(OneironautBlockRegistry.RAYCAST_BLOCKER_GLASS.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(OneironautBlockRegistry.WISP_LANTERN.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(OneironautBlockRegistry.WISP_LANTERN_TINTED.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(OneironautBlockRegistry.CELL.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(OneironautBlockRegistry.MEDIA_GEL.get(), RenderType.translucent());
                ScryingLensOverlayRegistry.addDisplayer(
                        OneironautBlockRegistry.WISP_BATTERY.get(),
                        WispBatteryEntity::applyScryingLensOverlay
                );




                ItemPackagedHex[] castingItems = {
                        OneironautItemRegistry.REVERBERATION_ROD.get(),
                        OneironautItemRegistry.BOTTOMLESS_CASTING_ITEM.get()
                };
                for (ItemPackagedHex item : castingItems) {
                    ItemProperties.register(item, ItemPackagedHex.HAS_PATTERNS_PRED,
                            (stack, world, holder, holderID) -> item.hasHex(stack) ? 0.99f : -0.01f);
                }

                ItemProperties.register(OneironautItemRegistry.REVERBERATION_ROD.get(), ReverberationRod.CASTING_PREDICATE,
                        (stack, world, holder, holderID) -> holder != null && holder.getUseItem().equals(stack) ? 0.99f : -0.01f);

                ItemProperties.register(OneironautItemRegistry.WISP_CAPTURE_ITEM.get(), WispCaptureItem.FILLED_PREDICATE,
                        (stack, world, holder, holderID) -> ((WispCaptureItem) stack.getItem()).hasWisp(stack, world) ? 0.99f : -0.01f);
                ItemProperties.register(
                        OneironautItemRegistry.SHIFTING_PSEUDOAMETHYST.get(),
                        ResourceLocation.fromNamespaceAndPath(Oneironaut.MODID, "observation"),
                        OneironautClient::processObservationPredicate
                );

                ItemProperties.register(
                        OneironautItemRegistry.LIBRARY_CARD.get(),
                        ResourceLocation.fromNamespaceAndPath(Oneironaut.MODID, "written"),
                        (stack, world, holder, holderID) -> ((ItemLibraryCard) stack.getItem()).getDimension(stack) != null ? 0.99f : -0.01f
                );

                Item[] nameSensitiveStaves = {
                        OneironautItemRegistry.ECHO_STAFF.get(),
                        OneironautItemRegistry.BEACON_STAFF.get(),
                        OneironautItemRegistry.SPOON_STAFF.get()
                };
                for (Item staff : nameSensitiveStaves) {
                    ItemProperties.register(staff, ItemStaff.FUNNY_LEVEL_PREDICATE, (stack, level, holder, holderID) -> {
                        if (!stack.has(DataComponents.CUSTOM_NAME)) return 0f;
                        var name = stack.getHoverName().getString().toLowerCase(Locale.ROOT);
                        if (name.contains("old")) return 1f;
                        if (name.contains("wand of the forest")) return 2f;
                        return 0f;
                    });
                }
                InactiveSlipwayBlock.init();
                Oneironaut.LOGGER.info("Cached client object. Player: {}", cachedClient.player);
            });
        }
    }

    @EventBusSubscriber(modid = Oneironaut.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
    public static final class GameBus {
        private GameBus() {}

        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
            Minecraft client = Minecraft.getInstance();
            try {
                HoverElevatorBlockEntity.processHover(false, client.level != null ? client.level.getGameTime() : -1L);
            } catch (ConcurrentModificationException exception) {
                Oneironaut.LOGGER.error("Oopsie client-side hoverlift exception {}", exception.getMessage());
            }
        }
    }

    public static boolean isWorldClientNoosphere(Level world) {
        if (world instanceof ClientLevel clientWorld) {
            return clientWorld.effects().getClass() == NoosphereDimensionEffects.class
                    || clientWorld.effects().getClass() == DeepNoosphereDimensionEffects.class;
        }
        return false;
    }

    public static boolean isWorldClientDeepNoosphere(Level world) {
        if (world instanceof ClientLevel clientWorld) {
            return clientWorld.effects().getClass() == DeepNoosphereDimensionEffects.class;
        }
        return false;
    }

    private static final class RegistryShim<T extends Block> {
        private final DeferredHolder<Block, T> ro;

        private RegistryShim(DeferredHolder<Block, T> ro) {
            this.ro = ro;
        }

        private T get() {
            return ro.get();
        }
    }
}