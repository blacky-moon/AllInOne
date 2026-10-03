package org.arcticquests.dev.oneironaut.oneironautt.network

import net.minecraft.client.Minecraft
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.neoforged.neoforge.network.handling.IPayloadContext
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut

class SpoopyScreamPacket(val sound: SoundEvent, val pitch: Float) : CustomPacketPayload {

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE

    companion object {
        val TYPE: CustomPacketPayload.Type<SpoopyScreamPacket> =
            CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath(Oneironaut.MODID, "scream"))

        private val SOUND_EVENT_CODEC: StreamCodec<FriendlyByteBuf, SoundEvent> = StreamCodec.of(
            { buf, sound -> buf.writeResourceLocation(sound.location) },
            { buf ->
                BuiltInRegistries.SOUND_EVENT.get(buf.readResourceLocation())
                    ?: error("Unknown sound event received")
            }
        )

        val STREAM_CODEC: StreamCodec<FriendlyByteBuf, SpoopyScreamPacket> = StreamCodec.composite(
            SOUND_EVENT_CODEC, SpoopyScreamPacket::sound,
            ByteBufCodecs.FLOAT, SpoopyScreamPacket::pitch,
            ::SpoopyScreamPacket
        )

        @JvmStatic
        fun handle(packet: SpoopyScreamPacket, context: IPayloadContext) {
            context.enqueueWork {
                val world = Minecraft.getInstance().level ?: return@enqueueWork
                val you = Minecraft.getInstance().player ?: return@enqueueWork
                val pos = you.eyePosition.subtract(you.lookAngle)
                world.playLocalSound(pos.x, pos.y, pos.z, packet.sound, SoundSource.HOSTILE, 3f, packet.pitch, true)
            }
        }
    }
}