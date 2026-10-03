package blacky_moon.miyu.overevaluate.actions.metaevals

import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.OperationResult
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import net.minecraft.network.chat.Component

object OpAthenaPrint : Action {
    override fun operate(env: CastingEnvironment, image: CastingImage, continuation: SpellContinuation): OperationResult {
        if (image.userData.contains("last_mishap")) {
            val mishapJson = image.userData.getString("last_mishap")
            val component = Component.Serializer.fromJson(mishapJson, env.world.registryAccess())

            if (component != null) {
                env.printMessage(component)
            }
        }
        return OperationResult(image.withUsedOp(), listOf(), continuation, HexEvalSounds.NORMAL_EXECUTE)
    }
}