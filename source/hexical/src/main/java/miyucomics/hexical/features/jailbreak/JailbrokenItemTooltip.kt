package miyucomics.hexical.features.jailbreak

import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.common.items.magic.ItemPackagedHex
import miyucomics.hexical.features.charms.CharmUtilities
import miyucomics.hexical.features.curios.CurioItem
import miyucomics.hexical.misc.InitHook
import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponents
import com.mojang.serialization.DynamicOps
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent

object JailbrokenItemTooltip : InitHook() {
    override fun init() {
        NeoForge.EVENT_BUS.addListener<ItemTooltipEvent> { event ->
            val stack = event.itemStack
            val lines = event.toolTip
            val tag = stack.get(DataComponents.CUSTOM_DATA)?.copyTag() ?: return@addListener

            if (stack.item !is ItemPackagedHex || !tag.getBoolean("cracked"))
                return@addListener

            // ИСПРАВЛЕНИЕ 1: registries() возвращает Optional, используем orElse(null) и ранний выход
            val lookup = event.context.registries() ?: return@addListener

            // ИСПРАВЛЕНИЕ 2: Явно указываем тип DynamicOps<Tag>, чтобы Kotlin корректно связал дженерики
            val ops: DynamicOps<Tag> = lookup.createSerializationContext(NbtOps.INSTANCE)

            if (tag.contains(ItemPackagedHex.TAG_PROGRAM)) {
                // Tag.TAG_COMPOUND — это byte, в Kotlin нужно явно конвертировать в Int
                lines.add(Component.translatable("hexical.cracked.hex", getText(tag.getList(ItemPackagedHex.TAG_PROGRAM, Tag.TAG_COMPOUND.toInt()), ops)))
            } else {
                lines.add(Component.translatable("hexical.cracked.cracked").withStyle(ChatFormatting.GOLD))
            }
        }

        NeoForge.EVENT_BUS.addListener<ItemTooltipEvent> { event ->
            val stack = event.itemStack
            val lines = event.toolTip
            val tag = stack.get(DataComponents.CUSTOM_DATA)?.copyTag() ?: return@addListener

            if (stack.item !is CurioItem || !tag.getBoolean("cracked"))
                return@addListener

            val lookup = event.context.registries() ?: return@addListener
            val ops: DynamicOps<Tag> = lookup.createSerializationContext(NbtOps.INSTANCE)

            if (CharmUtilities.isStackCharmed(stack)) {
                val charmTag = CharmUtilities.getCompound(stack)
                lines.add(Component.translatable("hexical.cracked.hex", getText(charmTag.getList("hex", Tag.TAG_COMPOUND.toInt()), ops)))
            } else {
                lines.add(Component.translatable("hexical.cracked.cracked").withStyle(ChatFormatting.GOLD))
            }
        }
    }

    private fun getText(hex: ListTag, ops: DynamicOps<Tag>): Component {
        return hex.fold(Component.empty() as Component) { acc, curr ->
            val iota = IotaType.TYPED_CODEC.parse(ops, curr).result().orElse(null)
            acc.copy().append(iota?.display() ?: Component.empty())
        }
    }
}