package miyucomics.hexical.features.evocation

import miyucomics.hexical.HexicalMain
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import java.util.UUID

data class StartEvokePayload(val playerUUID: UUID) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<StartEvokePayload> = TYPE

    companion object {
        @JvmField
        val TYPE = CustomPacketPayload.Type<StartEvokePayload>(HexicalMain.id("start_evoke"))
        @JvmField
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, StartEvokePayload> = StreamCodec.ofMember(
            { payload, buf -> buf.writeUUID(payload.playerUUID) },
            { buf -> StartEvokePayload(buf.readUUID()) }
        )
    }
}

data class EndEvokePayload(val playerUUID: UUID) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<EndEvokePayload> = TYPE

    companion object {
        @JvmField
        val TYPE = CustomPacketPayload.Type<EndEvokePayload>(HexicalMain.id("end_evoke"))
        @JvmField
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, EndEvokePayload> = StreamCodec.ofMember(
            { payload, buf -> buf.writeUUID(payload.playerUUID) },
            { buf -> EndEvokePayload(buf.readUUID()) }
        )
    }
}
