package miyucomics.hexical.features.pedestal

import at.petrak.hexcasting.api.block.circle.BlockCircleComponent
import at.petrak.hexcasting.api.casting.circles.ICircleComponent.ControlFlow
import at.petrak.hexcasting.api.casting.eval.env.CircleCastEnv
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.Containers
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.SimpleWaterloggedBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.level.pathfinder.PathComputationType
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import java.util.EnumMap
import java.util.EnumSet

class PedestalBlock(properties: Properties) : BlockCircleComponent(properties.strength(4f, 4f)), EntityBlock, SimpleWaterloggedBlock {
    init {
        registerDefaultState(
            stateDefinition.any()
                .setValue(ENERGIZED, false) // Убедитесь, что ENERGIZED существует в BlockCircleComponent
                .setValue(FACING, Direction.NORTH)
                .setValue(BlockStateProperties.WATERLOGGED, false)
        )
    }

    override fun setPlacedBy(level: Level, pos: BlockPos, state: BlockState, placer: LivingEntity?, stack: ItemStack) {
        super.setPlacedBy(level, pos, state, placer, stack)
        (level.getBlockEntity(pos) as? PedestalBlockEntity)?.onBlockPlace()
    }

    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, moved: Boolean) {
        if (state.`is`(newState.block)) return
        val pedestal = level.getBlockEntity(pos)
        if (pedestal is PedestalBlockEntity) {
            Containers.dropContents(level, pos, pedestal)
            level.updateNeighbourForOutputSignal(pos, this)
            pedestal.onBlockBreak()
        }
        super.onRemove(state, level, pos, newState, moved)
    }

    override fun isPathfindable(state: BlockState, pathType: PathComputationType): Boolean = false

    // В 1.21+ взаимодействие с блоком разделено на useItemOn (с предметом) и useWithoutItem (пустой рукой)
    override fun useItemOn(
        stack: ItemStack, state: BlockState, level: Level, pos: BlockPos,
        player: Player, hand: InteractionHand, hit: BlockHitResult
    ): ItemInteractionResult {
        val pedestal = level.getBlockEntity(pos)
        if (pedestal is PedestalBlockEntity) {
            pedestal.onUse(player, hand)
            return ItemInteractionResult.sidedSuccess(level.isClientSide)
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
    }

    override fun useWithoutItem(
        state: BlockState, level: Level, pos: BlockPos, player: Player, hit: BlockHitResult
    ): InteractionResult {
        val pedestal = level.getBlockEntity(pos)
        if (pedestal is PedestalBlockEntity) {
            pedestal.onUse(player, InteractionHand.MAIN_HAND)
            return InteractionResult.sidedSuccess(level.isClientSide)
        }
        return InteractionResult.PASS
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder)
        builder.add(FACING, BlockStateProperties.WATERLOGGED)
    }

    // === Методы Hex Casting API ===
    override fun canEnterFromDirection(enterDir: Direction, pos: BlockPos, state: BlockState, level: ServerLevel): Boolean {
        return enterDir != this.normalDir(pos, state, level).opposite
    }

    override fun acceptControlFlow(image: CastingImage, env: CircleCastEnv, enterDir: Direction, pos: BlockPos, state: BlockState, level: ServerLevel): ControlFlow {
        val exits = this.possibleExitDirections(pos, state, level)
        exits.remove(enterDir.opposite)
        return ControlFlow.Continue(
            (level.getBlockEntity(pos) as PedestalBlockEntity).modifyImage(image),
            exits.map { dir -> exitPositionFromDirection(pos, dir) }
        )
    }

    override fun possibleExitDirections(pos: BlockPos, state: BlockState, level: Level): EnumSet<Direction> {
        val exits = EnumSet.allOf(Direction::class.java)
        val normal = this.normalDir(pos, state, level)
        exits.remove(normal)
        exits.remove(normal.opposite)
        return exits
    }

    override fun particleHeight(pos: BlockPos, state: BlockState, level: Level): Float = PedestalBlockEntity.HEIGHT.toFloat()

    override fun normalDir(pos: BlockPos, state: BlockState, level: Level, recursionLeft: Int): Direction = state.getValue(FACING)

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState? {
        val superState = super.getStateForPlacement(ctx) ?: return null
        return superState
            .setValue(FACING, ctx.clickedFace)
            .setValue(BlockStateProperties.WATERLOGGED, ctx.level.getFluidState(ctx.clickedPos).type == Fluids.WATER)
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return SHAPES[state.getValue(FACING)]!!
    }

    override fun getFluidState(state: BlockState): FluidState {
        return if (state.getValue(BlockStateProperties.WATERLOGGED)) Fluids.WATER.getSource(false) else super.getFluidState(state)
    }

    override fun updateShape(state: BlockState, direction: Direction, neighborState: BlockState, level: LevelAccessor, pos: BlockPos, neighborPos: BlockPos): BlockState {
        if (state.getValue(BlockStateProperties.WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level))
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos)
    }

    override fun getAnalogOutputSignal(state: BlockState, level: Level, pos: BlockPos): Int {
        val blockEntity = level.getBlockEntity(pos)
        if (blockEntity !is PedestalBlockEntity) return 0
        return AbstractContainerMenu.getRedstoneSignalFromContainer(blockEntity)
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = PedestalBlockEntity(pos, state)

    override fun <T : BlockEntity> getTicker(level: Level, state: BlockState, type: BlockEntityType<T>): BlockEntityTicker<T> {
        return BlockEntityTicker { level1, _, _, blockEntity ->
            (blockEntity as? PedestalBlockEntity)?.tick(level1)
        }
    }

    companion object {
        val FACING: DirectionProperty = BlockStateProperties.FACING

        val SHAPES: Map<Direction, VoxelShape> = EnumMap<Direction, VoxelShape>(Direction::class.java).apply {
            put(Direction.NORTH, Shapes.box(0.0, 0.0, 0.25, 1.0, 1.0, 1.0))
            put(Direction.SOUTH, Shapes.box(0.0, 0.0, 0.0, 1.0, 1.0, 0.75))
            put(Direction.EAST, Shapes.box(0.0, 0.0, 0.0, 0.75, 1.0, 1.0))
            put(Direction.WEST, Shapes.box(0.25, 0.0, 0.0, 1.0, 1.0, 1.0))
            put(Direction.UP, Shapes.box(0.0, 0.0, 0.0, 1.0, 0.75, 1.0))
            put(Direction.DOWN, Shapes.box(0.0, 0.25, 0.0, 1.0, 1.0, 1.0))
        }
    }
}