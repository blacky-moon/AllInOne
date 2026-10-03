package miyucomics.hexical.features.shaders

import com.mojang.logging.LogUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.PostChain
import net.minecraft.resources.ResourceLocation
import java.io.IOException

object ShaderRenderer {
    private val LOGGER = LogUtils.getLogger()

    private var activeShader: PostChain? = null
    private var lastShader: PostChain? = null
    private var lastWidth = 0
    private var lastHeight = 0

    @JvmStatic
    fun render(deltaTick: Float) {
        val shader = activeShader ?: return

        if (lastShader != shader) {
            lastShader = shader
            lastWidth = 0
            lastHeight = 0
        }

        updateEffectSize(shader)
        shader.process(deltaTick)
        Minecraft.getInstance().mainRenderTarget.bindWrite(false)
    }

    fun setEffect(location: ResourceLocation?) {
        activeShader?.close()
        if (location == null) {
            activeShader = null
            return
        }
        try {
            val client = Minecraft.getInstance()
            activeShader = PostChain(
                client.textureManager,
                client.resourceManager,
                client.mainRenderTarget,
                location
            )
        } catch (e: IOException) {
            // Раньше исключение глушилось молча — из-за этого было невозможно понять,
            // почему часть шейдеров "просто не работает". Теперь пишем причину в лог:
            // отсутствующий файл, битый JSON, неверные target'ы и т.д. будут видны сразу.
            LOGGER.error("Failed to load post-processing shader '{}': {}", location, e.message, e)
            activeShader = null
        } catch (e: Exception) {
            // PostChain может кидать не только IOException (например JsonSyntaxException
            // при некорректном формате effect-файла) — ловим шире, чтобы не терять эти случаи молча.
            LOGGER.error("Unexpected error loading post-processing shader '{}'", location, e)
            activeShader = null
        }
    }

    private fun updateEffectSize(effect: PostChain) {
        val client = Minecraft.getInstance()
        val width = client.window.width
        val height = client.window.height
        if ((width != lastWidth || height != lastHeight) && width != 0 && height != 0) {
            lastWidth = width
            lastHeight = height
            effect.resize(width, height)
        }
    }
}