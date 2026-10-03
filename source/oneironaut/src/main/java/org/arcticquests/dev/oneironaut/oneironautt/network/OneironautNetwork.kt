package org.arcticquests.dev.oneironaut.oneironautt.network

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut

object OneironautNetwork {
    private const val PROTOCOL = "1"

    @JvmStatic
    fun register(event: RegisterPayloadHandlersEvent) {
        val registrar = event.registrar(Oneironaut.MODID).versioned(PROTOCOL)

        registrar.playToClient(
            HoverliftAntiDesyncPacket.TYPE,
            HoverliftAntiDesyncPacket.STREAM_CODEC,
            HoverliftAntiDesyncPacket::handle
        )

        registrar.playToClient(
            ParticleBurstPacket.TYPE,
            ParticleBurstPacket.STREAM_CODEC,
            ParticleBurstPacket::handle
        )

        registrar.playToClient(
            FireballUpdatePacket.TYPE,
            FireballUpdatePacket.STREAM_CODEC,
            FireballUpdatePacket::handle
        )

        registrar.playToClient(
            ItemUpdatePacket.TYPE,
            ItemUpdatePacket.STREAM_CODEC,
            ItemUpdatePacket::handle
        )

        registrar.playToClient(
            SpoopyScreamPacket.TYPE,
            SpoopyScreamPacket.STREAM_CODEC,
            SpoopyScreamPacket::handle
        )

        registrar.playToClient(
            UnBrainsweepPacket.TYPE,
            UnBrainsweepPacket.STREAM_CODEC,
            UnBrainsweepPacket::handle
        )
    }
}