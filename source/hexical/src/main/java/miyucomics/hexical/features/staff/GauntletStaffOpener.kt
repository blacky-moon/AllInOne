package miyucomics.hexical.features.staff

import at.petrak.hexcasting.api.casting.eval.vm.CastingVM
import at.petrak.hexcasting.common.lib.HexAttributes
import at.petrak.hexcasting.common.msgs.MsgClearSpiralPatternsS2C
import at.petrak.hexcasting.common.msgs.MsgOpenSpellGuiS2C
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.server.level.ServerPlayer
import net.minecraft.stats.Stats
import net.minecraft.world.InteractionHand
import net.minecraft.world.item.ItemStack

object GauntletStaffOpener {
    private val VIRTUAL_HAND = InteractionHand.MAIN_HAND

    fun open(player: ServerPlayer, stack: ItemStack, sneaking: Boolean) {
        if (player.getAttributeValue(HexAttributes.FEEBLE_MIND) > 0.0) return

        if (sneaking) {
            IXplatAbstractions.INSTANCE.clearCastingData(player)
            val clearPacket = MsgClearSpiralPatternsS2C(player.uuid)
            IXplatAbstractions.INSTANCE.sendPacketToPlayer(player, clearPacket)
            IXplatAbstractions.INSTANCE.sendPacketTracking(player, clearPacket)


        }

        val vm: CastingVM = IXplatAbstractions.INSTANCE.getStaffcastVM(player, VIRTUAL_HAND)
        val patterns = IXplatAbstractions.INSTANCE.getPatternsSavedInUi(player)
        val ravenmind = vm.image.ravenmind().orElse(null)

        IXplatAbstractions.INSTANCE.sendPacketToPlayer(
            player,
            MsgOpenSpellGuiS2C(VIRTUAL_HAND, patterns, vm.image.stack, ravenmind, 0)
        )
        player.awardStat(Stats.ITEM_USED.get(stack.item))
    }
}