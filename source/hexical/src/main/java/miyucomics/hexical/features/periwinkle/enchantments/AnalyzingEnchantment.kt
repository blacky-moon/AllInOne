package miyucomics.hexical.features.periwinkle.enchantments

import at.petrak.hexcasting.common.lib.HexAttributes
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EquipmentSlotGroup
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.neoforged.neoforge.event.ItemAttributeModifierEvent

object AnalyzingEnchantment : LeiEnchantmentLogic {
    override val enchantmentId = "analyzing"
    val SCRY_SIGHT_BOOST_ID: ResourceLocation = ResourceLocation.fromNamespaceAndPath("hexverse", "analyzing_boost")

    override fun modifyAttributes(event: ItemAttributeModifierEvent, level: Int) {
        event.addModifier(
            HexAttributes.SCRY_SIGHT,
            AttributeModifier(SCRY_SIGHT_BOOST_ID, 1.0, AttributeModifier.Operation.ADD_VALUE),
            EquipmentSlotGroup.HEAD
        )
    }
}