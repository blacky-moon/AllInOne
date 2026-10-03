package miyucomics.hexical.features.specklikes

import at.petrak.hexcasting.api.HexAPI.modLoc
import at.petrak.hexcasting.api.pigment.FrozenPigment
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Axis
import miyucomics.hexical.features.specklikes.mesh.MeshEntity
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import org.joml.Matrix4f
import org.joml.Vector3f
import kotlin.math.cos
import kotlin.math.sin

class MeshRenderer(context: EntityRendererProvider.Context) : EntityRenderer<MeshEntity>(context) {

    override fun getTextureLocation(entity: MeshEntity): ResourceLocation? = null

    override fun shouldRender(entity: MeshEntity, frustum: net.minecraft.client.renderer.culling.Frustum?, x: Double, y: Double, z: Double): Boolean = true

    override fun render(
        entity: MeshEntity,
        entityYaw: Float,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int
    ) {
        val vertices = entity.clientVertices
        if (vertices.size < 2) return

        poseStack.pushPose()
        poseStack.translate(0.0, 0.25, 0.0)
        poseStack.mulPose(Axis.YP.rotationDegrees(-entity.yRot))
        poseStack.mulPose(Axis.XP.rotationDegrees(entity.xRot))
        poseStack.mulPose(Axis.ZP.rotationDegrees(entity.clientRoll))
        poseStack.scale(entity.clientSize, entity.clientSize, entity.clientSize)

        val buffer = bufferSource.getBuffer(RENDER_LAYER)

        for (i in 1 until vertices.size) {
            val a = vertices[i - 1]
            val b = vertices[i]
            drawConnection(
                poseStack, buffer,
                Vector3f(a.x, a.y, a.z),
                Vector3f(b.x, b.y, b.z),
                entity.clientPigment,
                entity.clientThickness * 0.025f
            )
        }

        poseStack.popPose()
    }

    private fun drawConnection(
        poseStack: PoseStack,
        consumer: VertexConsumer,
        start: Vector3f,
        end: Vector3f,
        pigment: FrozenPigment,
        thickness: Float
    ) {
        val direction = Vector3f(end).sub(start).normalize()
        var perpendicular = Vector3f(direction).cross(Vector3f(1f, 0f, 0f))
        if (direction.dot(Vector3f(1f, 0f, 0f)) > 0.99f || direction.dot(Vector3f(1f, 0f, 0f)) < -0.99f) {
            perpendicular = Vector3f(direction).cross(Vector3f(0f, 1f, 0f))
        }

        val pose: Matrix4f = poseStack.last().pose()
        val perpendicularCrossProduct = Vector3f(perpendicular).cross(direction)
        val directionDotProduct = Vector3f(direction).mul(direction.dot(perpendicular))

        for (i in 0 until SIDES) {
            val startAngle = i * ANGLE_INCREMENT
            val endAngle = ((i + 1) % SIDES) * ANGLE_INCREMENT

            val a = calcOffset(perpendicular, perpendicularCrossProduct, directionDotProduct, startAngle, thickness)
            val b = calcOffset(perpendicular, perpendicularCrossProduct, directionDotProduct, endAngle, thickness)

            vertex(consumer, pose, Vector3f(start).add(a), pigment)
            vertex(consumer, pose, Vector3f(start).add(b), pigment)
            vertex(consumer, pose, Vector3f(end).add(b), pigment)
            vertex(consumer, pose, Vector3f(end).add(a), pigment)
        }
    }

    private fun calcOffset(
        perpendicular: Vector3f,
        perpCrossDir: Vector3f,
        dirDotPerp: Vector3f,
        angle: Float,
        thickness: Float
    ): Vector3f {
        val cosA = cos(angle.toDouble()).toFloat()
        val sinA = sin(angle.toDouble()).toFloat()
        return Vector3f(perpendicular).mul(cosA)
            .add(Vector3f(perpCrossDir).mul(sinA))
            .add(Vector3f(dirDotPerp).mul(1f - cosA))
            .normalize()
            .mul(thickness)
    }

    private fun vertex(consumer: VertexConsumer, pose: Matrix4f, position: Vector3f, pigment: FrozenPigment) {
        val color = pigment.colorProvider.getColor(0f, net.minecraft.world.phys.Vec3(position.x.toDouble(), position.y.toDouble(), position.z.toDouble()))
        consumer.addVertex(pose, position.x, position.y, position.z)
            .setColor(color)
            .setUv(0f, 0f)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(0xF000F0)
            .setNormal(0f, 1f, 0f)
    }

    companion object {
        private const val SIDES = 6
        private const val ANGLE_INCREMENT = (2.0 * Math.PI / SIDES).toFloat()
        private val RENDER_LAYER: RenderType = RenderType.entityCutoutNoCull(modLoc("textures/entity/white.png"))
    }
}