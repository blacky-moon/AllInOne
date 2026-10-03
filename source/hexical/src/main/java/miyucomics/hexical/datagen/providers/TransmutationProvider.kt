package miyucomics.hexical.datagen.providers

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.common.lib.HexItems
import com.google.gson.JsonObject
import miyucomics.hexical.features.transmuting.TransmutingRecipe
import miyucomics.hexical.inits.HexicalItems
import net.minecraft.core.HolderLookup
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient

object TransmutationProvider {
    val recipePages = mutableListOf<JsonObject>()

    fun init() {
        makePage("enchant_apple")
        makePage("cry_obsidian")
        makePage("uncry_obsidian")
        makePage("thoughtknot")
        makePage("unthoughtknot")
        makePage("fossil_fuel")
        makePage("renewable_fuel")

        // Добавляем страницы для driver_dot
        makePage("iron_driver_dot")
        makePage("gold_driver_dot")
        makePage("copper_driver_dot")
        makePage("netherite_driver_dot")
    }

    private fun makePage(name: String) {
        recipePages.add(JsonObject().apply {
            addProperty("type", "hexcasting:transmuting")
            addProperty("recipe", "hexical:transmuting/$name")
            addProperty("title", "hexical.recipe.$name.header")
            addProperty("text", "hexical.recipe.$name.text")
        })
    }

    fun buildRecipes(output: RecipeOutput, registries: HolderLookup.Provider) {
        makeTransmutation(output, "enchant_apple", Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE, MediaConstants.CRYSTAL_UNIT * 10)
        makeTransmutation(output, "cry_obsidian", Items.OBSIDIAN, Items.CRYING_OBSIDIAN, MediaConstants.SHARD_UNIT)
        makeTransmutation(output, "uncry_obsidian", Items.CRYING_OBSIDIAN, Items.OBSIDIAN, -2 * MediaConstants.DUST_UNIT)

        makeTransmutation(output, "thoughtknot", Items.STRING, HexItems.THOUGHT_KNOT, (0.75 * MediaConstants.DUST_UNIT).toLong())
        makeTransmutation(output, "unthoughtknot", HexItems.THOUGHT_KNOT, Items.STRING, MediaConstants.DUST_UNIT / -2)

        makeTransmutation(output, "fossil_fuel", Items.CHARCOAL, Items.COAL, MediaConstants.DUST_UNIT / 2)
        makeTransmutation(output, "renewable_fuel", Items.COAL, Items.CHARCOAL, MediaConstants.DUST_UNIT / -4)

        // ✅ ДОБАВЛЕНО: Генерация рецептов для driver_dot
        makeTransmutation(output, "iron_driver_dot", Items.IRON_INGOT, HexicalItems.IRON_DRIVER_DOT_ITEM.get(), 100000L, 3)
        makeTransmutation(output, "gold_driver_dot", Items.GOLD_INGOT, HexicalItems.GOLD_DRIVER_DOT_ITEM.get(), 100000L, 3)
        makeTransmutation(output, "copper_driver_dot", Items.COPPER_INGOT, HexicalItems.COPPER_DRIVER_DOT_ITEM.get(), 100000L, 3)
        makeTransmutation(output, "netherite_driver_dot", Items.NETHERITE_INGOT, HexicalItems.NETHERITE_DRIVER_DOT_ITEM.get(), 100000L, 3)
    }

    fun makeTransmutation(output: RecipeOutput, name: String, original: Item, new: Item, cost: Long, count: Int = 1) {
        val id = ResourceLocation.fromNamespaceAndPath("hexical", "transmuting/$name")
        val input = Ingredient.of(original)
        val outputs = listOf(ItemStack(new, count))

        val recipe = TransmutingRecipe(id, input, cost, outputs)
        output.accept(id, recipe, null)
    }
}