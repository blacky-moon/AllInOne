package miyucomics.hexical.features.lesser_sentinels

import at.petrak.hexcasting.xplat.IXplatAbstractions
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.BufferUploader
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import com.mojang.math.Axis // ИСПРАВЛЕНО: Правильный класс для осей вращения в Mojang-маппингах
import miyucomics.hexical.ClientStorage
import miyucomics.hexical.HexicalMain
import miyucomics.hexical.RenderUtils
import miyucomics.hexical.misc.InitHook
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.world.phys.Vec2
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import kotlin.math.cos
import kotlin.math.sin

@EventBusSubscriber(modid = HexicalMain.MOD_ID, value = [Dist.CLIENT])
object LesserSentinelRenderer : InitHook() {
    override fun init() {}

    @JvmStatic
    @SubscribeEvent
    fun onRenderLevelStage(event: RenderLevelStageEvent) {
        if (event.stage != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return

        ClientStorage.lesserSentinels.forEach { pos ->
            val matrices = event.poseStack
            val camera = event.camera
            val camPos = camera.position

            matrices.pushPose()
            matrices.translate(pos.x - camPos.x, pos.y - camPos.y, pos.z - camPos.z)

            // ИСПРАВЛЕНО: Используем Axis.YP и Axis.XP
            matrices.mulPose(Axis.YP.rotationDegrees(-camera.yRot))
            matrices.mulPose(Axis.XP.rotationDegrees(camera.xRot))

            RenderSystem.disableDepthTest()
            RenderSystem.enableBlend()
            RenderSystem.defaultBlendFunc()
            RenderSystem.disableCull()
            RenderSystem.setShader(GameRenderer::getPositionColorShader)

            val tessellator = Tesselator.getInstance()
            val bufferBuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR)

            val points = mutableListOf<Vec2>()
            for (i in 0..6) {
                val angle = (i % 6) * (Math.PI / 3)
                points.add(Vec2(cos(angle).toFloat(), sin(angle).toFloat()).scale(0.25f))
            }

            val pigment = IXplatAbstractions.INSTANCE.getPigment(Minecraft.getInstance().player!!).colorProvider

            fun makeVertex(offset: Vec2) {
                bufferBuilder.addVertex(
                    matrices.last().pose(),
                    offset.x,
                    offset.y,
                    0f
                ).setColor(
                    pigment.getColor(
                        ClientStorage.ticks.toFloat(),
                        pos.add(offset.x.toDouble() * 2, offset.y.toDouble() * 2, 0.0)
                    )
                )
            }

            RenderUtils.quadifyLines(::makeVertex, 0.05f, points)

            BufferUploader.drawWithShader(bufferBuilder.build())

            RenderSystem.enableCull()
            RenderSystem.disableBlend()
            RenderSystem.enableDepthTest()
            matrices.popPose()
        }
    }
}