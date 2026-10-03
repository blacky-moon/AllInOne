package org.arcticquests.dev.oneironaut.oneironautt.registry;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut;

public class OneironautFluidTypes {
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, Oneironaut.MODID);

    public static final DeferredHolder<FluidType, FluidType> THOUGHT_SLURRY_TYPE =
            FLUID_TYPES.register("thought_slurry", () -> new FluidType(
                    FluidType.Properties.create().descriptionId("fluid.oneironaut.thought_slurry").canExtinguish(true)
            ));

    public static void init(IEventBus modEventBus) {
        FLUID_TYPES.register(modEventBus);
    }

}