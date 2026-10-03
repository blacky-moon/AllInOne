package miyucomics.hexical.features.player

import miyucomics.hexical.misc.InitHook
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.entity.player.PlayerEvent

object RespawnPersistHook : InitHook() {
    override fun init() {
        NeoForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun onPlayerRespawn(event: PlayerEvent.Clone) {
        // Данные уже скопированы автоматически благодаря copyOnDeath(true)
        // Здесь вызываем только дополнительную логику обработки респавна
        if (event.isWasDeath) {
            val newManager = event.entity.getHexicalPlayerManager()
            val oldManager = event.original.getHexicalPlayerManager()

            // Передаем менеджеры или самих игроков для специфической пост-обработки
            newManager.handleRespawn(event.entity, event.original)
        }
    }
}