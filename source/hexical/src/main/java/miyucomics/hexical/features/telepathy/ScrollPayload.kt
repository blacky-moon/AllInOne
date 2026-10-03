package miyucomics.hexical.features.telepathy

import miyucomics.hexical.HexicalMain
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload

data class ScrollPayload(val scrollDelta: Int) : CustomPacketPayload {
    override fun type() = TYPE
    companion object {
        val TYPE: CustomPacketPayload.Type<ScrollPayload> = CustomPacketPayload.Type(HexicalMain.id("scroll"))
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ScrollPayload> = StreamCodec.of(
            { buf, payload -> buf.writeInt(payload.scrollDelta) },
            { buf -> ScrollPayload(buf.readInt()) }
        )
    }
}