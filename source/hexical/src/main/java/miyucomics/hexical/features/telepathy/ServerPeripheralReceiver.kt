package miyucomics.hexical.features.telepathy

import at.petrak.hexcasting.common.items.ItemStaff
import miyucomics.hexical.compat.HexicalCompat
import miyucomics.hexical.features.evocation.ServerEvocationManager
import miyucomics.hexical.features.staff.GauntletStaffOpener
import net.minecraft.server.level.ServerPlayer
import net.neoforged.fml.ModList

object ServerPeripheralReceiver {
    fun handleKeyPressed(payload: KeyPressedPayload, player: ServerPlayer) {
        player.serverKeybindActive()[payload.keyTranslationKey] = true
        player.serverKeybindDuration()[payload.keyTranslationKey] = 0
        if (payload.keyTranslationKey == "key.hexical.telepathy") {
            player.serverScroll = 0
        }
        if (payload.keyTranslationKey == "key.hexical.evoke") {
            ServerEvocationManager.startEvocation(player, player.server)
        }
        if (payload.keyTranslationKey == "key.hexical.open_gauntlet") {
            handleOpenStaff(player)
        }
    }

    fun handleKeyReleased(payload: KeyReleasedPayload, player: ServerPlayer) {
        player.serverKeybindActive()[payload.keyTranslationKey] = false
        player.serverKeybindDuration()[payload.keyTranslationKey] = 0
        if (payload.keyTranslationKey == "key.hexical.evoke") {
            ServerEvocationManager.endEvocation(player, player.server)
        }
    }

    fun handleScroll(payload: ScrollPayload, player: ServerPlayer) {
        player.serverScroll += payload.scrollDelta
    }

    private fun handleOpenStaff(player: ServerPlayer) {

        if (!ModList.get().isLoaded("curios")) return

        val staffStack = HexicalCompat.getGauntletStaff(player) ?: return
        GauntletStaffOpener.open(player, staffStack, player.isShiftKeyDown)
    }
}