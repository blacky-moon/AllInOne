package org.arcticquests.dev.oneironaut.oneironautt.network

import net.minecraft.client.Minecraft
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.network.handling.IPayloadContext
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut
import java.util.ConcurrentModificationException

class FireballUpdatePacket(val targetVelocity: Vec3, val entityId: Int) : CustomPacketPayload {

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE

    companion object {
        val TYPE: CustomPacketPayload.Type<FireballUpdatePacket> =
            CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath(Oneironaut.MODID, "fireballupdate"))

        private val VEC3_CODEC: StreamCodec<RegistryFriendlyByteBuf, Vec3> = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, { v: Vec3 -> v.x },
            ByteBufCodecs.DOUBLE, { v: Vec3 -> v.y },
            ByteBufCodecs.DOUBLE, { v: Vec3 -> v.z },
            { x: Double, y: Double, z: Double -> Vec3(x, y, z) }
        )

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, FireballUpdatePacket> = StreamCodec.composite(
            VEC3_CODEC, FireballUpdatePacket::targetVelocity,
            ByteBufCodecs.VAR_INT, FireballUpdatePacket::entityId,
            ::FireballUpdatePacket
        )

        @JvmStatic
        fun handle(packet: FireballUpdatePacket, context: IPayloadContext) {
            context.enqueueWork {
                val level = Minecraft.getInstance().level ?: return@enqueueWork
                val entityToUpdate = level.getEntity(packet.entityId) as? AbstractHurtingProjectile ?: return@enqueueWork
                try {
                    entityToUpdate.deltaMovement = packet.targetVelocity
                    entityToUpdate.accelerationPower = packet.targetVelocity.length()
                } catch (e: ConcurrentModificationException) {
                    Oneironaut.LOGGER.debug("oopsie, concurrent modification!\n$e")
                }
            }
        }
    }
}