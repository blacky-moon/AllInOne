package io.github.techtastic.hexweb.forge

import dev.architectury.platform.forge.EventBuses
import io.github.techtastic.hexweb.HexWeb
import io.github.techtastic.hexweb.HexWeb.init
import io.github.techtastic.hexweb.config.HexWebConfig
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig
import thedarkcolour.kotlinforforge.KotlinModLoadingContext

@Mod(HexWeb.MOD_ID)
class HexWebForge {
    init {
        EventBuses.registerModEventBus(HexWeb.MOD_ID, KotlinModLoadingContext.get().getKEventBus())

        init()

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, HexWebConfig.SPEC)
    }
}
