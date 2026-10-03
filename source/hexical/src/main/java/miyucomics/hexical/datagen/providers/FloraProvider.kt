package miyucomics.hexical.datagen.providers

import com.google.gson.JsonObject
import miyucomics.hexical.features.flora.ConjureFloraRecipe
import miyucomics.hexical.inits.HexicalBlocks
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks

object FloraProvider {
    val recipePages = mutableListOf<JsonObject>()

    fun init() {
        defaultRecipes.forEach { (flower, cost) ->
            val name = BuiltInRegistries.BLOCK.getKey(flower).path
            recipePages.add(JsonObject().apply {
                addProperty("type", "hexcasting:flora")
                addProperty("recipe", "hexical:flora/$name")
                addProperty("title", flower.descriptionId)
                addProperty("text", "hexical.page.flora.$name")
            })
        }
    }

    fun buildRecipes(output: RecipeOutput, registries: HolderLookup.Provider) {
        defaultRecipes.forEach { (flower, cost) ->
            val name = BuiltInRegistries.BLOCK.getKey(flower).path
            val id = ResourceLocation.fromNamespaceAndPath("hexical", "flora/$name")
            val recipe = ConjureFloraRecipe(
                Ingredient.of(flower.asItem()),
                flower.defaultBlockState(),
                cost
            )
            output.accept(id, recipe, null)
        }
    }

    val defaultRecipes: Map<Block, Long> = mapOf<Block, Long>(
        Blocks.PINK_PETALS to 2500L,
        Blocks.SHORT_GRASS to 3000L,
        Blocks.TALL_GRASS to 3000L,
        Blocks.FERN to 3500L,
        Blocks.LARGE_FERN to 3500L,
        Blocks.NETHER_SPROUTS to 3500L,
        Blocks.SEAGRASS to 3500L,
        Blocks.DANDELION to 5000L,
        Blocks.POPPY to 5000L,
        Blocks.BLUE_ORCHID to 5000L,
        Blocks.ALLIUM to 5000L,
        Blocks.AZURE_BLUET to 5000L,
        Blocks.RED_TULIP to 5000L,
        Blocks.PINK_TULIP to 5000L,
        Blocks.ORANGE_TULIP to 5000L,
        Blocks.WHITE_TULIP to 5000L,
        Blocks.OXEYE_DAISY to 5000L,
        Blocks.CORNFLOWER to 5000L,
        Blocks.LILY_OF_THE_VALLEY to 5000L,
        Blocks.SUNFLOWER to 5000L,
        Blocks.ROSE_BUSH to 5000L,
        Blocks.PEONY to 5000L,
        Blocks.LILAC to 5000L,
        Blocks.KELP to 5000L,
        Blocks.RED_MUSHROOM to 6000L,
        Blocks.BROWN_MUSHROOM to 6000L,
        Blocks.CRIMSON_FUNGUS to 6000L,
        Blocks.WARPED_FUNGUS to 6000L,
        Blocks.CRIMSON_ROOTS to 6000L,
        Blocks.WARPED_ROOTS to 6000L,
        Blocks.SEA_PICKLE to 6000L,
        Blocks.SMALL_DRIPLEAF to 7000L,
        Blocks.BIG_DRIPLEAF to 7000L,
        Blocks.DEAD_BUSH to 7500L,
        Blocks.NETHER_WART to 7500L,
        HexicalBlocks.PERIWINKLE_FLOWER.get() to 10000L,
        Blocks.TORCHFLOWER to 15000L,
        Blocks.PITCHER_PLANT to 15000L,
        Blocks.BAMBOO to 50000L,
        Blocks.ACACIA_SAPLING to 50000L,
        Blocks.BIRCH_SAPLING to 50000L,
        Blocks.CHERRY_SAPLING to 50000L,
        Blocks.DARK_OAK_SAPLING to 50000L,
        Blocks.JUNGLE_SAPLING to 50000L,
        Blocks.OAK_SAPLING to 50000L,
        Blocks.SPRUCE_SAPLING to 50000L,
        Blocks.MANGROVE_PROPAGULE to 50000L,
        Blocks.SUGAR_CANE to 50000L,
        Blocks.WITHER_ROSE to 100000L,
        Blocks.CACTUS to 100000L
    )
}
