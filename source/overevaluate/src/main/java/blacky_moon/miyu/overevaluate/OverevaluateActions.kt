package blacky_moon.miyu.overevaluate

import at.petrak.hexcasting.api.casting.ActionRegistryEntry
import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.castables.SpecialHandler
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.common.casting.actions.stack.OpTwiddling
import at.petrak.hexcasting.common.lib.hex.HexActions
import at.petrak.hexcasting.xplat.IXplatAbstractions
import blacky_moon.miyu.overevaluate.actions.OpDioscuriII
import blacky_moon.miyu.overevaluate.actions.jumble.OpCreateJumble
import blacky_moon.miyu.overevaluate.actions.jumble.OpDissolveJumble
import blacky_moon.miyu.overevaluate.actions.list_utils.OpRotateList
import blacky_moon.miyu.overevaluate.actions.list_utils.OpSnapList
import blacky_moon.miyu.overevaluate.actions.math.OpBetween
import blacky_moon.miyu.overevaluate.actions.math.OpHadamard
import blacky_moon.miyu.overevaluate.actions.metaevals.*
import blacky_moon.miyu.overevaluate.actions.modifiers.OpKeepArguments
import blacky_moon.miyu.overevaluate.actions.soroban.OpSorobanPop
import blacky_moon.miyu.overevaluate.actions.soroban.OpSorobanPush
import blacky_moon.miyu.overevaluate.actions.soroban.OpSorobanRetrieve
import blacky_moon.miyu.overevaluate.handlers.*
import net.minecraft.core.Registry
import net.neoforged.neoforge.registries.RegisterEvent

object OverevaluateActions {

    fun init(helper: RegisterEvent.RegisterHelper<ActionRegistryEntry>) {
        register(helper, "ponder", "ddq", HexDir.NORTH_WEST, OpKeepArguments)

        register(helper, "athena", "dweaqqw", HexDir.SOUTH_EAST, OpAthena)
        register(helper, "maat", "qed", HexDir.NORTH_EAST, OpMaat)
        register(helper, "athena_print", "dweaqqqqa", HexDir.SOUTH_EAST, OpAthenaPrint)
        register(helper, "athena_log", "dweaqqqqd", HexDir.SOUTH_EAST, OpAthenaLog)

        register(helper, "create_jumble", "deaqd", HexDir.WEST, OpCreateJumble)
        register(helper, "dissolve_jumble", "aedqa", HexDir.SOUTH_WEST, OpDissolveJumble)

        register(helper, "soroban_retrieve", "waqdee", HexDir.SOUTH_EAST, OpSorobanRetrieve)
        register(helper, "soroban_push", "wdeaqq", HexDir.NORTH_EAST, OpSorobanPush)
        register(helper, "soroban_pop", "qdeeaae", HexDir.NORTH_EAST, OpSorobanPop)

        register(helper, "rotate_list", "wwaadaqadae", HexDir.EAST, OpRotateList)
        register(helper, "snap_list", "eawdq", HexDir.EAST, OpSnapList)
        register(helper, "apep", "dqd", HexDir.EAST, OpApep)
        register(helper, "themis", "dwaad", HexDir.WEST, OpThemis)

        registerSpecialHandler("root", RootHandler.Factory())

        register(helper, "hadamard", "waqaww", HexDir.SOUTH_EAST, OpHadamard)
        register(helper, "between", "wqw", HexDir.EAST, OpBetween)

        register(helper, "tutu", "eedqa", HexDir.WEST, OpTutu)
        register(helper, "janus", "aadee", HexDir.SOUTH_WEST, OpJanus)
        registerSpecialHandler("pluto", PlutoHandler.Factory())
        registerSpecialHandler("nephthys", NephthysHandler.Factory())
        register(helper, "atalanta", "aqdea", HexDir.SOUTH_WEST, OpAtalanta)
        register(helper, "castor", "adadee", HexDir.NORTH_WEST, OpCastor)
        register(helper, "pollux", "dadaqq", HexDir.NORTH_EAST, OpPollux)
        register(helper, "sisyphus", "qaqwede", HexDir.NORTH_EAST, OpSisyphus)

        registerSpecialHandler("sekhmet", SekhmetHandler.Factory())
        registerSpecialHandler("geb", GebHandler.Factory())
        registerSpecialHandler("nut", NutHandler.Factory())
        register(helper, "swap_one_three", "ddwqaq", HexDir.NORTH_EAST, OpTwiddling(3, intArrayOf(2, 1, 0)))
        register(helper, "swap_two_three", "aawede", HexDir.EAST, OpTwiddling(3, intArrayOf(1, 0, 2)))
        register(helper, "dup_many", "waadadaa", HexDir.EAST, OpDioscuriII)
    }

    private fun register(
        helper: RegisterEvent.RegisterHelper<ActionRegistryEntry>,
        name: String,
        signature: String,
        startDir: HexDir,
        action: Action
    ) {
        helper.register(
            Overevaluate.id(name),
            ActionRegistryEntry(HexPattern.fromAngles(signature, startDir), action)
        )
    }

    private fun <T : SpecialHandler> registerSpecialHandler(name: String, handler: SpecialHandler.Factory<T>) {
        Registry.register(IXplatAbstractions.INSTANCE.specialHandlerRegistry, Overevaluate.id(name), handler)
    }
}