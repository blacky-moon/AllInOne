package miyucomics.hexcellular

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.ChatFormatting
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec

class PropertyIota(val name: String, val readonly: Boolean = false) : Iota(TYPE_SUPPLIER) {
    override fun isTruthy() = true
    override fun toleratesOther(that: Iota) = that is PropertyIota && this.name == that.name && this.readonly == that.readonly
    override fun hashCode(): Int = java.util.Objects.hash(name, readonly)

    override fun display(): Component =
        Component.literal(name)
            .withStyle(if (readonly) ChatFormatting.BOLD else ChatFormatting.GREEN)
            .withStyle(ChatFormatting.GREEN)

    companion object {
        @JvmField
        val TYPE: IotaType<PropertyIota> = object : IotaType<PropertyIota>() {
            val CODEC: MapCodec<PropertyIota> = RecordCodecBuilder.mapCodec { instance ->
                instance.group(
                    Codec.STRING.fieldOf("name").forGetter(PropertyIota::name),
                    Codec.BOOL.fieldOf("readonly").forGetter(PropertyIota::readonly)
                ).apply(instance, ::PropertyIota)
            }

            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, PropertyIota> = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, PropertyIota::name,
                ByteBufCodecs.BOOL, PropertyIota::readonly,
                ::PropertyIota
            )

            override fun codec(): MapCodec<PropertyIota> = CODEC
            override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, PropertyIota> = STREAM_CODEC
            override fun color() = -0x591c5f
        }

        private val TYPE_SUPPLIER = { TYPE }
    }
}

fun List<Iota>.getProperty(idx: Int, argc: Int): String {
    val x = this.getOrElse(idx) { throw MishapNotEnoughArgs(idx + 1, this.size) }
    if (x is PropertyIota)
        return x.name
    throw MishapInvalidIota.ofType(x, if (argc == 0) idx else argc - (idx + 1), "property")
}