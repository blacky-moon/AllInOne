package miyucomics.hexical.features.dyes

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import miyucomics.hexical.misc.InitHook
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimplePreparableReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.AddReloadListenerEvent
import java.io.InputStreamReader

object DyeingUtils : InitHook() {
    val blockDyeLookup = mutableMapOf<net.minecraft.world.level.block.Block, DyeOption>()
    val itemDyeLookup = mutableMapOf<net.minecraft.world.item.Item, DyeOption>()
    val blockConvertLookup = mutableMapOf<net.minecraft.world.level.block.Block, Map<DyeOption, net.minecraft.world.level.block.Block>>()
    val itemConvertLookup = mutableMapOf<net.minecraft.world.item.Item, Map<DyeOption, net.minecraft.world.item.Item>>()

    fun getDye(block: net.minecraft.world.level.block.Block) = blockDyeLookup[block]
    fun getDye(item: net.minecraft.world.item.Item) = itemDyeLookup[item]

    fun getResult(block: net.minecraft.world.level.block.Block, dye: DyeOption): net.minecraft.world.level.block.Block? =
        blockConvertLookup[block]?.get(dye)

    fun getResult(stack: ItemStack, dye: DyeOption): ItemStack? {
        val newItem = itemConvertLookup[stack.item]?.get(dye) ?: return null
        val newStack = ItemStack(newItem, stack.count)
        // В 1.21.1 копируем Data Components через applyComponents(DataComponentPatch)
        newStack.applyComponents(stack.componentsPatch)
        return newStack
    }

    // В DyeingUtils.kt
    override fun init() {
        // AddReloadListenerEvent принадлежит NeoForge EVENT BUS, а не Mod Bus!
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener<net.neoforged.neoforge.event.AddReloadListenerEvent> { event ->
            event.addListener(DyeReloadListener())
        }
    }
    private class DyeReloadListener : SimplePreparableReloadListener<Unit>() {
        override fun prepare(manager: ResourceManager, profiler: ProfilerFiller) {}

        override fun apply(unit: Unit, manager: ResourceManager, profiler: ProfilerFiller) {
            blockDyeLookup.clear()
            blockConvertLookup.clear()
            // В 1.21.1 listResources возвращает Map<ResourceLocation, Resource>
            manager.listResources("dyeing/block") { id -> id.path.endsWith(".json") }.forEach { (_, resource) ->
                // ИСПРАВЛЕНО: open() заменен на openAsReader() в Mojang mappings 1.21.1
                resource.openAsReader().use { reader ->
                    load(reader, BuiltInRegistries.BLOCK, blockDyeLookup, blockConvertLookup)
                }
            }

            itemDyeLookup.clear()
            itemConvertLookup.clear()
            manager.listResources("dyeing/item") { id -> id.path.endsWith(".json") }.forEach { (_, resource) ->
                resource.openAsReader().use { reader ->
                    load(reader, BuiltInRegistries.ITEM, itemDyeLookup, itemConvertLookup)
                }
            }
        }
    }

    private fun <T : Any> load(
        reader: java.io.Reader,
        registry: Registry<T>,
        dyeLookup: MutableMap<T, DyeOption>,
        convertLookup: MutableMap<T, Map<DyeOption, T>>
    ) {
        val json = JsonParser.parseReader(reader) as JsonObject
        val allMatches = json.entrySet()
            .filter { registry.containsKey(ResourceLocation.parse(it.key)) }
            .associate { (id, dyeElement) ->
                val obj = registry.get(ResourceLocation.parse(id))!!
                val dye = DyeOption.entries[dyeElement.asInt]
                dyeLookup[obj] = dye
                dye to obj
            }

        allMatches.values.forEach { convertLookup[it] = allMatches }
    }
}