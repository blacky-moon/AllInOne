package miyucomics.hexical.features.mute

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import miyucomics.hexical.HexicalMain
import miyucomics.hexical.inits.HexicalComponents
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.EntityRenderDispatcher
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity

object MuteRenderer {
    private val ICON: ResourceLocation = HexicalMain.id("textures/mob_effect/muted.png")

    @JvmStatic
    fun render(
        entity: Entity,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        dispatcher: EntityRenderDispatcher
    ) {
        val muted = entity.getCapability(HexicalComponents.MUTED_CAP)?.muted ?: return
        if (!muted) return

        val time = entity.level().getGameTime() + Minecraft.getInstance().timer.gameTimeDeltaTicks
        val height = entity.bbHeight

        poseStack.pushPose()
        poseStack.translate(0.0, (height + Mth.sin(time * 0.05f) * 0.05f + 0.5f).toDouble(), 0.0)
        poseStack.mulPose(dispatcher.cameraOrientation())
        poseStack.scale(0.5f, 0.5f, 0.5f)

        val buffer: VertexConsumer = bufferSource.getBuffer(RenderType.entityCutout(ICON))
        val pose = poseStack.last()
        val mat = pose.pose()

        buffer.addVertex(mat, -0.5f, -0.5f, 0f)
            .setColor(255, 255, 255, 255)
            .setUv(1f, 1f)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(LightTexture.FULL_BRIGHT)
            .setNormal(pose, 0f, 1f, 0f)

        buffer.addVertex(mat, -0.5f, 0.5f, 0f)
            .setColor(255, 255, 255, 255)
            .setUv(1f, 0f)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(LightTexture.FULL_BRIGHT)
            .setNormal(pose, 0f, 1f, 0f)

        buffer.addVertex(mat, 0.5f, 0.5f, 0f)
            .setColor(255, 255, 255, 255)
            .setUv(0f, 0f)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(LightTexture.FULL_BRIGHT)
            .setNormal(pose, 0f, 1f, 0f)

        buffer.addVertex(mat, 0.5f, -0.5f, 0f)
            .setColor(255, 255, 255, 255)
            .setUv(0f, 1f)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(LightTexture.FULL_BRIGHT)
            .setNormal(pose, 0f, 1f, 0f)

        poseStack.popPose()
    }
}