package miyucomics.hexical.features.media_jar

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.utils.mediaBarColor
import at.petrak.hexcasting.common.items.magic.ItemMediaHolder
import miyucomics.hexical.features.transmuting.TransmutationResult
import miyucomics.hexical.features.transmuting.TransmutingHelper
import miyucomics.hexical.inits.HexicalBlocks
import miyucomics.hexical.inits.HexicalComponents
import miyucomics.hexical.inits.HexicalSounds
import miyucomics.hexical.misc.TextUtilities
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextColor
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ClickAction
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import kotlin.math.max
import kotlin.math.min

// Единственный источник истины для количества медиа в ItemStack-версии банки —
// компонент HexicalComponents.MEDIA_HOLDER (hexical:media_holder). Раньше здесь
// читали/писали "media" через CUSTOM_DATA (а до этого — BLOCK_ENTITY_DATA),
// но нигде в коде эти поля фактически не совпадали с тем, что показывает
// креативная вкладка (HexicalItems задаёт полную банку именно через
// MEDIA_HOLDER) — отсюда тултип всегда показывал 0 и дунканье не сохранялось.
class MediaJarItem : BlockItem(HexicalBlocks.MEDIA_JAR_BLOCK.get(), Item.Properties().stacksTo(1)) {
    override fun appendHoverText(stack: ItemStack, context: Item.TooltipContext, list: MutableList<Component>, flag: TooltipFlag) {
        val media = stack.get(HexicalComponents.MEDIA_HOLDER.get()) ?: 0L
        list.add(Component.translatable("hexcasting.tooltip.media_amount.advanced",
            Component.literal(TextUtilities.DUST_AMOUNT.format((media / MediaConstants.DUST_UNIT.toFloat()).toDouble())).withStyle { it.withColor(ItemMediaHolder.HEX_COLOR) },
            Component.translatable("hexcasting.tooltip.media", TextUtilities.DUST_AMOUNT.format((MediaJarBlock.MAX_CAPACITY / MediaConstants.DUST_UNIT.toFloat()).toDouble())).withStyle { it.withColor(ItemMediaHolder.HEX_COLOR) },
            Component.literal(TextUtilities.PERCENTAGE.format((100f * media / MediaJarBlock.MAX_CAPACITY).toDouble()) + "%").withStyle { it.withColor(TextColor.fromRgb(mediaBarColor(media, MediaJarBlock.MAX_CAPACITY))) }
        ))
    }

    override fun overrideStackedOnOther(stack: ItemStack, slot: Slot, clickType: ClickAction, player: Player): Boolean {
        if (clickType != ClickAction.SECONDARY)
            return false
        val slotStack = slot.getItem()
        if (slotStack.isEmpty)
            return false
        val level = player.level()

        var currentMedia = stack.get(HexicalComponents.MEDIA_HOLDER.get()) ?: 0L
        val result = TransmutingHelper.transmuteItem(
            level,
            slotStack,
            currentMedia,
            { toInsert ->
                val given = min(MediaJarBlock.MAX_CAPACITY - currentMedia, toInsert)
                currentMedia = max(min(currentMedia + given, MediaJarBlock.MAX_CAPACITY), 0)
                given
            },
            { toWithdraw ->
                if (currentMedia >= toWithdraw) {
                    currentMedia -= toWithdraw
                    true
                } else {
                    currentMedia = 0
                    false
                }
            }
        )
        // Обязательно пишем обратно в стек — коллбэки выше меняют только
        // локальную переменную currentMedia, а не сам компонент на предмете.
        stack.set(HexicalComponents.MEDIA_HOLDER.get(), currentMedia)

        return when (result) {
            is TransmutationResult.AbsorbedMedia -> {
                level.playSound(null, player.x, player.y, player.z, HexicalSounds.AMETHYST_MELT.get(), SoundSource.BLOCKS, 1f, 1f)
                true
            }
            is TransmutationResult.TransmutedItems -> {
                level.playSound(null, player.x, player.y, player.z, HexicalSounds.ITEM_DUNKS.get(), SoundSource.BLOCKS, 1f, 1f)
                val output: MutableList<ItemStack> = result.output.toMutableList()
                if (slot.getItem().isEmpty)
                    slot.set(output.removeAt(0))
                output.forEach { player.getInventory().add(it) }
                true
            }
            is TransmutationResult.RefilledHolder -> {
                level.playSound(null, player.x, player.y, player.z, HexicalSounds.ITEM_DUNKS.get(), SoundSource.BLOCKS, 1f, 1f)
                true
            }
            is TransmutationResult.Pass -> false
        }
    }
}