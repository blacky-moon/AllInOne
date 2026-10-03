package blacky_moon.miyu.overevaluate.frames

import at.petrak.hexcasting.api.casting.SpellList
import at.petrak.hexcasting.api.casting.eval.CastResult
import at.petrak.hexcasting.api.casting.eval.ResolvedPatternType
import at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM
import at.petrak.hexcasting.api.casting.eval.vm.ContinuationFrame
import at.petrak.hexcasting.api.casting.eval.vm.FrameEvaluate
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import blacky_moon.miyu.overevaluate.mishaps.ThemisMishap
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.server.level.ServerLevel
import java.util.Optional

data class ThemisFrame(
    val data: SpellList,
    val code: SpellList,
    val baseStack: List<Iota>?,
    val seen: MutableList<Iota>,
    val priorities: MutableList<Double>,
    override val type: ContinuationFrame.Type<*> = TYPE
) : ContinuationFrame {

    override fun breakDownwards(stack: List<Iota>): Pair<Boolean, List<Iota>> {
        val newStack = baseStack?.toMutableList() ?: mutableListOf()
        val zipped = seen.zip(priorities)
        val sorted = zipped.sortedBy { it.second }
        newStack.add(ListIota(sorted.map { it.first }))
        return true to newStack
    }

    override fun evaluate(continuation: SpellContinuation, level: ServerLevel, vm: CastingVM): CastResult {
        val stack = if (baseStack == null) {
            vm.image.stack.toList()
        } else {
            val top = vm.image.stack.lastOrNull()
            if (top !is DoubleIota)
                return CastResult(
                    ListIota(code),
                    continuation,
                    null,
                    listOf(OperatorSideEffect.DoMishap(ThemisMishap(top), Mishap.Context(null, null))),
                    ResolvedPatternType.ERRORED,
                    HexEvalSounds.MISHAP,
                )
            priorities.add(top.double)
            baseStack
        }

        val (stackTop, newImage, newCont) = if (data.nonEmpty) {
            val cont2 = continuation
                .pushFrame(ThemisFrame(data.cdr, code, stack, seen, priorities))
                .pushFrame(FrameEvaluate(code, true))
            seen.add(data.car)
            Triple(data.car, vm.image.withUsedOp(), cont2)
        } else {
            val zipped = seen.zip(priorities)
            val sorted = zipped.sortedBy { it.second }
            Triple(ListIota(sorted.map { it.first }), vm.image, continuation)
        }

        val newStack = stack.toMutableList()
        newStack.add(stackTop)
        return CastResult(
            ListIota(code),
            newCont,
            newImage.withResetEscape().copy(stack = newStack),
            listOf(),
            ResolvedPatternType.EVALUATED,
            HexEvalSounds.THOTH,
        )
    }

    override fun size() = data.size() + code.size() + seen.size + (baseStack?.size ?: 0)

    companion object {
        @JvmField
        val TYPE: ContinuationFrame.Type<ThemisFrame> = object : ContinuationFrame.Type<ThemisFrame> {

            private val iotaListCodec: Codec<List<Iota>> = Codec.list(IotaType.TYPED_CODEC)

            val CODEC: MapCodec<ThemisFrame> = RecordCodecBuilder.mapCodec { instance ->
                instance.group(
                    SpellList.CODEC.fieldOf("data").forGetter(ThemisFrame::data),
                    SpellList.CODEC.fieldOf("code").forGetter(ThemisFrame::code),
                    iotaListCodec.optionalFieldOf("base").forGetter { frame -> Optional.ofNullable(frame.baseStack) },
                    iotaListCodec.fieldOf("seen").forGetter { frame -> frame.seen.toList() },
                    Codec.DOUBLE.listOf().fieldOf("priorities").forGetter { frame -> frame.priorities.toList() }
                ).apply(instance) { data, code, base, seen, priorities ->
                    ThemisFrame(
                        data,
                        code,
                        base.orElse(null),
                        seen.toMutableList(),
                        priorities.toMutableList()
                    )
                }
            }

            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ThemisFrame> = StreamCodec.composite(
                SpellList.STREAM_CODEC, ThemisFrame::data,
                SpellList.STREAM_CODEC, ThemisFrame::code,
                ByteBufCodecs.optional(ByteBufCodecs.collection({ size -> ArrayList(size) }, IotaType.TYPED_STREAM_CODEC)),
                { frame -> Optional.ofNullable(frame.baseStack) },
                ByteBufCodecs.collection({ size -> ArrayList(size) }, IotaType.TYPED_STREAM_CODEC),
                { frame -> frame.seen },
                ByteBufCodecs.collection({ size -> ArrayList(size) }, ByteBufCodecs.DOUBLE),
                { frame -> frame.priorities },
                { data, code, base, seen, priorities ->
                    ThemisFrame(
                        data,
                        code,
                        base.orElse(null),
                        seen.toMutableList(),
                        priorities.toMutableList()
                    )
                }
            )

            override fun codec(): MapCodec<ThemisFrame> = CODEC
            override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, ThemisFrame> = STREAM_CODEC
        }
    }
}