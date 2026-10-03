package miyucomics.hexical.features.hex_candles

import at.petrak.hexcasting.api.pigment.ColorProvider
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.common.particles.ConjureParticleOptions
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.CandleBlock
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.level.material.PushReaction
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import net.minecraft.util.RandomSource

class HexCandleBlock : CandleBlock(Properties.of()
    .mapColor(MapColor.COLOR_PURPLE)
    .noOcclusion()
    .strength(0.1f)
    .sound(SoundType.CANDLE)
    .lightLevel { state -> if (state.getValue(LIT)) 3 else 0 }
    .pushReaction(PushReaction.DESTROY)
), EntityBlock {

    init {
        this.registerDefaultState(
            this.stateDefinition.any()
                .setValue(LIT, false)
                .setValue(WATERLOGGED, false)
                .setValue(CANDLES, 1)
        )
    }

    // Обработка предмета в руке (включая огниво/огненный заряд)
    override fun useItemOn(
        stack: ItemStack,
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hitResult: BlockHitResult
    ): ItemInteractionResult {
        if (player.isShiftKeyDown)
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult)

        if (!state.getValue(LIT)) {
            // Свеча не горит — даём родителю обработать зажигание (огниво и т.д.)
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult)
        }

        // Свеча горит — применяем пигмент
        val candle = level.getBlockEntity(pos) as? HexCandleBlockEntity
            ?: return super.useItemOn(stack, state, level, pos, player, hand, hitResult)

        if (IXplatAbstractions.INSTANCE.isPigment(stack)) {
            candle.setPigment(FrozenPigment(stack.copy(), player.uuid))
        } else {
            candle.setPigment(IXplatAbstractions.INSTANCE.getPigment(player))
        }

        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos)
        return ItemInteractionResult.sidedSuccess(level.isClientSide)
    }

    // Обработка пустой руки (тушение свечи)
    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hitResult: BlockHitResult
    ): InteractionResult {
        if (player.isShiftKeyDown || !state.getValue(LIT))
            return super.useWithoutItem(state, level, pos, player, hitResult)

        // Свеча горит — применяем пигмент с пустой рукой
        val candle = level.getBlockEntity(pos) as? HexCandleBlockEntity
            ?: return super.useWithoutItem(state, level, pos, player, hitResult)

        candle.setPigment(IXplatAbstractions.INSTANCE.getPigment(player))
        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos)
        return InteractionResult.SUCCESS
    }

    override fun animateTick(state: BlockState, level: Level, pos: BlockPos, random: RandomSource) {
        if (!state.getValue(LIT))
            return

        val blockEntity = level.getBlockEntity(pos)
        if (blockEntity !is HexCandleBlockEntity)
            return

        val colorProvider = blockEntity.getPigment().colorProvider
        getParticleOffsets(state).forEach { offset: Vec3 ->
            spawnCandleParticles(level, Vec3.atLowerCornerOf(pos).add(offset), random, colorProvider)
        }
    }

    // Правильный getStateForPlacement (без override, потому что родительский метод final)
    // Вместо этого используем то, что уже есть в CandleBlock — он сам обрабатывает WATERLOGGED и CANDLES
    // Удалите ваш метод getPlacementState, он не нужен

    override fun updateShape(state: BlockState, direction: Direction, neighborState: BlockState, level: LevelAccessor, pos: BlockPos, neighborPos: BlockPos): BlockState {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level))
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos)
    }

    override fun getFluidState(state: BlockState): FluidState {
        return if (state.getValue(WATERLOGGED)) Fluids.WATER.getSource(false) else super.getFluidState(state)
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = HexCandleBlockEntity(pos, state)

    companion object {
        fun spawnCandleParticles(level: Level, position: Vec3, random: RandomSource, colorProvider: ColorProvider) {
            if (random.nextFloat() < 0.17f) {
                level.playSound(
                    null,
                    position.x + 0.5,
                    position.y + 0.5,
                    position.z + 0.5,
                    SoundEvents.CANDLE_AMBIENT,
                    SoundSource.BLOCKS,
                    1.0f + random.nextFloat(),
                    random.nextFloat() * 0.7f + 0.3f
                )
            }
            level.addParticle(
                ConjureParticleOptions(colorProvider.getColor(level.gameTime.toFloat(), position)),
                position.x, position.y, position.z,
                0.0, level.random.nextFloat() * 0.02, 0.0
            )
        }
    }
}