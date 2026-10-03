package miyucomics.hexical.features.shaders

import miyucomics.hexical.misc.InitHook
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent
import net.neoforged.neoforge.common.NeoForge

object ClientShaderReceiver : InitHook() {
    override fun init() {
        NeoForge.EVENT_BUS.addListener<ClientPlayerNetworkEvent.LoggingOut> { event ->
            ShaderRenderer.setEffect(null)
        }
    }
}