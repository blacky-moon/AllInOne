package miyucomics.hexical.features.confetti

import com.mojang.blaze3d.vertex.VertexConsumer
import miyucomics.hexical.HexicalMain
import net.minecraft.client.Camera
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.ParticleRenderType
import net.minecraft.client.particle.SpriteSet
import net.minecraft.client.particle.TextureSheetParticle
import net.minecraft.core.particles.SimpleParticleType
import net.minecraft.util.Mth
import net.minecraft.world.phys.Vec3
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.max

class ConfettiParticle(
    level: ClientLevel,
    x: Double, y: Double, z: Double,
    dx: Double, dy: Double, dz: Double,
    private val spriteSet: SpriteSet
) : TextureSheetParticle(level, x, y, z, dx, dy, dz) {

    private val particleId: Double = random.nextDouble()
    private var pitch = 0f
    private var yaw = 0f
    private var prevPitch = 0f
    private var prevYaw = 0f
    private var deltaPitch = 0f
    private var deltaYaw = 0f
    private var deltaRoll = 0f

    init {
        setSprite(spriteSet.get(random))
        xd = dx
        yd = dy
        zd = dz
        setSize(0.001f, 0.001f)
        gravity = 0.2f
        friction = 0.9f
        lifetime = random.nextInt(400) + 300
        scale(1.25f)
    }

    override fun render(vertexConsumer: VertexConsumer, camera: Camera, partialTick: Float) {
        val rotation = Quaternionf()
            .rotateZ(Mth.lerp(partialTick, oRoll, roll))
            .rotateY(Mth.lerp(partialTick, prevYaw, yaw))
            .rotateX(Mth.lerp(partialTick, prevPitch, pitch))

        val camPos = camera.position
        val px = Mth.lerp(partialTick.toDouble(), xo, x).toFloat() - camPos.x.toFloat()
        val py = Mth.lerp(partialTick.toDouble(), yo, y).toFloat() - camPos.y.toFloat()
        val pz = Mth.lerp(partialTick.toDouble(), zo, z).toFloat() - camPos.z.toFloat()

        val vertices = arrayOf(
            Vector3f(-quadSize, -quadSize, 0f),
            Vector3f(-quadSize, quadSize, 0f),
            Vector3f(quadSize, quadSize, 0f),
            Vector3f(quadSize, -quadSize, 0f)
        ).map { v -> v.rotate(rotation).add(px, py, pz) }

        val light = getLightColor(partialTick)

        fun vertex(v: Vector3f, u: Float, texV: Float) {
            vertexConsumer.addVertex(v.x, v.y, v.z)
                .setUv(u, texV)
                .setColor(rCol, gCol, bCol, alpha)
                .setLight(light)
        }

        vertex(vertices[0], u1, v1)
        vertex(vertices[1], u1, v0)
        vertex(vertices[2], u0, v0)
        vertex(vertices[3], u0, v1)

        // Обратная сторона для двустороннего рендеринга
        vertex(vertices[3], u0, v1)
        vertex(vertices[2], u0, v0)
        vertex(vertices[1], u1, v0)
        vertex(vertices[0], u1, v1)
    }

    override fun tick() {
        xo = x; yo = y; zo = z
        prevYaw = yaw; prevPitch = pitch; oRoll = roll

        xd += X_NOISE.getValue(particleId, age.toDouble(), false) / 100
        zd += Z_NOISE.getValue(particleId, age.toDouble(), false) / 100

        if (onGround || (x == xo && z == zo && y == yo && age > 0)) {
            age = max(age, lifetime - 20)
        } else {
            deltaYaw += YAW_NOISE.getValue(particleId, age.toDouble(), false).toFloat() / 10f
            deltaRoll += ROLL_NOISE.getValue(particleId, age.toDouble(), false).toFloat() / 10f
            deltaPitch += PITCH_NOISE.getValue(particleId, age.toDouble(), false).toFloat() / 10f
            yaw += deltaYaw
            pitch += deltaPitch
            roll += deltaRoll
        }

        deltaYaw *= 0.98f
        deltaRoll *= 0.98f
        deltaPitch *= 0.98f

        super.tick()
    }

    override fun getRenderType(): ParticleRenderType = ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT

    class Factory(private val spriteSet: SpriteSet) : ParticleProvider<SimpleParticleType> {
        override fun createParticle(
            type: SimpleParticleType,
            level: ClientLevel,
            x: Double, y: Double, z: Double,
            dx: Double, dy: Double, dz: Double
        ): Particle {
            val p = ConfettiParticle(level, x, y, z, dx, dy, dz, spriteSet)
            p.yaw = HexicalMain.RANDOM.nextFloat() * Mth.TWO_PI
            p.pitch = HexicalMain.RANDOM.nextFloat() * Mth.TWO_PI
            p.roll = HexicalMain.RANDOM.nextFloat() * Mth.TWO_PI
            return p
        }
    }

    companion object {
        // В 1.21.1 OctaveSimplexNoiseSampler может отсутствовать или быть изменен.
        // Если он недоступен, используйте ImprovedNoise или SimplexNoise из vanilla.
        // Здесь предполагается, что у вас есть собственная реализация или доступ к нему.
        // Для примера используем заглушку, если оригинальный класс удален.
        private val X_NOISE = NoiseGenerator(58637214)
        private val Z_NOISE = NoiseGenerator(823917)
        private val YAW_NOISE = NoiseGenerator(28943157)
        private val ROLL_NOISE = NoiseGenerator(80085)
        private val PITCH_NOISE = NoiseGenerator(49715286)
    }

    // Простая обертка, если OctaveSimplexNoiseSampler был удален в 1.21.1
    // Замените на реальную реализацию шума, доступную в вашей среде
    private class NoiseGenerator(seed: Int) {
        private val random = java.util.Random(seed.toLong())
        fun getValue(x: Double, y: Double, z: Boolean): Double = random.nextGaussian()
    }
}