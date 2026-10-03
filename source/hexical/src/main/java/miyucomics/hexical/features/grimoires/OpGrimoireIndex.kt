package miyucomics.hexical.features.grimoires

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.iota.PatternIota
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem
import miyucomics.hexical.inits.HexicalItems
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData
import net.minecraft.nbt.CompoundTag

object OpGrimoireIndex : ConstMediaAction {
    override val argc = 0
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val itemInfo = env.getHeldItemToOperateOn { stack -> stack.`is`(HexicalItems.GRIMOIRE_ITEM.get()) }
        if (itemInfo == null)
            throw MishapBadOffhandItem.of(null, "grimoire")

        val stack = itemInfo.stack
        populateGrimoireMetadata(stack)

        val customData = stack.get(DataComponents.CUSTOM_DATA)
        val tag = customData?.copyTag() ?: CompoundTag()
        val metadata = tag.getCompound("metadata")

        val result = mutableListOf<PatternIota>()
        for (pattern in metadata.allKeys) {
            val compound = metadata.getCompound(pattern)
            if (compound.contains("direction")) {
                result.add(
                    PatternIota(
                        HexPattern.fromAngles(
                            pattern,
                            HexDir.entries[compound.getInt("direction")]
                        )
                    )
                )
            }
        }
        return listOf(ListIota(result.toList()))
    }

    fun populateGrimoireMetadata(grimoire: ItemStack) {
        val customData = grimoire.get(DataComponents.CUSTOM_DATA)
        val tag = customData?.copyTag() ?: CompoundTag()

        if (tag.contains("metadata")) return

        val metadata = CompoundTag()
        if (tag.contains("expansions")) {
            val expansions = tag.getCompound("expansions")
            for (key in expansions.allKeys) {
                val data = CompoundTag()
                data.putInt("direction", HexDir.EAST.ordinal)
                metadata.put(key, data)
            }
        }
        tag.put("metadata", metadata)
        grimoire.set(DataComponents.CUSTOM_DATA, CustomData.of(tag))
    }
}