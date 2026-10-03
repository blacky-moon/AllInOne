package me.nanorasmus.nanodev.hex_js

import net.minecraft.resources.ResourceLocation
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.fml.loading.FMLEnvironment

@Mod(HexJSNeoForge.MOD_ID)
class HexJSNeoForge(modEventBus: IEventBus, modContainer: ModContainer) {

    init {
        MOD_EVENT_BUS = modEventBus

        HexJS.init()

        if (FMLEnvironment.dist == Dist.CLIENT) {
            HexJS.initClient()
        } else if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
            HexJS.initServer()
        }
    }

    companion object {
        const val MOD_ID: String = "hexjs"

        lateinit var MOD_EVENT_BUS: IEventBus

        @JvmStatic
        fun id(path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(MOD_ID, path)
    }
}