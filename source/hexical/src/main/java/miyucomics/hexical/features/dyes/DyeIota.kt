package miyucomics.hexical.features.dyes

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.DyeColor

class DyeIota(val dye: DyeOption) : Iota({ TYPE }) {
    override fun isTruthy() = true

    override fun toleratesOther(that: Iota) = typesMatch(this, that) && that is DyeIota && this.dye == that.dye

    override fun display(): Component = dye.coloredText

    override fun hashCode() = dye.hashCode()

    companion object {
        @JvmField
        val TYPE: IotaType<DyeIota> = object : IotaType<DyeIota>() {
            override fun color() = -1

            override fun codec(): MapCodec<DyeIota> = RecordCodecBuilder.mapCodec { instance ->
                instance.group(
                    com.mojang.serialization.Codec.INT.fieldOf("dye").forGetter { it.dye.ordinal }
                ).apply(instance) { ordinal -> DyeIota(DyeOption.entries[ordinal]) }
            }

            // ИСПРАВЛЕНО: Используем StreamCodec.of для явного указания RegistryFriendlyByteBuf
            override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, DyeIota> =
                StreamCodec.of(
                    { buf, value -> buf.writeVarInt(value.dye.ordinal) },
                    { buf -> DyeIota(DyeOption.entries[buf.readVarInt()]) }
                )
        }
    }
}

fun List<Iota>.getDye(idx: Int, argc: Int = 0): DyeOption {
    val x = this.getOrElse(idx) { throw MishapNotEnoughArgs(idx + 1, this.size) }
    if (x is DyeIota) return x.dye
    throw MishapInvalidIota.ofType(x, if (argc == 0) idx else argc - (idx + 1), "dye")
}

fun List<Iota>.getColoredDye(idx: Int, argc: Int = 0): DyeColor {
    val dye = getDye(idx, argc)
    if (dye != DyeOption.UNCOLORED) return dye.dyeColor!!
    throw MishapInvalidIota.of(this[idx], if (argc == 0) idx else argc - (idx + 1), "colored_dye")
}