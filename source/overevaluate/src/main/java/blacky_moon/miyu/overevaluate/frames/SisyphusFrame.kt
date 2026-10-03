package blacky_moon.miyu.overevaluate.frames

import at.petrak.hexcasting.api.casting.SpellList
import at.petrak.hexcasting.api.casting.eval.CastResult
import at.petrak.hexcasting.api.casting.eval.ResolvedPatternType
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM
import at.petrak.hexcasting.api.casting.eval.vm.ContinuationFrame
import at.petrak.hexcasting.api.casting.eval.vm.FrameEvaluate
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.server.level.ServerLevel

data class SisyphusFrame(
    val code: SpellList,
    override val type: ContinuationFrame.Type<*> = TYPE
) : ContinuationFrame {

    override fun breakDownwards(stack: List<Iota>): Pair<Boolean, List<Iota>> {
        return true to stack
    }

    override fun evaluate(continuation: SpellContinuation, world: ServerLevel, vm: CastingVM): CastResult = CastResult(
        ListIota(code),
        continuation
            .pushFrame(SisyphusFrame(code))
            .pushFrame(FrameEvaluate(code, true)),
        vm.image.withResetEscape().withUsedOp(),
        listOf(),
        ResolvedPatternType.EVALUATED,
        HexEvalSounds.THOTH
    )

    override fun size() = code.size()

    companion object {
        @JvmField
        val TYPE: ContinuationFrame.Type<SisyphusFrame> = object : ContinuationFrame.Type<SisyphusFrame> {

            val CODEC: MapCodec<SisyphusFrame> = RecordCodecBuilder.mapCodec { instance ->
                instance.group(
                    SpellList.CODEC.fieldOf("code").forGetter(SisyphusFrame::code)
                ).apply(instance) { code -> SisyphusFrame(code) }
            }

            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, SisyphusFrame> = StreamCodec.composite(
                SpellList.STREAM_CODEC,
                SisyphusFrame::code,
                { code -> SisyphusFrame(code) }
            )

            override fun codec(): MapCodec<SisyphusFrame> = CODEC
            override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, SisyphusFrame> = STREAM_CODEC
        }
    }
}