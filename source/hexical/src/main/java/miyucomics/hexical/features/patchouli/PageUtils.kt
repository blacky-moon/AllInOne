package miyucomics.hexical.features.patchouli

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.client.model.data.ModelData
import org.joml.Quaternionf

object PageUtils {
    fun renderBlock(graphics: GuiGraphics, state: BlockState, entity: BlockEntity?, centerX: Int, centerY: Int, scale: Float, tickDelta: Float) {
        val poseStack = graphics.pose()
        poseStack.pushPose()
        poseStack.translate(centerX.toFloat(), centerY.toFloat(), 100f)
        poseStack.scale(scale, -scale, scale)

        val quat = Quaternionf().rotateX(30f * (Math.PI.toFloat() / 180f)).rotateY(45f * (Math.PI.toFloat() / 180f))
        poseStack.mulPose(quat)
        poseStack.translate(-0.5f, -0.5f, -0.5f)

        val client = Minecraft.getInstance()
        val blockRenderer = client.blockRenderer
        val entityRenderer = client.blockEntityRenderDispatcher // или client.getBlockEntityRenderDispatcher()
        val bufferSource: MultiBufferSource = graphics.bufferSource()

        // Рендерим блок
        blockRenderer.renderSingleBlock(
            state,
            poseStack,
            bufferSource,
            LightTexture.FULL_BRIGHT,
            OverlayTexture.NO_OVERLAY,
            ModelData.EMPTY,
            RenderType.cutout()
        )

        // Рендерим блок-сущность
        entity?.let {
            entityRenderer.render(it, tickDelta, poseStack, bufferSource)
        }

        // Завершаем батч (если это BufferSource)
        if (bufferSource is MultiBufferSource.BufferSource) {
            bufferSource.endBatch()
        }
        poseStack.popPose()
    }
}