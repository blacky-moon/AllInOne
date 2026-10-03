package miyucomics.hexical.features.sentinel_beds

import miyucomics.hexical.HexicalMain
import miyucomics.hexical.inits.HexicalBlocks
import miyucomics.hexical.misc.InitHook
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.ai.village.poi.PoiType
import net.neoforged.neoforge.registries.RegisterEvent

object SentinelBedPoi : InitHook() {
    // Ленивая инициализация – блок будет получен только при первом обращении, когда реестр уже зарегистрирован
    val SENTINEL_BED_POI: PoiType by lazy {
        PoiType(
            HexicalBlocks.SENTINEL_BED_BLOCK.get().stateDefinition.possibleStates.toSet(),
            0, 1
        )
    }

    override fun init() {

    }

    fun registerAll(helper: RegisterEvent.RegisterHelper<PoiType>) {
        helper.register(HexicalMain.id("sentinel_bed"), SENTINEL_BED_POI)
    }

    fun isSentinelBed(world: ServerLevel, pos: BlockPos): Boolean {
        val registry = world.registryAccess().registryOrThrow(Registries.POINT_OF_INTEREST_TYPE)
        val holder = registry.getHolder(HexicalMain.id("sentinel_bed"))
        return world.poiManager.getType(pos).filter { it == holder.get() }.isPresent
    }
}