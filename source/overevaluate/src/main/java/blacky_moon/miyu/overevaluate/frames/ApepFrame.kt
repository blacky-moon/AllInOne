package blacky_moon.miyu.overevaluate.frames

import at.petrak.hexcasting.api.casting.SpellList
import at.petrak.hexcasting.api.casting.eval.CastResult
import at.petrak.hexcasting.api.casting.eval.ResolvedPatternType
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM
import at.petrak.hexcasting.api.casting.eval.vm.ContinuationFrame
import at.petrak.hexcasting.api.casting.eval.vm.FrameEvaluate
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.server.level.ServerLevel
import java.util.Optional

data class ApepFrame(
    val data: SpellList,
    val code: SpellList,
    var acc: Iota,
    val baseStack: List<Iota>?,
    override val type: ContinuationFrame.Type<*> = TYPE
) : ContinuationFrame {

    override fun evaluate(continuation: SpellContinuation, level: ServerLevel, vm: CastingVM): CastResult {
        val stack = if (baseStack == null) {
            vm.image.stack.toList()
        } else {
            acc = vm.image.stack.lastOrNull() ?: NullIota()
            baseStack
        }

        val newStack = stack.toMutableList()
        val (newImage, newCont) = if (data.nonEmpty) {
            newStack.add(acc)
            newStack.add(data.car)
            Pair(vm.image.withUsedOp(), continuation
                .pushFrame(ApepFrame(data.cdr, code, acc, stack))
                .pushFrame(FrameEvaluate(code, true)))
        } else {
            newStack.add(acc)
            Pair(vm.image, continuation)
        }

        return CastResult(
            ListIota(code),
            newCont,
            newImage.withResetEscape().copy(stack = newStack),
            listOf(),
            ResolvedPatternType.EVALUATED,
            HexEvalSounds.THOTH,
        )
    }

    override fun breakDownwards(stack: List<Iota>): Pair<Boolean, List<Iota>> {
        val newStack = baseStack?.toMutableList() ?: mutableListOf()
        acc = stack.lastOrNull() ?: NullIota()
        newStack.add(acc)
        return true to newStack
    }

    override fun size() = data.size() + code.size() + acc.size() + (baseStack?.size ?: 0)

    companion object {
        @JvmField
        val TYPE: ContinuationFrame.Type<ApepFrame> = object : ContinuationFrame.Type<ApepFrame> {

            private val baseListCodec: Codec<List<Iota>> = Codec.list(IotaType.TYPED_CODEC)

            val CODEC: MapCodec<ApepFrame> = RecordCodecBuilder.mapCodec<ApepFrame> { instance ->
                instance.group(
                    SpellList.CODEC.fieldOf("data").forGetter(ApepFrame::data),
                    SpellList.CODEC.fieldOf("code").forGetter(ApepFrame::code),
                    IotaType.TYPED_CODEC.fieldOf("acc").forGetter(ApepFrame::acc),
                    baseListCodec.optionalFieldOf("base").forGetter { frame: ApepFrame ->
                        Optional.ofNullable<List<Iota>>(frame.baseStack)
                    }
                ).apply(instance) { data: SpellList, code: SpellList, acc: Iota, base: Optional<List<Iota>> ->
                    ApepFrame(data, code, acc, base.orElse(null))
                }
            }

            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ApepFrame> = StreamCodec.composite(
                SpellList.STREAM_CODEC, ApepFrame::data,
                SpellList.STREAM_CODEC, ApepFrame::code,
                IotaType.TYPED_STREAM_CODEC, ApepFrame::acc,
                ByteBufCodecs.optional(
                    ByteBufCodecs.collection({ size -> ArrayList<Iota>(size) }, IotaType.TYPED_STREAM_CODEC)
                ),
                { frame: ApepFrame -> Optional.ofNullable<List<Iota>>(frame.baseStack) },
                { data: SpellList, code: SpellList, acc: Iota, base: Optional<List<Iota>> ->
                    ApepFrame(data, code, acc, base.orElse(null))
                }
            )

            override fun codec(): MapCodec<ApepFrame> = CODEC
            override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, ApepFrame> = STREAM_CODEC
        }
    }
}