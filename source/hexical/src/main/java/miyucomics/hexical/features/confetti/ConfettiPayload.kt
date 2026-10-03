package miyucomics.hexical.features.confetti

import miyucomics.hexical.HexicalMain
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.phys.Vec3

data class ConfettiPayload(
    val seed: Long,
    val pos: Vec3,
    val dir: Vec3,
    val speed: Double
) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<ConfettiPayload> = TYPE

    companion object {
        @JvmField
        val TYPE: CustomPacketPayload.Type<ConfettiPayload> =
            CustomPacketPayload.Type(HexicalMain.id("confetti"))

        @JvmField
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ConfettiPayload> =
            StreamCodec.composite(
                ByteBufCodecs.VAR_LONG, ConfettiPayload::seed,
                ByteBufCodecs.VECTOR3F.map(
                    { v -> Vec3(v.x.toDouble(), v.y.toDouble(), v.z.toDouble()) },
                    { v -> org.joml.Vector3f(v.x.toFloat(), v.y.toFloat(), v.z.toFloat()) }
                ), ConfettiPayload::pos,
                ByteBufCodecs.VECTOR3F.map(
                    { v -> Vec3(v.x.toDouble(), v.y.toDouble(), v.z.toDouble()) },
                    { v -> org.joml.Vector3f(v.x.toFloat(), v.y.toFloat(), v.z.toFloat()) }
                ), ConfettiPayload::dir,
                ByteBufCodecs.DOUBLE, ConfettiPayload::speed,
                ::ConfettiPayload
            )
    }
}