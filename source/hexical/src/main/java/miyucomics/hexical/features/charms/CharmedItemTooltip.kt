package miyucomics.hexical.features.charms

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.utils.mediaBarColor
import at.petrak.hexcasting.common.items.magic.ItemMediaHolder
import miyucomics.hexical.misc.InitHook
import miyucomics.hexical.misc.TextUtilities
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent

object CharmedItemTooltip : InitHook() {
    override fun init() {
        NeoForge.EVENT_BUS.addListener(::onTooltip)
    }

    private fun onTooltip(event: ItemTooltipEvent) {
        val stack = event.itemStack
        val lines = event.toolTip

        if (!CharmUtilities.isStackCharmed(stack)) return

        val media = CharmUtilities.getMedia(stack)
        val maxMedia = CharmUtilities.getMaxMedia(stack)

        lines.add(Component.translatable("hexical.charmed")
            .withStyle(ChatFormatting.LIGHT_PURPLE))

        // В 1.21.1 withStyle не принимает лямбду напрямую.
        // Используем Style.EMPTY.withColor(int) для создания стиля с цветом.
        val hexColorStyle = Style.EMPTY.withColor(ItemMediaHolder.HEX_COLOR)
        val barColorStyle = Style.EMPTY.withColor(mediaBarColor(media, maxMedia))

        val currentMediaText = Component.literal(
            TextUtilities.DUST_AMOUNT.format((media / MediaConstants.DUST_UNIT.toFloat()).toDouble())
        ).withStyle(hexColorStyle)

        val maxMediaText = Component.translatable(
            "hexcasting.tooltip.media",
            TextUtilities.DUST_AMOUNT.format((maxMedia / MediaConstants.DUST_UNIT.toFloat()).toDouble())
        ).withStyle(hexColorStyle)

        val percentText = Component.literal(
            "${TextUtilities.PERCENTAGE.format((100f * media / maxMedia).toDouble())}%"
        ).withStyle(barColorStyle)

        lines.add(Component.translatable(
            "hexcasting.tooltip.media_amount.advanced",
            currentMediaText, maxMediaText, percentText
        ))
    }
}