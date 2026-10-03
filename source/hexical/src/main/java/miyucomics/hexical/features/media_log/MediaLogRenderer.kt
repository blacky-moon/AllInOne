package miyucomics.hexical.features.media_log

import at.petrak.hexcasting.client.render.*
import com.mojang.blaze3d.vertex.PoseStack
import miyucomics.hexical.ClientStorage
import miyucomics.hexical.ClientStorage.ticks
import miyucomics.hexical.HexicalMain
import miyucomics.hexical.misc.InitHook
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.util.Mth
import net.minecraft.world.phys.Vec2
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.ClientTickEvent
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent
import net.neoforged.neoforge.client.gui.VanillaGuiLayers
import kotlin.math.max
import kotlin.math.min

@EventBusSubscriber(modid = HexicalMain.MOD_ID, value = [Dist.CLIENT])

object MediaLogRenderer : InitHook() {
    const val FADE_IN_DURATION: Int = 40
    var fadingInLog = false
    var fadingInLogStart = 0
    var fadingInLogTweener = 0

    override fun init() {}
    @JvmStatic
    @SubscribeEvent
    fun onClientTick(event: ClientTickEvent.Post) {
        fadingInLogTweener = if (fadingInLog) min(ticks - fadingInLogStart, FADE_IN_DURATION) else max(fadingInLogTweener - 5, 0)
    }
    @JvmStatic
    @SubscribeEvent
    fun onRenderGuiLayer(event: RenderGuiLayerEvent.Post) {
        if (event.name != VanillaGuiLayers.HOTBAR) return
        if (fadingInLogTweener == 0) return

        val guiGraphics = event.guiGraphics

        // ИСПРАВЛЕНО: В 1.21.1 frameTime удален. Берем partialTick напрямую из события рендера
        val partialTick = event.partialTick.gameTimeDeltaTicks
        val progress = (fadingInLogTweener + partialTick) / FADE_IN_DURATION.toFloat()

        val bgAlpha = (progress * 100).toInt()
        val backgroundColor = (bgAlpha shl 24) // Черный цвет с альфа-каналом
        guiGraphics.fill(0, 0, guiGraphics.guiWidth(), guiGraphics.guiHeight(), backgroundColor)

        val pose = guiGraphics.pose()
        pose.pushPose()
        pose.translate(guiGraphics.guiWidth() / 2f, guiGraphics.guiHeight() / 2f, 0f)

        for (phase in phases) {
            val localProgress = (progress - phase.start) / phase.duration
            if (localProgress > 0f)
                phase.render(guiGraphics, Mth.clamp(localProgress, 0f, 1f))
        }

        pose.popPose()
    }

    fun drawMishapText(guiGraphics: GuiGraphics, alpha: Float) {
        val mishapText = ClientStorage.mediaLog.mishap
        val font = Minecraft.getInstance().font
        val color = (alpha * 255).toInt() shl 24 or 0xFFFFFF
        guiGraphics.drawCenteredString(font, mishapText, 0, -guiGraphics.guiHeight() / 2 + 10, color)
    }

    fun drawMediaLogPattern(pose: PoseStack, index: Int, alpha: Float) {
        pose.pushPose()
        val x = index % 8
        val y = (index / 8)
        pose.translate(x * 50f, y * 50f, 0f)

        val patternsList = ClientStorage.mediaLog.patterns.buffer()
        if (patternsList.size > index) {
            pose.translate(-12.5f, -12.5f, 0f)
            pose.scale(25f, 25f, 25f)
            val color = (alpha * 255).toInt() shl 24 or 0xFFFFFF

            val patternlike = HexPatternLike.of(patternsList.elementAt(index))
            val patternSettings = WorldlyPatternRenderHelpers.READABLE_SCROLL_SETTINGS
            val staticPoints = HexPatternPoints.getStaticPoints(patternlike, patternSettings, 0.0)
            val nonzappyLines = patternlike.nonZappyPoints
            val zappyPattern = makeZappy(nonzappyLines, findDupIndices(nonzappyLines), patternSettings.hops, patternSettings.variance, patternSettings.speed, patternSettings.flowIrregular, patternSettings.readabilityOffset, patternSettings.lastSegmentProp, 0.0)

            drawLineSeq(pose.last().pose(), staticPoints.scaleVecs(zappyPattern), 0.05f, color, color, VCDrawHelper.getHelper(null, pose, 0.001f))
        } else {
            drawSpot(pose.last().pose(), Vec2.ZERO, 0.2f, 1f, 1f, 1f, alpha)
        }

        pose.popPose()
    }

    fun drawStackItem(guiGraphics: GuiGraphics, index: Int, alpha: Float) {
        val stackList = ClientStorage.mediaLog.stack.buffer()
        if (index >= stackList.size || alpha == 0f)
            return

        val pose = guiGraphics.pose()
        pose.pushPose()
        val font = Minecraft.getInstance().font
        val color = (alpha * 255).toInt() shl 24 or 0xFFFFFF

        guiGraphics.drawCenteredString(font, stackList.elementAt(index), 17, 16 * (4 - index), color)
        pose.popPose()
    }

    private val phases = listOf(
        Phase(0.0f, 0.2f) { ctx, t ->
            drawMishapText(ctx, t)
        },
        Phase(0.2f, 0.5f) { ctx, t ->
            val progress = Mth.clamp(t, 0f, 1f)
            val visible = (progress * 32).toInt()
            val alpha = (progress * 32) % 1
            for (i in 0 until visible)
                drawMediaLogPattern(ctx.pose(), i, 1f)
            if (visible < 31)
                drawMediaLogPattern(ctx.pose(), visible, alpha)
        },
        Phase(0.7f, 0.3f) { ctx, t ->
            val progress = Mth.clamp(t, 0f, 1f)
            val visible = (progress * 8).toInt()
            val alpha = (progress * 8) % 1
            for (i in 0 until visible)
                drawStackItem(ctx, i, 1f)
            if (visible < 7)
                drawStackItem(ctx, visible, alpha)
        }
    )

    private data class Phase(
        val start: Float,
        val duration: Float,
        val render: (GuiGraphics, Float) -> Unit
    )
}