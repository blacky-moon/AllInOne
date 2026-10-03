package miyucomics.hexical.features.sparkle

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleType
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import org.joml.Vector3f

class SparkleParticleEffect(
    val color: Vector3f,
    val lifespan: Int
) : ParticleOptions {

    override fun getType(): ParticleType<SparkleParticleEffect> =
        miyucomics.hexical.inits.HexicalParticles.SPARKLE_PARTICLE.get()

    companion object {
        @JvmField
        val MAP_CODEC: MapCodec<SparkleParticleEffect> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                Codec.FLOAT.fieldOf("r").forGetter { it.color.x },
                Codec.FLOAT.fieldOf("g").forGetter { it.color.y },
                Codec.FLOAT.fieldOf("b").forGetter { it.color.z },
                Codec.INT.fieldOf("lifespan").forGetter { it.lifespan }
            ).apply(instance) { r, g, b, lifespan ->
                SparkleParticleEffect(Vector3f(r, g, b), lifespan)
            }
        }

        @JvmField
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, SparkleParticleEffect> =
            StreamCodec.composite(
                ByteBufCodecs.FLOAT, { it.color.x },
                ByteBufCodecs.FLOAT, { it.color.y },
                ByteBufCodecs.FLOAT, { it.color.z },
                ByteBufCodecs.VAR_INT, { it.lifespan },
                { r, g, b, lifespan -> SparkleParticleEffect(Vector3f(r, g, b), lifespan) }
            )
    }
}