package miyucomics.hexical.features.emi

import dev.emi.emi.api.EmiEntrypoint // <-- ВАЖНЫЙ ИМПОРТ
import dev.emi.emi.api.EmiPlugin
import dev.emi.emi.api.EmiRegistry
import dev.emi.emi.api.recipe.EmiRecipeCategory
import dev.emi.emi.api.render.EmiTexture
import dev.emi.emi.api.stack.EmiStack
import miyucomics.hexical.HexicalMain
import miyucomics.hexical.features.media_jar.MediaJarBlock
import miyucomics.hexical.features.transmuting.TransmutingRecipe
import miyucomics.hexical.inits.HexicalItems
import miyucomics.hexical.inits.HexicalRecipes
import miyucomics.hexical.inits.HexicalComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeHolder
import net.minecraft.world.item.crafting.SingleRecipeInput

// ✅ ИСПРАВЛЕНО: Добавлена аннотация, чтобы EMI нашел плагин в NeoForge
@EmiEntrypoint
class HexicalEmi : EmiPlugin {
    override fun register(registry: EmiRegistry) {
        registry.addCategory(TRANSMUTING_CATEGORY)
        registry.addWorkstation(TRANSMUTING_CATEGORY, TRANSMUTING_ICON)

        val recipes = registry.recipeManager.getAllRecipesFor<SingleRecipeInput, TransmutingRecipe>(HexicalRecipes.TRANSMUTING_TYPE.get())
        for (recipe: RecipeHolder<TransmutingRecipe> in recipes) {
            registry.addRecipe(TransmutingEmi(recipe.value()))
        }
    }

    companion object {
        val TRANSMUTING_ICON: EmiStack = EmiStack.of(
            ItemStack(HexicalItems.MEDIA_JAR_ITEM.get()).apply {
                this.set(HexicalComponents.MEDIA_HOLDER.get(), MediaJarBlock.MAX_CAPACITY)
            }
        )

        val TRANSMUTING_CATEGORY = EmiRecipeCategory(
            HexicalMain.id("transmuting"),
            TRANSMUTING_ICON,
            EmiTexture(HexicalMain.id("textures/gui/transmuting_simplified.png"), 0, 0, 16, 16, 16, 16, 16, 16)
        )
    }
}