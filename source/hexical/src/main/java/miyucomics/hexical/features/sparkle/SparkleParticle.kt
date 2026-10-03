package miyucomics.hexical.features.sparkle

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.ParticleRenderType
import net.minecraft.client.particle.SpriteSet
import net.minecraft.client.particle.TextureSheetParticle

class SparkleParticle(
    world: ClientLevel,
    x: Double, y: Double, z: Double,
    xd: Double, yd: Double, zd: Double,
    private val spriteSet: SpriteSet
) : TextureSheetParticle(world, x, y, z) {
    init {
        lifetime = 20
        this.xd = 0.0
        this.yd = 0.0
        this.zd = 0.0
        setSpriteFromAge(spriteSet)
        scale(2.5f)
    }

    override fun tick() {
        super.tick()
        setSpriteFromAge(spriteSet)
    }

    override fun getLightColor(tint: Float): Int {
        val i = super.getLightColor(tint)
        val k = i shr 16 and 0xFF
        return 240 or (k shl 16)
    }

    override fun getRenderType(): ParticleRenderType = ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT

    class Factory(private val spriteSet: SpriteSet) : ParticleProvider<SparkleParticleEffect> {
        override fun createParticle(
            effect: SparkleParticleEffect,
            world: ClientLevel,
            x: Double, y: Double, z: Double,
            xd: Double, yd: Double, zd: Double
        ): Particle {
            val particle = SparkleParticle(world, x, y, z, xd, yd, zd, spriteSet)
            particle.setColor(effect.color.x, effect.color.y, effect.color.z)
            particle.lifetime = effect.lifespan
            return particle
        }
    }
}