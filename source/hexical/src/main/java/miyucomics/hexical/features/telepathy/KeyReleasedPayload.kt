package miyucomics.hexical.features.telepathy

import miyucomics.hexical.HexicalMain
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload

data class KeyReleasedPayload(val keyTranslationKey: String) : CustomPacketPayload {
    override fun type() = TYPE
    companion object {
        val TYPE: CustomPacketPayload.Type<KeyReleasedPayload> = CustomPacketPayload.Type(HexicalMain.id("release_key"))
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, KeyReleasedPayload> = StreamCodec.of(
            { buf, payload -> buf.writeUtf(payload.keyTranslationKey) },
            { buf -> KeyReleasedPayload(buf.readUtf()) }
        )
    }
}