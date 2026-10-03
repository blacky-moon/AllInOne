package violetker13.Hex.HexTables.casting

import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.ActionRegistryEntry
import at.petrak.hexcasting.api.casting.castables.Action

import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.common.lib.hex.HexActions

import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import violetker13.Hex.HexTables.HexTables.MOD_ID
import violetker13.Hex.HexTables.spells.OpGetBlockLootTable

object HexTablesPatterns {
    @JvmStatic
    fun init() {
        //register("hexlag_delay", "aqdeeewdwwd", HexDir.SOUTH_WEST, hexlag_delay())
        register("block_loot_tables","wwawaww", HexDir.NORTH_WEST, OpGetBlockLootTable() )

    }


    private fun register(name: String, signature: String, startDir: HexDir, action: Action) {
        Registry.register(
            HexActions.REGISTRY,
            Identifier.of(MOD_ID, name),
            ActionRegistryEntry(HexPattern.fromAngles(signature, startDir), action)
        )
    }
}