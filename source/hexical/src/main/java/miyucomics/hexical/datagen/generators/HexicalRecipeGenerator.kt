package miyucomics.hexical.datagen.generators

import at.petrak.hexcasting.common.lib.HexItems
import miyucomics.hexical.datagen.providers.FloraProvider
import miyucomics.hexical.datagen.providers.TransmutationProvider
import miyucomics.hexical.inits.HexicalItems
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.data.recipes.SingleItemRecipeBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.Ingredient
import java.util.concurrent.CompletableFuture

class HexicalRecipeGenerator(
    packOutput: PackOutput,
    private val registries: CompletableFuture<HolderLookup.Provider>
) : RecipeProvider(packOutput, registries) {

    override fun buildRecipes(output: RecipeOutput) {
        val lookup = registries.join()

        // ИСПРАВЛЕНО: Используем curio.id.path вместо BuiltInRegistries.ITEM.getKey()
        // Это гарантирует, что каждый curio получит свое уникальное имя файла (например, curio_compass).
        for (curio in HexicalItems.CURIOS) {
            val curioItem = curio.get()
            val itemName = curio.id.path // Берем путь напрямую из DeferredHolder!

            SingleItemRecipeBuilder.stonecutting(
                Ingredient.of(HexItems.CHARGED_AMETHYST),
                RecipeCategory.MISC,
                curioItem,
                1
            )
                .unlockedBy("has_charged_amethyst", has(HexItems.CHARGED_AMETHYST))
                .save(
                    output,
                    ResourceLocation.fromNamespaceAndPath("hexical", "curio/${itemName}_from_stonecutting")
                )
        }

        FloraProvider.buildRecipes(output, lookup)
        TransmutationProvider.buildRecipes(output, lookup)
    }

    // Вспомогательный метод для has
    private fun has(item: net.minecraft.world.item.Item): net.minecraft.advancements.Criterion<net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance> {
        return net.minecraft.advancements.critereon.InventoryChangeTrigger.TriggerInstance.hasItems(item)
    }
}