package miyucomics.hexical.features.lesser_sentinels

import miyucomics.hexical.HexicalMain
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.phys.Vec3

data class LesserSentinelPayload(val positions: List<Vec3>) : CustomPacketPayload {
    override fun type() = TYPE

    companion object {
        @JvmField
        val TYPE = CustomPacketPayload.Type<LesserSentinelPayload>(HexicalMain.id("lesser_sentinels"))

        @JvmField
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, LesserSentinelPayload> = StreamCodec.of(
            { buf, payload ->
                buf.writeInt(payload.positions.size)
                payload.positions.forEach { vec ->
                    buf.writeDouble(vec.x)
                    buf.writeDouble(vec.y)
                    buf.writeDouble(vec.z)
                }
            },
            { buf ->
                val count = buf.readInt()
                val list = mutableListOf<Vec3>()
                repeat(count) {
                    list.add(Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()))
                }
                LesserSentinelPayload(list)
            }
        )
    }
}