package miyucomics.hexical.features.spike

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Axis
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import org.joml.Matrix4f
import org.joml.Vector3f
import kotlin.math.floor
import kotlin.math.max

class SpikeRenderer(context: EntityRendererProvider.Context) : EntityRenderer<SpikeEntity>(context) {

    private val textures = listOf(
        ResourceLocation.withDefaultNamespace("textures/block/small_amethyst_bud.png"),
        ResourceLocation.withDefaultNamespace("textures/block/medium_amethyst_bud.png"),
        ResourceLocation.withDefaultNamespace("textures/block/large_amethyst_bud.png"),
        ResourceLocation.withDefaultNamespace("textures/block/amethyst_cluster.png")
    )

    override fun getTextureLocation(spike: SpikeEntity): ResourceLocation {
        val progress = spike.getAnimationProgress()
        val index = max(floor(progress * (textures.size - 1)).toInt(), 0)
        return textures[index.coerceIn(0, textures.lastIndex)]
    }

    override fun render(
        spike: SpikeEntity,
        entityYaw: Float,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int
    ) {
        val progress = spike.getAnimationProgress()
        if (progress <= 0f) return

        poseStack.pushPose()

        val texture = getTextureLocation(spike)
        val buffer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(texture))

        val direction = spike.getSpikeDirection()

        // Поворачиваем так, чтобы шип "рос" из грани блока в сторону direction.
        // Базовая геометрия строится смотрящей вверх (+Y), затем поворачивается.
        when (direction) {
            Direction.DOWN -> poseStack.mulPose(Axis.XP.rotationDegrees(180f))
            Direction.NORTH -> poseStack.mulPose(Axis.XP.rotationDegrees(90f))
            Direction.SOUTH -> poseStack.mulPose(Axis.XN.rotationDegrees(90f))
            Direction.WEST -> poseStack.mulPose(Axis.ZN.rotationDegrees(90f))
            Direction.EAST -> poseStack.mulPose(Axis.ZP.rotationDegrees(90f))
            Direction.UP -> { /* без поворота, база уже смотрит вверх */ }
        }

        // Длина шипа растёт вместе с progress; основание всегда 0.5x0.5, чтобы
        // не "проваливалось" в блок при малом progress.
        val height = 0.9f * progress
        val half = 0.25f

        val mat: Matrix4f = poseStack.last().pose()

        // Апекс (вершина шипа), основание — квадрат вокруг (0,0,0) на высоте 0.
        val apex = Vector3f(0f, height, 0f)
        val base00 = Vector3f(-half, 0f, -half)
        val base10 = Vector3f(half, 0f, -half)
        val base11 = Vector3f(half, 0f, half)
        val base01 = Vector3f(-half, 0f, half)

        // 4 боковые грани пирамиды. VertexConsumer здесь работает в режиме QUADS,
        // поэтому каждую треугольную грань дублируем как вырожденный квад
        // (последняя вершина повторяет апекс) — это надёжно рендерится без
        // отдельного TRIANGLES RenderType.
        quadFace(buffer, mat, base00, base10, apex, apex, normalFor(base00, base10, apex))
        quadFace(buffer, mat, base10, base11, apex, apex, normalFor(base10, base11, apex))
        quadFace(buffer, mat, base11, base01, apex, apex, normalFor(base11, base01, apex))
        quadFace(buffer, mat, base01, base00, apex, apex, normalFor(base01, base00, apex))

        // Основание (видно снизу, полезно при взгляде исподнизу/сквозь блок)
        quadFace(buffer, mat, base00, base01, base11, base10, Vector3f(0f, -1f, 0f))

        poseStack.popPose()
    }

    private fun normalFor(a: Vector3f, b: Vector3f, c: Vector3f): Vector3f {
        val u = Vector3f(b).sub(a)
        val v = Vector3f(c).sub(a)
        return Vector3f(u).cross(v).normalize()
    }

    private fun quadFace(
        consumer: VertexConsumer,
        mat: Matrix4f,
        p0: Vector3f, p1: Vector3f, p2: Vector3f, p3: Vector3f,
        normal: Vector3f
    ) {
        vertex(consumer, mat, p0, 0f, 0f, normal)
        vertex(consumer, mat, p1, 1f, 0f, normal)
        vertex(consumer, mat, p2, 1f, 1f, normal)
        vertex(consumer, mat, p3, 0f, 1f, normal)
    }

    private fun vertex(
        consumer: VertexConsumer,
        mat: Matrix4f,
        p: Vector3f,
        u: Float, v: Float,
        normal: Vector3f
    ) {
        consumer.addVertex(mat, p.x, p.y, p.z)
            .setColor(255, 255, 255, 255)
            .setUv(u, v)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(0xF000F0) // Max light
            .setNormal(normal.x, normal.y, normal.z)
    }
}