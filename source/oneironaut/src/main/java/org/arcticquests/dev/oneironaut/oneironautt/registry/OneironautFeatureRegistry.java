package org.arcticquests.dev.oneironaut.oneironautt.registry;


import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut;
import org.arcticquests.dev.oneironaut.oneironautt.feature.*;

public class OneironautFeatureRegistry {

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, Oneironaut.MODID);
    public static void init(IEventBus eventBus){
        FEATURES.register(eventBus);
    }
    //islands
    public static final DeferredHolder<Feature<?>, NoosphereSeaIsland> NOOSPHERE_SEA_ISLAND = FEATURES.register("noosphere_sea_island", () -> new NoosphereSeaIsland(NoosphereSeaIslandConfig.CODEC));
    public static final DeferredHolder<Feature<?>, NoosphereSeaVolcano> NOOSPHERE_SEA_VOLCANO = FEATURES.register("noosphere_sea_volcano", () -> new NoosphereSeaVolcano(NoosphereSeaVolcanoConfig.CODEC));
    public static final DeferredHolder<Feature<?>, BlockVein> BLOCK_VEIN = FEATURES.register("block_vein", () -> new BlockVein(BlockVeinConfig.CODEC));
    public static final DeferredHolder<Feature<?>, BlockBlob> BLOCK_BLOB = FEATURES.register("block_blob", () -> new BlockBlob(BlockBlobConfig.CODEC));
}