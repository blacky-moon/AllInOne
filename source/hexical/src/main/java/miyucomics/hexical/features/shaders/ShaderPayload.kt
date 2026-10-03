package miyucomics.hexical.features.shaders

import miyucomics.hexical.HexicalMain
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload

data class ShaderPayload(val shaderId: String) : CustomPacketPayload {
    override fun type() = TYPE

    companion object {
        val TYPE: CustomPacketPayload.Type<ShaderPayload> = CustomPacketPayload.Type(HexicalMain.id("shader"))
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ShaderPayload> = StreamCodec.of(
            { buf, payload -> buf.writeUtf(payload.shaderId) },
            { buf -> ShaderPayload(buf.readUtf()) }
        )
    }
}