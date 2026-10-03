package miyucomics.hexical.features.periwinkle.enchantments

import net.neoforged.neoforge.event.ItemAttributeModifierEvent

/**
 * Реализуй этот интерфейс объектом-синглтоном для каждого кастомного
 * Lei-зачарования, чья логика не выражается декларативным JSON.
 * Регистрация самого Enchantment всё равно идёт через datapack JSON
 * (data/hexverse/enchantment/<name>.json), этот интерфейс только
 * подключает императивную логику модификации атрибутов.
 */
interface LeiEnchantmentLogic {
    /** ResourceKey зачарования, как оно объявлено в data/.../enchantment/<id>.json */
    val enchantmentId: String

    fun modifyAttributes(event: ItemAttributeModifierEvent, level: Int)
}