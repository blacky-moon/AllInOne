package miyucomics.hexical.inits

import miyucomics.hexical.HexicalMain
import miyucomics.hexical.features.sparkle.SparkleParticleEffect
import net.minecraft.core.particles.ParticleType
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.core.registries.Registries
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

object HexicalParticles {
    val PARTICLE_TYPES: DeferredRegister<ParticleType<*>> =
        DeferredRegister.create(Registries.PARTICLE_TYPE, HexicalMain.MOD_ID)

    val CONFETTI_PARTICLE: DeferredHolder<ParticleType<*>, SimpleParticleType> =
        PARTICLE_TYPES.register("confetti", Supplier {
            SimpleParticleType(true)
        })

    val SPARKLE_PARTICLE: DeferredHolder<ParticleType<*>, ParticleType<SparkleParticleEffect>> =
        PARTICLE_TYPES.register("sparkle", Supplier {
            object : ParticleType<SparkleParticleEffect>(true) {
                override fun codec() = SparkleParticleEffect.MAP_CODEC
                override fun streamCodec() = SparkleParticleEffect.STREAM_CODEC
            }
        })

    fun init(modEventBus: IEventBus) {
        PARTICLE_TYPES.register(modEventBus)
    }
}