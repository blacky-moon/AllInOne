package miyucomics.hexical.features.rotate

import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.BlockState

interface BlockRotationHandler {
    fun canHandle(state: BlockState, direction: Direction): Boolean
    fun handle(state: BlockState, direction: Direction): BlockState
}