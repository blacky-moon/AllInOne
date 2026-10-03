package miyucomics.hexical.features.zap

import miyucomics.hexical.HexicalMain
import miyucomics.hexical.misc.InitHook
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.tick.LevelTickEvent

object ZapManager : InitHook(){

    override fun init() {
        NeoForge.EVENT_BUS.addListener<LevelTickEvent.Post> { event ->

            val level = event.level
            if (level is ServerLevel) {
                getPersistentState(level).tick(level)
            }
        }
    }

    fun getPersistentState(level: ServerLevel): ZapState {
        // В 1.21.1 SavedData получаются через dataStorage, а не через getData()
        return level.dataStorage.computeIfAbsent(
            ZapState.factory(),
            "zaps" // Имя файла сохранения (будет сохранен как data/<modid>/zaps.nbt)
        )
    }

    fun zap(level: ServerLevel, position: BlockPos, power: Int, ticks: Int) {
        getPersistentState(level).zap(level, position, power, ticks)
    }

    @JvmStatic
    fun hasMagicalPower(level: ServerLevel, position: BlockPos): Boolean {
        return getPersistentState(level).hasMagicalPower(position)
    }

    @JvmStatic
    fun getMagicalPower(level: ServerLevel, position: BlockPos): Int {
        return getPersistentState(level).getMagicalPower(position)
    }
}