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
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.AddReloadListenerEvent

object PrestidigitationBlockBooleans {
    private val map: MutableMap<Block, BooleanProperty> = mutableMapOf()

    private val reloadListener = object : SimplePreparableReloadListener<Unit>() {
        override fun prepare(manager: ResourceManager, profiler: ProfilerFiller) {}

        override fun apply(obj: Unit, manager: ResourceManager, profiler: ProfilerFiller) {
            map.clear()
            manager.listResources("prestidigitation") { path -> path.path.endsWith("block_booleans.json") }
                .keys.forEach { path ->
                    val resource = manager.getResource(path).orElseThrow()
                    resource.openAsReader().use { reader ->
                        val json = JsonParser.parseReader(reader) as JsonObject
                        json.entrySet().forEach {
                            val id = ResourceLocation.parse(it.key)
                            if (BuiltInRegistries.BLOCK.containsKey(id)) {
                                map[BuiltInRegistries.BLOCK.get(id)] =
                                    BooleanProperty.create(it.value.asString)
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
                val state = getBlockState(env, pos)
                val property = map[state.block] ?: return
                setBlockState(env, pos, state.setValue(property, !state.getValue(property)))
            }
        })
    }

    @JvmStatic
    fun onAddReloadListener(event: AddReloadListenerEvent) {
        event.addListener(reloadListener)
    }
}
