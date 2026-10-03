package org.arcticquests.dev.oneironaut.oneironautt

import net.neoforged.neoforge.common.ModConfigSpec

object OneironautForgeConfig {
    val SERVER_SPEC: ModConfigSpec
    val SERVER: Server

    class Server(builder: ModConfigSpec.Builder) {
        val planeShiftOtherPlayers = builder.define("planeShiftOtherPlayers", false)
        val planeShiftNonliving = builder.define("planeShiftNonliving", true)
        val ideaLifetime = builder.defineInRange("ideaLifetime", 20 * 60 * 60, 1, Int.MAX_VALUE)
        val swapRequiresNoosphere = builder.define("swapRequiresNoosphere", true)
        val swapSwapsBEs = builder.define("swapSwapsBEs", true)
        val impulseRedirectsFireball = builder.define("impulseRedirectsFireball", true)
        val infusionEternalChorus = builder.define("infusionEternalChorus", true)
        val allowOverworldReflection = builder.define("allowOverworldReflection", true)
        val allowNetherReflection = builder.define("allowNetherReflection", true)
        val staleIPhialLenience = builder.defineInRange("staleIPhialLenience", 0.1, 0.0, 1.0)
    }

    init {
        val b = ModConfigSpec.Builder()
        SERVER = Server(b)
        SERVER_SPEC = b.build()
    }
}