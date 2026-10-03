package miyucomics.hexical.features.grimoires

import at.petrak.hexcasting.api.casting.iota.PatternIota
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.CustomData

object GrimoireItem : Item(Properties().stacksTo(1)) {
    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        val customData = stack.get(DataComponents.CUSTOM_DATA)
        if (customData == null) {
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
            return
        }

        val tag = customData.copyTag()
        if (!tag.contains("metadata")) {
            super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
            return
        }

        val metadata = tag.getCompound("metadata")
        val text = Component.translatable("hexical.grimoire.contains")

        val keys = metadata.allKeys
        val components = mutableListOf<Component>()

        for (key in keys) {
            val compound = metadata.getCompound(key)
            if (compound.contains("direction")) {
                val pattern = PatternIota(
                    HexPattern.fromAngles(
                        key,
                        HexDir.entries[compound.getInt("direction")]
                    )
                ).display()
                components.add(pattern)
            }
        }

        if (components.isNotEmpty()) {
            text.append(components[0])
            for (i in 1 until components.size) {
                text.append(", ").append(components[i])
            }
        }

        tooltipComponents.add(text.copy().withStyle(ChatFormatting.GRAY))
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
    }
}