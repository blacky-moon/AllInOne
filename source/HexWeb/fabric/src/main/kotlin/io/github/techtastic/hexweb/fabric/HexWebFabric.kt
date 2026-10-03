package io.github.techtastic.hexweb.fabric

import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry
import io.github.techtastic.hexweb.HexWeb.MOD_ID
import io.github.techtastic.hexweb.HexWeb.init
import io.github.techtastic.hexweb.config.HexWebConfig
import net.fabricmc.api.ModInitializer
import net.minecraftforge.fml.config.ModConfig

object HexWebFabric : ModInitializer {
    override fun onInitialize() {
        init()

        val CONFIG = ForgeConfigRegistry.INSTANCE.register(MOD_ID, ModConfig.Type.SERVER, HexWebConfig.SPEC)
    }
}