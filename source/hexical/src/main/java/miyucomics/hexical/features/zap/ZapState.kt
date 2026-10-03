package miyucomics.hexical.features.zap

import com.mojang.serialization.Codec
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.saveddata.SavedData

class ZapState private constructor(
    private val instances: MutableMap<BlockPos, ZapInstance> = HashMap()
) : SavedData() {

    fun hasMagicalPower(position: BlockPos): Boolean = instances.containsKey(position)

    fun getMagicalPower(position: BlockPos): Int = instances[position]?.power ?: 0

    fun zap(level: ServerLevel, position: BlockPos, power: Int, ticks: Int) {
        instances[position] = ZapInstance(position, power, ticks)
        triggerBlockUpdate(level, position)
        setDirty()
    }

    fun tick(level: ServerLevel) {
        var changed = false
        val iterator = instances.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            val zap = entry.value
            if (zap.time <= 1) {
                iterator.remove()
                triggerBlockUpdate(level, entry.key)
                changed = true
            } else {
                entry.setValue(zap.copy(time = zap.time - 1))
                changed = true
            }
        }
        if (changed) setDirty()
    }

    companion object {
        private val LIST_CODEC: Codec<List<ZapInstance>> = ZapInstance.CODEC.listOf()

        fun factory(): SavedData.Factory<ZapState> {
            return SavedData.Factory(
                { ZapState() },
                { tag: CompoundTag, registries ->
                    val listTag = tag.getList("zaps", Tag.TAG_COMPOUND.toInt())
                    val parsed = LIST_CODEC.parse(NbtOps.INSTANCE, listTag)
                        .resultOrPartial { /* log error if needed */ }
                        .orElse(emptyList())

                    ZapState(parsed.associateBy { it.position }.toMutableMap())
                }
            )
        }

        private fun triggerBlockUpdate(level: ServerLevel, pos: BlockPos) {
            level.updateNeighborsAt(pos, Blocks.AIR)
        }
    }

    override fun save(tag: CompoundTag, registries: net.minecraft.core.HolderLookup.Provider): CompoundTag {
        val encoded = LIST_CODEC.encodeStart(NbtOps.INSTANCE, instances.values.toList())
            .resultOrPartial { /* log error */ }
            .orElseGet { ListTag() }

        tag.put("zaps", encoded)
        return tag
    }
}