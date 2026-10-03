package dev.kineticcat.complexhex;

import dev.kineticcat.complexhex.api.util.BITHandlers;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is effectively the loading entrypoint for most of your code, at least
 * if you are using Architectury as intended.
 *
 * NOTE: registration into vanilla Minecraft registries (iota types, action
 * patterns, arithmetics) does NOT happen here anymore. Those registries
 * (HexIotaTypes.REGISTRY, HexActions.REGISTRY, HexArithmetics.REGISTRY) are
 * frozen once NeoForge's registration phase finishes, which happens before
 * FMLCommonSetupEvent (the event this init() method runs on) ever fires.
 * Calling Registry.register on them here throws
 * "Registry is already frozen". They're registered instead from
 * ComplexhexNeoForge's RegisterEvent listener, which fires while those
 * registries are still open. See ComplexhexNeoForge.java.
 */
public class Complexhex {
    public static final String MOD_ID = "complexhex";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);


    public static void init() {
        LOGGER.info("Complex Hex says 'ough'");

        ComplexhexAbstractions.initPlatformSpecific();
        BITHandlers.INSTANCE.init();

        LOGGER.info(ComplexhexAbstractions.getConfigDirectory().toAbsolutePath().normalize().toString());
    }

    /**
     * Shortcut for identifiers specific to this mod.
     */
    public static ResourceLocation id(String string) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, string);
    }
}