package org.arcticquests.dev.oneironaut.oneironautt.network

import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.common.lib.HexSounds
import at.petrak.hexcasting.common.particles.ConjureParticleOptions
import net.minecraft.client.Minecraft
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundSource
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.network.handling.IPayloadContext
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut
import ram.talia.hexal.api.nextColour

class ParticleBurstPacket(
    val origin: Vec3,
    val direction: Vec3,
    val posRandom: Double,
    val speedRandom: Double,
    val color: FrozenPigment,
    val quantity: Int,
    val isActuallySound: Boolean
) : CustomPacketPayload {

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE

    companion object {
        val TYPE: CustomPacketPayload.Type<ParticleBurstPacket> =
            CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath(Oneironaut.MODID, "particleburst"))

        private val VEC3_CODEC: StreamCodec<RegistryFriendlyByteBuf, Vec3> = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, { v: Vec3 -> v.x },
            ByteBufCodecs.DOUBLE, { v: Vec3 -> v.y },
            ByteBufCodecs.DOUBLE, { v: Vec3 -> v.z },
            { x: Double, y: Double, z: Double -> Vec3(x, y, z) }
        )

        // StreamCodec.composite поддерживает максимум 6 полей, поэтому сворачиваем
        // posRandom+speedRandom в один Pair<Double, Double> через отдельный под-кодек.
        private val RANDOM_PAIR_CODEC: StreamCodec<RegistryFriendlyByteBuf, Pair<Double, Double>> = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, { p: Pair<Double, Double> -> p.first },
            ByteBufCodecs.DOUBLE, { p: Pair<Double, Double> -> p.second },
            { a: Double, b: Double -> a to b }
        )

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ParticleBurstPacket> = StreamCodec.composite(
            VEC3_CODEC, ParticleBurstPacket::origin,
            VEC3_CODEC, ParticleBurstPacket::direction,
            RANDOM_PAIR_CODEC, { p: ParticleBurstPacket -> p.posRandom to p.speedRandom },
            FrozenPigment.STREAM_CODEC, ParticleBurstPacket::color,
            ByteBufCodecs.VAR_INT, ParticleBurstPacket::quantity,
            ByteBufCodecs.BOOL, ParticleBurstPacket::isActuallySound
        ) { origin, direction, randomPair, color, quantity, isActuallySound ->
            ParticleBurstPacket(origin, direction, randomPair.first, randomPair.second, color, quantity, isActuallySound)
        }

        @JvmStatic
        fun handle(packet: ParticleBurstPacket, context: IPayloadContext) {
            context.enqueueWork {
                val level = Minecraft.getInstance().level ?: return@enqueueWork
                val rand = level.random
                val origin = packet.origin
                val direction = packet.direction
                val posRandom = packet.posRandom
                val speedRandom = packet.speedRandom
                val color = packet.color.nextColour(rand)
                val quantity = packet.quantity.coerceAtLeast(1)

                if (!packet.isActuallySound) {
                    for (i in 1..quantity) {
                        val adjustedPos = Vec3(
                            origin.x + (rand.nextGaussian() * posRandom),
                            origin.y + (rand.nextGaussian() * posRandom),
                            origin.z + (rand.nextGaussian() * posRandom)
                        )
                        val adjustedSpeed = Vec3(
                            direction.x + (rand.nextGaussian() * speedRandom),
                            direction.y + (rand.nextGaussian() * speedRandom),
                            direction.z + (rand.nextGaussian() * speedRandom)
                        )
                        level.addParticle(
                            ConjureParticleOptions(color),
                            adjustedPos.x, adjustedPos.y, adjustedPos.z,
                            adjustedSpeed.x, adjustedSpeed.y, adjustedSpeed.z
                        )
                    }
                } else {
                    level.playSound(
                        null, origin.x, origin.y, origin.z, HexSounds.CASTING_AMBIANCE,
                        SoundSource.MASTER, 1f, 1f
                    )
                }
            }
        }
    }
}