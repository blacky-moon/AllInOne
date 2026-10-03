package blacky_moon.miyu.overevaluate.frames

import at.petrak.hexcasting.api.casting.eval.CastResult
import at.petrak.hexcasting.api.casting.eval.ResolvedPatternType
import at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM
import at.petrak.hexcasting.api.casting.eval.vm.ContinuationFrame
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.BooleanIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import com.llamalad7.mixinextras.injector.wrapoperation.Operation
import com.mojang.serialization.JsonOps
import com.mojang.serialization.MapCodec
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.network.codec.StreamCodec
import net.minecraft.server.level.ServerLevel

object AthenaFrame : ContinuationFrame {

    val TYPE: ContinuationFrame.Type<AthenaFrame> = object : ContinuationFrame.Type<AthenaFrame> {
        override fun codec(): MapCodec<AthenaFrame> = MapCodec.unit(AthenaFrame)

        override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, AthenaFrame> = StreamCodec.unit(AthenaFrame)
    }

    override val type: ContinuationFrame.Type<*> = TYPE

    override fun breakDownwards(stack: List<Iota>): Pair<Boolean, List<Iota>> {
        val oldStack = stack.toMutableList()
        oldStack.add(BooleanIota(false))
        return true to oldStack
    }

    override fun evaluate(continuation: SpellContinuation, level: ServerLevel, vm: CastingVM): CastResult {
        val stack = vm.image.stack.toMutableList()
        stack.add(BooleanIota(false))
        return CastResult(
            NullIota(),
            continuation,
            vm.image.copy(stack = stack),
            listOf(),
            ResolvedPatternType.EVALUATED,
            HexEvalSounds.NOTHING,
        )
    }

    override fun size() = 0

    @JvmStatic
    fun handleAthena(
        iota: Iota,
        vm: CastingVM,
        world: ServerLevel,
        continuation: SpellContinuation,
        originalMethod: Operation<CastResult>
    ): CastResult {
        val original = originalMethod.call(iota, world, continuation)

        when (original.resolutionType) {
            ResolvedPatternType.EVALUATED, ResolvedPatternType.ESCAPED, ResolvedPatternType.UNDONE -> {
                return original
            }
            ResolvedPatternType.UNRESOLVED, ResolvedPatternType.ERRORED, ResolvedPatternType.INVALID -> {}
            else -> {}
        }

        val newCont = findResumePoint(continuation) ?: return original

        val stack = vm.image.stack.toMutableList()
        stack.add(BooleanIota(true))
        val newImage = vm.image.copy(stack = stack)

        val mishap = original.sideEffects.find { it is OperatorSideEffect.DoMishap } as? OperatorSideEffect.DoMishap
        if (mishap != null) {
            val mishapComponent = mishap.mishap.errorMessageWithName(vm.env, mishap.errorCtx)
            if (mishapComponent != null) {
                val mishapJson = ComponentSerialization.CODEC.encodeStart(
                    JsonOps.INSTANCE,
                    mishapComponent
                ).getOrThrow().toString()

                newImage.userData.putString("last_mishap", mishapJson)
            }
        }

        return CastResult(
            iota,
            newCont,
            newImage,
            listOf(),
            ResolvedPatternType.EVALUATED,
            HexEvalSounds.NORMAL_EXECUTE
        )
    }

    private fun findResumePoint(continuation: SpellContinuation): SpellContinuation? {
        var cont = continuation
        while (cont is SpellContinuation.NotDone) {
            if (cont.frame is AthenaFrame)
                return cont.next
            cont = cont.next
        }
        return null
    }
}