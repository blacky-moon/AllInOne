package blacky_moon.miyu.overevaluate.frames

import at.petrak.hexcasting.api.casting.eval.CastResult
import at.petrak.hexcasting.api.casting.eval.ResolvedPatternType
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM
import at.petrak.hexcasting.api.casting.eval.vm.ContinuationFrame
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.server.level.ServerLevel

data class NephthysFrame(
    val original: List<Iota>,
    override val type: ContinuationFrame.Type<*> = TYPE
) : ContinuationFrame {

    override fun evaluate(continuation: SpellContinuation, level: ServerLevel, vm: CastingVM): CastResult {
        return CastResult(
            ListIota(original),
            continuation,
            vm.image.withResetEscape().copy(stack = vm.image.stack.toMutableList().plus(original)),
            listOf(),
            ResolvedPatternType.EVALUATED,
            HexEvalSounds.HERMES
        )
    }

    override fun breakDownwards(stack: List<Iota>): Pair<Boolean, List<Iota>> {
        return true to stack
    }

    override fun size() = original.size

    companion object {
        @JvmField
        val TYPE: ContinuationFrame.Type<NephthysFrame> = object : ContinuationFrame.Type<NephthysFrame> {

            private val iotaListCodec: Codec<List<Iota>> = Codec.list(IotaType.TYPED_CODEC)

            val CODEC: MapCodec<NephthysFrame> = RecordCodecBuilder.mapCodec { instance ->
                instance.group(
                    iotaListCodec.fieldOf("original").forGetter(NephthysFrame::original)
                ).apply(instance) { original -> NephthysFrame(original) }
            }

            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, NephthysFrame> = StreamCodec.composite(
                ByteBufCodecs.collection({ size -> ArrayList(size) }, IotaType.TYPED_STREAM_CODEC),
                NephthysFrame::original,
                { original -> NephthysFrame(original) }
            )

            override fun codec(): MapCodec<NephthysFrame> = CODEC
            override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, NephthysFrame> = STREAM_CODEC
        }
    }
}