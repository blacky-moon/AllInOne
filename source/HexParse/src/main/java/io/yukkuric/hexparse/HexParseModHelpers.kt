package io.yukkuric.hexparse

import net.neoforged.fml.ModList
import net.neoforged.fml.loading.FMLEnvironment

class HexParseModHelpers : IModHelpers {
    override fun modLoaded(modId: String): Boolean = ModList.get().isLoaded(modId)
    override fun isPhysicalClient(): Boolean = FMLEnvironment.dist.isClient
}