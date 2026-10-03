package miyucomics.hexical.features.transmuting

import miyucomics.hexical.inits.HexicalRecipes
import net.minecraft.core.HolderLookup
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.item.crafting.SingleRecipeInput
import net.minecraft.world.level.Level

class TransmutingRecipe(
    val id: ResourceLocation,
    val input: Ingredient,
    val cost: Long,
    val output: List<ItemStack>
) : Recipe<SingleRecipeInput> {

    override fun matches(input: SingleRecipeInput, level: Level): Boolean =
        this.input.test(input.getItem(0))

    override fun assemble(input: SingleRecipeInput, registries: HolderLookup.Provider): ItemStack =
        output[0].copy()

    override fun getResultItem(registries: HolderLookup.Provider): ItemStack =
        output[0].copy()

    override fun canCraftInDimensions(width: Int, height: Int) = true

    // ✅ ИСПРАВЛЕНО: Берем тип и сериализатор из реестра
    override fun getType(): RecipeType<*> = HexicalRecipes.TRANSMUTING_TYPE.get()
    override fun getSerializer(): RecipeSerializer<*> = HexicalRecipes.TRANSMUTING_SERIALIZER.get()

    // Внутренний class Type удален, он больше не нужен
}