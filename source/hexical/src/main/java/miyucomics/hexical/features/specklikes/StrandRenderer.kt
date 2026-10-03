package miyucomics.hexical.features.specklikes

import at.petrak.hexcasting.api.HexAPI.modLoc
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import miyucomics.hexical.RenderUtils
import miyucomics.hexical.features.specklikes.strand.StrandEntity
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3
import org.joml.Matrix4f

class StrandRenderer(context: EntityRendererProvider.Context) : EntityRenderer<StrandEntity>(context) {

    override fun getTextureLocation(entity: StrandEntity): ResourceLocation? = null

    override fun shouldRender(entity: StrandEntity, frustum: net.minecraft.client.renderer.culling.Frustum?, x: Double, y: Double, z: Double): Boolean = true

    override fun render(
        entity: StrandEntity,
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

        val pose: Matrix4f = poseStack.last().pose()
        val buffer = bufferSource.getBuffer(RENDER_LAYER)

        // Используем Vec2 вместо Vector2f для соответствия сигнатуре quadifyLines
        fun makeVertex(pos: Vec2) {
            val worldPos = Vec3(pos.x.toDouble(), pos.y.toDouble(), 0.0).scale(2.0).add(entity.position())
            val color = entity.clientPigment.colorProvider.getColor(0f, worldPos)
            buffer.addVertex(pose, pos.x, pos.y, 0f)
                .setColor(color)
                .setUv(0f, 0f)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(0xF000F0)
                .setNormal(0f, 1f, 0f)
        }

        RenderUtils.quadifyLines(::makeVertex, entity.clientThickness * 0.05f / entity.clientSize, entity.clientVertices)

        poseStack.popPose()
    }

    companion object {
        private val RENDER_LAYER: RenderType = RenderType.entityCutoutNoCull(modLoc("textures/entity/white.png"))
    }
}