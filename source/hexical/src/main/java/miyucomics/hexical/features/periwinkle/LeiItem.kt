package miyucomics.hexical.features.periwinkle

import at.petrak.hexcasting.common.lib.HexAttributes
import net.minecraft.core.Holder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.EquipmentSlotGroup
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.ItemAttributeModifiers
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import miyucomics.hexical.HexicalMain

class LeiItem : ArmorItem(
    BuiltInRegistries.ARMOR_MATERIAL.wrapAsHolder(LeiArmorMaterial.INSTANCE),
    Type.HELMET,
    Properties().stacksTo(1)
) {

    override fun getDefaultAttributeModifiers(): ItemAttributeModifiers {
        val modifiers = ItemAttributeModifiers.builder()

        modifiers.add(
            HexAttributes.GRID_ZOOM,
            AttributeModifier(
                HexicalMain.id("scrying_lens_zoom"),
                0.5,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
            ),
            EquipmentSlotGroup.HEAD
        )

        modifiers.add(
            Attributes.ARMOR,
            AttributeModifier(
                HexicalMain.id("armor_modifier"),
                this.material.value().getDefense(this.type).toDouble(),
                AttributeModifier.Operation.ADD_VALUE
            ),
            EquipmentSlotGroup.HEAD
        )

        modifiers.add(
            Attributes.ARMOR_TOUGHNESS,
            AttributeModifier(
                HexicalMain.id("armor_toughness_modifier"),
                this.material.value().toughness.toDouble(),
                AttributeModifier.Operation.ADD_VALUE
            ),
            EquipmentSlotGroup.HEAD
        )

        return modifiers.build()
    }

    override fun interactLivingEntity(
        stack: ItemStack,
        player: Player,
        target: LivingEntity,
        hand: InteractionHand
    ): InteractionResult {
        if (target is Player && target.getItemBySlot(EquipmentSlot.HEAD).isEmpty) {
            target.setItemSlot(EquipmentSlot.HEAD, stack.copy())
            stack.shrink(1)
            return InteractionResult.SUCCESS
        }
        return InteractionResult.PASS
    }
}