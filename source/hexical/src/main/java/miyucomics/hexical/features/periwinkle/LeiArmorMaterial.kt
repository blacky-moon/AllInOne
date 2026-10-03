package miyucomics.hexical.features.periwinkle

import net.minecraft.Util
import net.minecraft.core.Holder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.ArmorMaterial
import net.minecraft.world.item.crafting.Ingredient
import miyucomics.hexical.HexicalMain
import java.util.EnumMap
import java.util.function.Supplier

object LeiArmorMaterial {
    val INSTANCE: ArmorMaterial = create()

    private fun create(): ArmorMaterial {
        val defenseMap = Util.make(
            EnumMap<ArmorItem.Type, Int>(ArmorItem.Type::class.java)
        ) { map ->
            map[ArmorItem.Type.HELMET] = 0
            map[ArmorItem.Type.CHESTPLATE] = 0
            map[ArmorItem.Type.LEGGINGS] = 0
            map[ArmorItem.Type.BOOTS] = 0
        }

        val texture = HexicalMain.id("lei_armor")
        val layers = listOf(ArmorMaterial.Layer(texture))
        val repairIngredient = Supplier { Ingredient.EMPTY }
        val equipSound: Holder<SoundEvent> = BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.AMETHYST_BLOCK_CHIME)

        return ArmorMaterial(
            defenseMap,
            100,
            equipSound,
            repairIngredient,
            layers,
            0.0f,
            0.0f
        )
    }
}