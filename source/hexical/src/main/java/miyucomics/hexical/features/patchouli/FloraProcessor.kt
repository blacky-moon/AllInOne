package miyucomics.hexical.features.patchouli

import at.petrak.hexcasting.common.items.magic.ItemMediaHolder
import miyucomics.hexical.features.flora.ConjureFloraRecipe
import miyucomics.hexical.misc.TextUtilities
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import vazkii.patchouli.api.IComponentProcessor
import vazkii.patchouli.api.IVariable
import vazkii.patchouli.api.IVariableProvider

@Suppress("unused")
class FloraProcessor : IComponentProcessor {
    lateinit var recipe: ConjureFloraRecipe

    override fun setup(world: Level, vars: IVariableProvider) {
        val registries = world.registryAccess()
        val recipeId = vars.get("recipe", registries).asString()
        val id = ResourceLocation.parse(recipeId)
        this.recipe = world.recipeManager.getAllRecipesFor(ConjureFloraRecipe.TYPE)
            .firstOrNull { it.id == id }?.value ?: return
    }

    override fun process(world: Level, key: String): IVariable {
        val registries = world.registryAccess()
        return when (key) {
            "block" -> IVariable.from(ItemStack(BuiltInRegistries.ITEM.get(BuiltInRegistries.BLOCK.getKey(recipe.state.block))), registries)
            "cost" -> IVariable.from(TextUtilities.getCostText(recipe.cost).setStyle(Style.EMPTY.withColor(ItemMediaHolder.HEX_COLOR)), registries)
            else -> IVariable.from(ItemStack.EMPTY, registries)
        }
    }
}