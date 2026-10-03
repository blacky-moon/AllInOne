package blacky_moon.miyu.overevaluate.actions.jumble

import at.petrak.hexcasting.api.casting.eval.CastResult
import at.petrak.hexcasting.api.casting.eval.ResolvedPatternType
import at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect.DoMishap
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.ChatFormatting
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.server.level.ServerLevel

class JumbleIota(val jumble: Jumble) : Iota({ TYPE }) {

    override fun isTruthy() = true

    override fun toleratesOther(that: Iota) = (typesMatch(this, that) && that is JumbleIota) && this.jumble == that.jumble

    override fun hashCode(): Int {
        return jumble.hashCode()
    }

    override fun executable() = true

    override fun execute(vm: CastingVM, world: ServerLevel, continuation: SpellContinuation): CastResult {
        val stack = vm.image.stack.toMutableList()
        if (stack.size < jumble.args)
            return CastResult(
                this,
                continuation,
                null,
                listOf(DoMishap(MishapNotEnoughArgs(jumble.args, stack.size), Mishap.Context(null, null))),
                ResolvedPatternType.INVALID,
                HexEvalSounds.MISHAP
            )

        val reference = stack.takeLast(jumble.args)
        for (i in 0 until jumble.args)
            stack.removeAt(stack.lastIndex)
        stack.addAll(jumble.jumble.map { reference[it] })

        return CastResult(
            this,
            continuation,
            vm.image.copy(stack = stack),
            listOf(),
            ResolvedPatternType.EVALUATED,
            HexEvalSounds.NORMAL_EXECUTE
        )
    }

    override fun display(): Component {
        return Jumble.display(jumble)
    }

    companion object {
        val TYPE: IotaType<JumbleIota> = object : IotaType<JumbleIota>() {
            override fun color() = 0xff_4b0afc.toInt()

            override fun codec(): MapCodec<JumbleIota> = Jumble.CODEC.xmap(::JumbleIota, JumbleIota::jumble)

            override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, JumbleIota> =
                Jumble.STREAM_CODEC.map(::JumbleIota, JumbleIota::jumble)
        }
    }
}

data class Jumble(val args: Int, val jumble: List<Int>) {
    companion object {
        val CODEC: MapCodec<Jumble> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                Codec.INT.fieldOf("args").forGetter(Jumble::args),
                Codec.INT.listOf().fieldOf("jumble").forGetter(Jumble::jumble)
            ).apply(instance, ::Jumble)
        }

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, Jumble> = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, Jumble::args,
            ByteBufCodecs.collection({ size -> ArrayList(size) }, ByteBufCodecs.VAR_INT), Jumble::jumble,
            ::Jumble
        )

        fun display(jumble: Jumble): Component {
            return Component.literal("${jumble.args} [${jumble.jumble.joinToString(" ")}]").withStyle(ChatFormatting.BLUE)
        }
    }
}

fun List<Iota>.getJumble(idx: Int, argc: Int = 0): Jumble {
    val x = this.getOrElse(idx) { throw MishapNotEnoughArgs(idx + 1, this.size) }
    if (x is JumbleIota)
        return x.jumble


    throw MishapInvalidIota.ofType(x, if (argc == 0) idx else argc - (idx + 1), "jumble")
}