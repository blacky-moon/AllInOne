package blacky_moon.miyu.overevaluate.actions.metaevals

import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.GarbageIota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import miyucomics.hexpose.iotas.TextIota
import net.minecraft.network.chat.Component
import net.neoforged.fml.ModList

object OpAthenaLog : Action {
    override fun operate(env: CastingEnvironment, image: CastingImage, continuation: SpellContinuation): OperationResult {
        if (!image.userData.contains("last_mishap")) {
            return OperationResult(
                image.withUsedOp().copy(stack = image.stack.plus(NullIota())),
                listOf(),
                continuation,
                HexEvalSounds.NORMAL_EXECUTE
            )
        }

        val mishapJson = image.userData.getString("last_mishap")

        val newIota = if (ModList.get().isLoaded("hexpose")) {
            val parsedComponent = Component.Serializer.fromJson(mishapJson, env.world.registryAccess())
                ?: Component.empty()
            TextIota(parsedComponent)
        } else {
            GarbageIota()
        }

        return OperationResult(
            image.withUsedOp().copy(stack = image.stack.plus(newIota)),
            listOf(),
            continuation,
            HexEvalSounds.NORMAL_EXECUTE
        )
    }
}