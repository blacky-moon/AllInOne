package miyucomics.hexical.features.patchouli

import at.petrak.hexcasting.common.items.magic.ItemMediaHolder
import miyucomics.hexical.features.transmuting.TransmutingRecipe
import miyucomics.hexical.inits.HexicalRecipes // <-- ВАЖНЫЙ ИМПОРТ
import miyucomics.hexical.misc.TextUtilities
import net.minecraft.network.chat.Style
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.item.crafting.SingleRecipeInput
import net.minecraft.world.level.Level
import vazkii.patchouli.api.IComponentProcessor
import vazkii.patchouli.api.IVariable
import vazkii.patchouli.api.IVariableProvider

@Suppress("unused")
class TransmutingProcessor : IComponentProcessor {
    private var recipe: TransmutingRecipe? = null

    override fun setup(world: Level, vars: IVariableProvider) {
        val registries = world.registryAccess()
        val recipeId = vars.get("recipe", registries).asString()

        // ✅ ИСПРАВЛЕНО: Берем тип из реестра и приводим к нужному дженерику
        @Suppress("UNCHECKED_CAST")
        val type = HexicalRecipes.TRANSMUTING_TYPE.get() as RecipeType<TransmutingRecipe>

        // ✅ ИСПРАВЛЕНО: Явно указываем дженерики для Kotlin и используем it.id / it.value (так как RecipeHolder в 1.21 - это record)
        this.recipe = world.recipeManager
            .getAllRecipesFor<SingleRecipeInput, TransmutingRecipe>(type)
            .firstOrNull { it.id.toString() == recipeId }
            ?.value
    }

    override fun process(world: Level, key: String): IVariable? {
        val recipe = this.recipe ?: return null
        val registries = world.registryAccess()

        if (key.length > 6 && key.take(6) == "output") {
            val index = key.substring(6).toInt()
            if (index < recipe.output.size)
                return IVariable.from(recipe.output[index], registries)
            return IVariable.from(ItemStack.EMPTY, registries)
        }

        return when (key) {
            "input" -> IVariable.from(recipe.input, registries)
            "cost" -> IVariable.from(TextUtilities.getCostText(recipe.cost).setStyle(Style.EMPTY.withColor(ItemMediaHolder.HEX_COLOR)), registries)
            else -> null
        }
    }
}