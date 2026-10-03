package miyucomics.hexical.features.media_jar

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Axis
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.core.Direction
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.abs

object MediaJarRenderer {
    private val FLUID_RENDER_TYPE: RenderType by lazy { MediaJarRenderTypeFactory.createFluidRenderType() }

    fun renderFluid(
        matrices: PoseStack,
        vertexConsumers: MultiBufferSource,
        filled: Float,
        offset: Vector3f = Vector3f(0f)
    ) {
        if (filled <= 0f) return
        val consumer = vertexConsumers.getBuffer(FLUID_RENDER_TYPE)
        matrices.pushPose()
        matrices.translate(0.5f, 1f / 16f, 0.5f)
        addRectangularPrism(consumer, matrices, height = filled * 12f / 16f, offset)
        matrices.popPose()
    }

    private val NEGATIVE_X_ROTATION: Quaternionf = Axis.XP.rotationDegrees(-90f)
    private val DIR2ROT: Map<Direction, Quaternionf> = Direction.entries.associateWith {
        Quaternionf(it.opposite.rotation).mul(NEGATIVE_X_ROTATION)
    }

    private fun addRectangularPrism(consumer: VertexConsumer, matrices: PoseStack, height: Float, offset: Vector3f) {
        val halfWidth = 0.5f / 2f
        val halfHeight = height / 2f

        matrices.pushPose()
        matrices.translate(0f, halfHeight, 0f)

        for (direction in Direction.entries) {
            var depth = halfWidth
            var y0 = -halfHeight
            var y1 = halfHeight

            if (direction.axis == Direction.Axis.Y) {
                depth = halfHeight
                y0 = -halfWidth
                y1 = halfWidth
            }

            matrices.pushPose()
            matrices.mulPose(DIR2ROT[direction]!!)
            matrices.translate(0f, 0f, -depth)
            addQuad(consumer, matrices, -halfWidth, y0, halfWidth, y1, offset)
            matrices.popPose()
        }
        matrices.popPose()
    }

    private fun addQuad(consumer: VertexConsumer, matrices: PoseStack, x0: Float, y0: Float, x1: Float, y1: Float, offset: Vector3f) {
        val pose = matrices.last().pose()
        val quadWidth = (kotlin.math.abs(x0 - x1) * 255).toInt()
        val quadHeight = (kotlin.math.abs(y0 - y1) * 255).toInt()

        // Формат POSITION_TEX_COLOR_NORMAL требует: Position, UV0, Color, Normal.
        // setLight (UV2) НЕ ИСПОЛЬЗУЕТСЯ!
        consumer.addVertex(pose, x0, y1, 0f).setUv(0f, 1f).setColor(quadWidth, quadHeight, 0, 0).setNormal(offset.x(), offset.y(), offset.z())
        consumer.addVertex(pose, x1, y1, 0f).setUv(1f, 1f).setColor(quadWidth, quadHeight, 0, 0).setNormal(offset.x(), offset.y(), offset.z())
        consumer.addVertex(pose, x1, y0, 0f).setUv(1f, 0f).setColor(quadWidth, quadHeight, 0, 0).setNormal(offset.x(), offset.y(), offset.z())
        consumer.addVertex(pose, x0, y0, 0f).setUv(0f, 0f).setColor(quadWidth, quadHeight, 0, 0).setNormal(offset.x(), offset.y(), offset.z())
    }
}