package miyucomics.hexical.datagen.providers

import com.google.gson.JsonObject
import miyucomics.hexical.features.dyes.DyeOption
import net.minecraft.core.HolderLookup
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import java.util.concurrent.CompletableFuture

object DyeingProvider {
    fun init() {
        itemPatterns.forEach { (group, pattern) ->
            val json = JsonObject().apply {
                val collection = DyeOption.entries.mapNotNull { resolve(BuiltInRegistries.ITEM, pattern, it) }.associate { (dye, identifier) ->
                    addProperty(dye.toString(), identifier)
                    DyeOption.entries[dye] to identifier
                }
                generateItemPage(group, collection)
            }
            generateFiles(json, "dyeing/item", group)
        }

        blockPatterns.forEach { (group, pattern) ->
            val json = JsonObject().apply {
                val collection = DyeOption.entries.mapNotNull { resolve(BuiltInRegistries.BLOCK, pattern, it) }.associate { (dye, identifier) ->
                    addProperty(dye.toString(), identifier)
                    DyeOption.entries[dye] to identifier
                }
                generateBlockPage(group, collection)
            }
            generateFiles(json, "dyeing/block", group)
        }
    }

    val blockPatterns = mapOf(
        "bed" to "minecraft:{color}bed",
        "candle" to "minecraft:{color}candle",
        "candle_cake" to "minecraft:{color}candle_cake",
        "carpet" to "minecraft:{color}carpet",
        "concrete" to "minecraft:{color}concrete",
        "concrete_powder" to "minecraft:{color}concrete_powder",
        "glazed_terracotta" to "minecraft:{color}glazed_terracotta",
        "sand" to "minecraft:{color}sand",
        "sandstone" to "minecraft:{color}sandstone",
        "cut_sandstone" to "minecraft:cut_{color}sandstone",
        "smooth_sandstone" to "minecraft:smooth_{color}sandstone",
        "chiseled_sandstone" to "minecraft:chiseled_{color}sandstone",
        "sandstone_slab" to "minecraft:{color}sandstone_slab",
        "cut_sandstone_slab" to "minecraft:cut_{color}sandstone_slab",
        "smooth_sandstone_slab" to "minecraft:smooth_{color}sandstone_slab",
        "sandstone_stairs" to "minecraft:{color}sandstone_stairs",
        "smooth_sandstone_stairs" to "minecraft:smooth_{color}sandstone_stairs",
        "sandstone_walls" to "minecraft:{color}sandstone_walls",
        "shulker_box" to "minecraft:{color}shulker_box",
        "stained_glass" to "minecraft:{color}stained_glass",
        "stained_glass_pane" to "minecraft:{color}stained_glass_pane",
        "terracotta" to "minecraft:{color}terracotta",
        "tulip" to "minecraft:{color}tulip",
        "wool" to "minecraft:{color}wool"
    )

    val itemPatterns = mapOf(
        "dye" to "minecraft:{color}dye",
        "pigment" to "hexcasting:dye_colorizer_{color}"
    )

    // ✅ ИСПРАВЛЕНО: Добавлена защита от пробелов и невалидных символов
    private fun <T : Any> resolve(registry: Registry<T>, pattern: String, dye: DyeOption): Pair<Int, String>? {
        val replacement = dye.replacement.trim() // Убираем случайные пробелы по краям
        val id1 = pattern.replace("{color}", replacement)
        val id2 = pattern.replace("{color}", replacement + "_")

        // Regex для проверки валидных символов ResourceLocation (только a-z, 0-9, /, ., _, -)
        val validPathRegex = "^[a-z0-9/._-]+$".toRegex()

        return try {
            when {
                validPathRegex.matches(id1) && registry.get(ResourceKey.create(registry.key(), ResourceLocation.parse(id1))) != null ->
                    dye.ordinal to id1
                validPathRegex.matches(id2) && registry.get(ResourceKey.create(registry.key(), ResourceLocation.parse(id2))) != null ->
                    dye.ordinal to id2
                else -> null
            }
        } catch (e: Exception) {
            // Если ResourceLocation все равно невалиден, просто пропускаем этот краситель
            null
        }
    }

    val tasks = mutableListOf<(CachedOutput, PackOutput, CompletableFuture<HolderLookup.Provider>) -> CompletableFuture<*>>()

    private fun generateFiles(json: JsonObject, path: String, name: String) {
        tasks.add { cache, output, _ ->
            // ✅ ИСПРАВЛЕНО: Правильная структура датапака: data/<namespace>/<path>/<name>.json
            // Раньше namespace "hexical" добавлялся после path, что создавало неверную структуру папок
            val resourcePath = output.getOutputFolder(PackOutput.Target.DATA_PACK)
                .resolve("hexical") // Сначала namespace
                .resolve(path)      // Затем путь (dyeing/item или dyeing/block)
                .resolve("$name.json")

            DataProvider.saveStable(cache, json, resourcePath)
        }
    }

    val patchouliPages = mutableListOf<JsonObject>()

    private fun generateBlockPage(name: String, collection: Map<DyeOption, String>) {
        patchouliPages.add(JsonObject().apply {
            addProperty("type", "hexical:dyeing_block")
            addProperty("text", "hexical.recipe.dyeing.$name.text")
            collection.forEach { (dye, item) -> addProperty(dye.replacement.trim().ifEmpty { "uncolored" }, item) }
        })
    }

    private fun generateItemPage(name: String, collection: Map<DyeOption, String>) {
        patchouliPages.add(JsonObject().apply {
            addProperty("type", "hexical:dyeing_item")
            addProperty("text", "hexical.recipe.dyeing.$name.text")
            collection.forEach { (dye, item) -> addProperty(dye.replacement.trim().ifEmpty { "uncolored" }, item) }
        })
    }
}