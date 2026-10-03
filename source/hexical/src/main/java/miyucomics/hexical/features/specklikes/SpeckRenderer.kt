package miyucomics.hexical.features.specklikes

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import miyucomics.hexical.features.specklikes.speck.SpeckEntity
import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.resources.ResourceLocation

class SpeckRenderer(context: EntityRendererProvider.Context) : EntityRenderer<SpeckEntity>(context) {

    override fun getTextureLocation(entity: SpeckEntity): ResourceLocation? = null

    override fun shouldRender(entity: SpeckEntity, frustum: net.minecraft.client.renderer.culling.Frustum, x: Double, y: Double, z: Double): Boolean = true

    override fun render(
        entity: SpeckEntity,
        entityYaw: Float,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int
    ) {
        poseStack.pushPose()
        poseStack.translate(0.0, 0.375, 0.0)
        poseStack.mulPose(Axis.YP.rotationDegrees(-entity.yRot))
        poseStack.mulPose(Axis.XP.rotationDegrees(entity.xRot))
        poseStack.mulPose(Axis.ZP.rotationDegrees(entity.clientRoll))

        val scale = entity.clientSize * 0.02f
        poseStack.scale(scale, -scale, scale)

        val text = entity.entityData.get(SpeckEntity.TEXT_DATA_TRACKER)
        val textWidth = font.width(text)

        // В 1.21.1 TextRenderer.draw заменен на font.drawInBatch или font.draw
        // Используем drawInBatch для корректной работы с VertexConsumer
        font.drawInBatch(
            text,
            -textWidth / 2f,
            0f,
            0xFFFFFFFF.toInt(),
            true, // dropShadow
            poseStack.last().pose(),
            bufferSource,
            Font.DisplayMode.NORMAL,
            0, // backgroundColor
            0xF000F0 // packedLight (max light)
        )

        poseStack.popPose()
    }
}