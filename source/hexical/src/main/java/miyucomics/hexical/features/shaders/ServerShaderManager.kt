package miyucomics.hexical.features.shaders

import miyucomics.hexical.HexicalMain
import miyucomics.hexical.misc.InitHook
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.entity.player.PlayerEvent
import net.neoforged.neoforge.network.PacketDistributor

object ServerShaderManager : InitHook() {
    fun setShader(player: ServerPlayer, shader: ResourceLocation?) {
        val shaderId = shader?.toString() ?: "null"
        PacketDistributor.sendToPlayer(player, ShaderPayload(shaderId))
    }

    override fun init() {
        NeoForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun onPlayerRespawn(event: PlayerEvent.PlayerRespawnEvent) {
        setShader(event.entity as ServerPlayer, null)
    }
}
