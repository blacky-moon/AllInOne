package miyucomics.hexical.features.pigments

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs
import at.petrak.hexcasting.api.pigment.FrozenPigment
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import miyucomics.hexical.misc.TextUtilities.getPigmentedText
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps

class PigmentIota(val pigment: FrozenPigment) : Iota({ PigmentIota.TYPE }) {
    override fun isTruthy() = true

    override fun toleratesOther(that: Iota): Boolean {
        return typesMatch(this, that) && this.pigment == (that as PigmentIota).pigment
    }

    override fun display(): Component {
        return getPigmentedText(pigment.item.descriptionId, pigment)
    }

    override fun hashCode(): Int {
        return pigment.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PigmentIota) return false
        return pigment == other.pigment
    }

    companion object {
        val TYPE: IotaType<PigmentIota> = object : IotaType<PigmentIota>() {
            override fun color() = 0xff_c466e3.toInt()

            override fun codec(): MapCodec<PigmentIota> {
                return RecordCodecBuilder.mapCodec { instance ->
                    instance.group(
                        FrozenPigment.CODEC.fieldOf("pigment").forGetter { it.pigment }
                    ).apply(instance, ::PigmentIota)
                }
            }

            override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, PigmentIota> {
                return StreamCodec.of(
                    { buf, iota -> FrozenPigment.STREAM_CODEC.encode(buf, iota.pigment) },
                    { buf -> PigmentIota(FrozenPigment.STREAM_CODEC.decode(buf)) }
                )
            }

            override fun validate(iota: PigmentIota, level: ServerLevel): Boolean {
                return true
            }
        }
    }
}

fun List<Iota>.getPigment(idx: Int, argc: Int = 0): FrozenPigment {
    val x = this.getOrElse(idx) { throw MishapNotEnoughArgs(idx + 1, this.size) }
    if (x is PigmentIota)
        return x.pigment
    throw MishapInvalidIota.ofType(x, if (argc == 0) idx else argc - (idx + 1), "pigment")
}