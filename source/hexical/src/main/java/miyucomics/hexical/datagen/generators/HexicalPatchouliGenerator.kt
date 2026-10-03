package miyucomics.hexical.datagen.generators

import com.google.gson.JsonObject
import miyucomics.hexical.datagen.entry
import miyucomics.hexical.datagen.providers.DyeingProvider
import miyucomics.hexical.datagen.providers.FloraProvider
import miyucomics.hexical.datagen.providers.TransmutationProvider
import miyucomics.hexical.inits.HexicalBlocks
import miyucomics.hexical.inits.HexicalItems
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Items
import java.util.concurrent.CompletableFuture

class HexicalPatchouliGenerator(
    private val packOutput: PackOutput
) : DataProvider {

    override fun getName(): String = "Hexical Patchouli Pages"

    override fun run(cache: CachedOutput): CompletableFuture<*> = CompletableFuture.allOf(
        generateCurioPages(cache),
        generateDyeingPages(cache),
        generateFloraPages(cache),
        generateMediaJarPages(cache)
    )

    private fun generateCurioPages(cache: CachedOutput): CompletableFuture<*> {
        return saveEntry(cache, entry("hexical.page.curios.title", HexicalItems.CURIO_STAFF.get()) {
            sortnum = 10
            textPage("hexical.page.curios.0")
            HexicalItems.CURIO_NAMES.mapIndexed { i, name -> HexicalItems.CURIOS[i].get() to name }.forEach { (curio, name) ->
                spotlightPage(curio, "hexical.page.curio_$name.summary")
            }
        }, "items", "curios")
    }

    private fun generateDyeingPages(cache: CachedOutput): CompletableFuture<*> {
        return saveEntry(cache, entry("hexical.page.dyes.title", Items.RED_DYE) {
            sortnum = 2
            category = "hexcasting:patterns/spells"
            textPage("hexical.page.dyes.0")
            patternPage("hexical:get_dye", "id/vector/entity", "dye/null", "hexical.page.get_dye.summary")
            patternPage("hexical:dye", "vector/entity, dye", "", "hexical.page.dye.summary")
            extraPages(DyeingProvider.patchouliPages)
        }, "patterns/spells", "dyeing")
    }

    private fun generateFloraPages(cache: CachedOutput): CompletableFuture<*> {
        return saveEntry(cache, entry("hexical.page.flora.title", Items.POPPY) {
            sortnum = 10
            category = "hexcasting:patterns/spells"
            patternPage("hexical:conjure_flora", "vector, identifier", "", "hexical.page.conjure_flora.summary")
            textPage("hexical.page.conjure_flora.description")
            textPage("hexical.page.conjure_flora.0")
            extraPages(FloraProvider.recipePages)
        }, "patterns/spells", "conjure_flora")
    }

    private fun generateMediaJarPages(cache: CachedOutput): CompletableFuture<*> {
        return saveEntry(cache, entry("hexical.page.media_jar.title", HexicalItems.MEDIA_JAR_ITEM.get()) {
            sortnum = 4
            textPage("hexical.page.media_jar.0")
            craftPage("hexical:media_jar", "hexical.page.media_jar.description")
            textPage("hexical.page.media_jar.1")
            extraPages(TransmutationProvider.recipePages)
        }, "items", "media_jar")
    }

    private fun saveEntry(cache: CachedOutput, json: JsonObject, path: String, name: String): CompletableFuture<*> {
        val resourcePath = packOutput.getOutputFolder(PackOutput.Target.RESOURCE_PACK)
            .resolve("patchouli_books/thehexbook/en_us/entries/$path")
            .resolve("$name.json")
        return DataProvider.saveStable(cache, json, resourcePath)
    }
}