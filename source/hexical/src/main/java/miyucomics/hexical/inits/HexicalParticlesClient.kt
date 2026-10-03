package miyucomics.hexical.inits

import miyucomics.hexical.features.confetti.ConfettiParticle
import miyucomics.hexical.features.sparkle.SparkleParticle
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent

object HexicalParticlesClient {
    fun registerProviders(event: RegisterParticleProvidersEvent) {
        // В 1.21.1 метод называется registerSpriteSet или registerSpecial в зависимости от типа частицы
        // Для простых частиц с текстурой:
        event.registerSpriteSet(HexicalParticles.CONFETTI_PARTICLE.get(), ConfettiParticle::Factory)
        event.registerSpriteSet(HexicalParticles.SPARKLE_PARTICLE.get(), SparkleParticle::Factory)

        // Если ваши частицы используют special renderer (без спрайтов):
        // event.registerSpecial(HexicalParticles.SPARKLE_PARTICLE.get(), SparkleParticle::Factory)
    }
}