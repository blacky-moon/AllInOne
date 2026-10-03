package dev.kineticcat.complexhex;

import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;


public class ComplexhexAbstractions {
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    public static void initPlatformSpecific() {
        // Nothing platform-specific needed for a single-loader mod.
    }

}