package org.blacky_moon.hexcurio.common.inits

import at.petrak.hexcasting.api.casting.ActionRegistryEntry
import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import net.neoforged.neoforge.registries.RegisterEvent
import org.blacky_moon.hexcurio.HexCurio

object HexCurioActions {
    fun init(helper: RegisterEvent.RegisterHelper<ActionRegistryEntry>) {

    }
    private fun register(
        helper: RegisterEvent.RegisterHelper<ActionRegistryEntry>,
        name: String,
        signature: String,
        startDir: HexDir,
        action: Action
    ) {
        helper.register(
            HexCurio.id(name),
            ActionRegistryEntry(HexPattern.fromAngles(signature, startDir), action)
        )
    }
}