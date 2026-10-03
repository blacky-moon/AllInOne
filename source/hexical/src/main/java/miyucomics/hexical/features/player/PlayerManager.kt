package miyucomics.hexical.features.player

import miyucomics.hexical.features.evocation.EvocationField
import miyucomics.hexical.features.evocation.EvocationTicker
import miyucomics.hexical.features.item_cache.PlayerItemCacheField
import miyucomics.hexical.features.item_cache.PlayerItemCacheTicker
import miyucomics.hexical.features.lesser_sentinels.LesserSentinelField
import miyucomics.hexical.features.media_log.MediaLogField
import miyucomics.hexical.features.player.types.PlayerField
import miyucomics.hexical.features.player.types.PlayerTicker
import miyucomics.hexical.features.telepathy.KeybindField
import miyucomics.hexical.features.telepathy.KeybindTicker
import miyucomics.hexical.features.wristpocket.WristpocketField
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.player.Player
import kotlin.reflect.KClass

class PlayerManager {
    @Suppress("UNCHECKED_CAST")
    fun <T : PlayerField> get(clazz: KClass<T>): T {
        return fields[clazz] as? T ?: error("No field registered for $clazz")
    }

    private val fields = mutableMapOf<KClass<out PlayerField>, PlayerField>()
    private fun registerField(field: PlayerField) { fields[field::class] = field }

    private val tickers = mutableListOf<PlayerTicker>()
    private fun registerTicker(ticker: PlayerTicker) { tickers.add(ticker) }

    init {
        registerField(EvocationField())
        registerField(KeybindField())
        registerField(MediaLogField())
        registerField(LesserSentinelField())
        registerField(PlayerItemCacheField())
        registerField(WristpocketField())
        registerTicker(EvocationTicker())
        registerTicker(KeybindTicker())
        registerTicker(PlayerItemCacheTicker())
    }

    fun tick(player: Player) {
        for (ticker in tickers) ticker.tick(player)
    }

    fun readNbt(compound: CompoundTag, registries: HolderLookup.Provider) {
        for (field in fields.values) field.readNbt(compound, registries)
    }

    fun writeNbt(compound: CompoundTag, registries: HolderLookup.Provider) {
        for (field in fields.values) field.writeNbt(compound, registries)
    }

    /**
     * Вызывается вручную только для специфической логики респавна,
     * которая НЕ покрывается автоматическим copyOnDeath.
     */
    fun handleRespawn(new: Player, old: Player) {
        for (field in fields.values) field.handleRespawn(new, old)
    }
}

/**
 * Безопасное получение PlayerManager через NeoForge Attachments.
 * Работает и на LocalPlayer, и на ServerPlayer без ClassCastException.
 */
fun Player.getHexicalPlayerManager(): PlayerManager {
    return this.getData(HexicalAttachments.HEXICAL_PLAYER_MANAGER.get())
}