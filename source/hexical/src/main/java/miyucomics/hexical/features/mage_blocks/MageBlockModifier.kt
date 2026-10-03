package miyucomics.hexical.features.mage_blocks

import com.mojang.datafixers.util.Pair as DfuPair
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

interface MageBlockModifier {
    val type: MageBlockModifierType<*>

    fun serialize(registries: HolderLookup.Provider): Tag

    fun getScryingLens(): DfuPair<ItemStack, Component>? = null

    fun tick(world: Level, pos: BlockPos, state: BlockState) {}
}