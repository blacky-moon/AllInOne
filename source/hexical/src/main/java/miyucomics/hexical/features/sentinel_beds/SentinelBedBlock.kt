package miyucomics.hexical.features.sentinel_beds

import com.mojang.serialization.MapCodec
import net.minecraft.world.level.block.DirectionalBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.Mirror
import net.minecraft.world.level.block.Rotation
import net.minecraft.core.Direction
import net.minecraft.world.level.block.Block

class SentinelBedBlock : DirectionalBlock(Properties.ofFullCopy(Blocks.DEEPSLATE_TILES).strength(4f, 6f)) {
    init {
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.SOUTH))
    }

    override fun codec(): MapCodec<out DirectionalBlock> = MapCodec.unit { this }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING)
    }

    override fun mirror(state: BlockState, mirror: Mirror): BlockState =
        state.rotate(mirror.getRotation(state.getValue(FACING)))

    override fun rotate(state: BlockState, rotation: Rotation): BlockState =
        state.setValue(FACING, rotation.rotate(state.getValue(FACING)))

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState =
        this.defaultBlockState().setValue(FACING, Direction.UP)
}