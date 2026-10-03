package org.arcticquests.dev.oneironaut.oneironautt.network

import net.minecraft.client.Minecraft
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.effect.MobEffects
import net.neoforged.neoforge.network.handling.IPayloadContext
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut

class HoverliftAntiDesyncPacket : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<HoverliftAntiDesyncPacket> = TYPE

    companion object {
        val ID: ResourceLocation = ResourceLocation.fromNamespaceAndPath(Oneironaut.MODID, "hoverliftantidesync")
        val TYPE: CustomPacketPayload.Type<HoverliftAntiDesyncPacket> = CustomPacketPayload.Type(ID)
        @JvmField
        val INSTANCE: HoverliftAntiDesyncPacket = HoverliftAntiDesyncPacket()

        val STREAM_CODEC: StreamCodec<io.netty.buffer.ByteBuf, HoverliftAntiDesyncPacket> =
            StreamCodec.unit(INSTANCE)

        @JvmStatic
        fun handle(packet: HoverliftAntiDesyncPacket, context: IPayloadContext) {
            context.enqueueWork {
                val player = Minecraft.getInstance().player
                if (player != null && player.hasEffect(MobEffects.SLOW_FALLING)) {
                    player.removeEffect(MobEffects.SLOW_FALLING)
                }
            }
        }
    }
}