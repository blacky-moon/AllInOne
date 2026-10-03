package org.arcticquests.dev.oneironaut.oneironautt.network

import net.minecraft.client.Minecraft
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.Mob
import net.neoforged.neoforge.network.handling.IPayloadContext
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut
import org.arcticquests.dev.oneironaut.oneironautt.unbrainsweep

class UnBrainsweepPacket(val patientID: Int) : CustomPacketPayload {

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE

    companion object {
        val TYPE: CustomPacketPayload.Type<UnBrainsweepPacket> =
            CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath(Oneironaut.MODID, "unbrainsweep"))

        val STREAM_CODEC: StreamCodec<io.netty.buffer.ByteBuf, UnBrainsweepPacket> = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, UnBrainsweepPacket::patientID,
            ::UnBrainsweepPacket
        )

        @JvmStatic
        fun handle(packet: UnBrainsweepPacket, context: IPayloadContext) {
            context.enqueueWork {
                val world = Minecraft.getInstance().level ?: return@enqueueWork
                val patient = world.getEntity(packet.patientID) as? Mob ?: return@enqueueWork
                patient.unbrainsweep()
            }
        }
    }
}