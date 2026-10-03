package miyucomics.hexical.features.prestidigitation.handlers

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import miyucomics.hexical.features.prestidigitation.interfaces.PrestidigitationHandler
import miyucomics.hexical.features.prestidigitation.interfaces.PrestidigitationHandlerBlock
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimplePreparableReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.AddReloadListenerEvent

object PrestidigitationBlockTransformations {
    private val map: MutableMap<Block, BlockState> = mutableMapOf()

    private val reloadListener = object : SimplePreparableReloadListener<Unit>() {
        override fun prepare(manager: ResourceManager, profiler: ProfilerFiller) {}

        override fun apply(obj: Unit, manager: ResourceManager, profiler: ProfilerFiller) {
            map.clear()
            manager.listResources("prestidigitation") { path ->
                path.path.endsWith("block_transformations.json")
            }.keys.forEach { path ->
                val resource = manager.getResource(path).orElseThrow()
                resource.openAsReader().use { reader ->
                    val json = JsonParser.parseReader(reader) as JsonObject
                    json.entrySet().forEach {
                        val from = ResourceLocation.parse(it.key)
                        val to = ResourceLocation.parse(it.value.asString)
                        if (BuiltInRegistries.BLOCK.containsKey(from) && BuiltInRegistries.BLOCK.containsKey(to)) {
                            map[BuiltInRegistries.BLOCK.get(from)] =
                                BuiltInRegistries.BLOCK.get(to).defaultBlockState()
                        }
                    }
                }
            }
        }
    }

    fun init(register: (PrestidigitationHandler) -> Unit) {
        NeoForge.EVENT_BUS.addListener(::onAddReloadListener)

        register(object : PrestidigitationHandlerBlock() {
            override fun canAffectBlock(env: CastingEnvironment, pos: BlockPos) =
                map.containsKey(getBlock(env, pos))

            override fun affect(env: CastingEnvironment, pos: BlockPos) {
                val newState = map[getBlock(env, pos)] ?: return
                setBlockState(env, pos, newState)
            }
        })
    }

    @JvmStatic
    fun onAddReloadListener(event: AddReloadListenerEvent) {
        event.addListener(reloadListener)
    }
}
