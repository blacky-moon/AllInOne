package miyucomics.hexical.features.periwinkle

import miyucomics.hexical.HexicalMain
import miyucomics.hexical.features.periwinkle.enchantments.AnalyzingEnchantment
import miyucomics.hexical.features.periwinkle.enchantments.LeiEnchantmentLogic
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.enchantment.ItemEnchantments
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.ItemAttributeModifierEvent

@EventBusSubscriber(modid = HexicalMain.MOD_ID)
object LeiEnchantmentsHook {
    private val logics: List<LeiEnchantmentLogic> = listOf(AnalyzingEnchantment)

    @JvmStatic
    @SubscribeEvent
    fun onItemAttributeModifier(event: ItemAttributeModifierEvent) {
        val enchantments: ItemEnchantments = event.itemStack.get(DataComponents.ENCHANTMENTS) ?: return

        logics.forEach { logic ->
            val level = enchantments.keySet().firstOrNull { holder ->
                holder.unwrapKey().map { it.location() == HexicalMain.id(logic.enchantmentId) }.orElse(false)
            }?.let { enchantments.getLevel(it) } ?: 0

            if (level > 0) logic.modifyAttributes(event, level)
        }
    }
}