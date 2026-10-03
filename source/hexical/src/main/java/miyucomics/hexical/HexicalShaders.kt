package miyucomics.hexical

import com.mojang.blaze3d.vertex.DefaultVertexFormat
import net.minecraft.client.renderer.ShaderInstance
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.RegisterShadersEvent

@EventBusSubscriber(modid = HexicalMain.MOD_ID, value = [Dist.CLIENT])
object HexicalShaders {
    var mediaJarShader: ShaderInstance? = null

    @SubscribeEvent
    @JvmStatic
    fun registerShaders(event: RegisterShadersEvent) {
        event.registerShader(
            ShaderInstance(
                event.resourceProvider,
                HexicalMain.id("media_jar"),
                DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL
            )
        ) { shader ->
            mediaJarShader = shader
        }
    }
}