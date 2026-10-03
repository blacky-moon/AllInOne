package miyucomics.hexical.features.telepathy

import miyucomics.hexical.HexicalMain
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload

data class KeyPressedPayload(val keyTranslationKey: String) : CustomPacketPayload {
    override fun type() = TYPE
    companion object {
        val TYPE: CustomPacketPayload.Type<KeyPressedPayload> = CustomPacketPayload.Type(HexicalMain.id("press_key"))
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, KeyPressedPayload> = StreamCodec.of(
            { buf, payload -> buf.writeUtf(payload.keyTranslationKey) },
            { buf -> KeyPressedPayload(buf.readUtf()) }
        )
    }
}