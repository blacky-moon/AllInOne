package miyucomics.hexical.features.hex_candles

import at.petrak.hexcasting.api.pigment.ColorProvider
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.common.particles.ConjureParticleOptions
import at.petrak.hexcasting.xplat.IXplatAbstractions
import miyucomics.hexical.inits.HexicalBlocks
import net.minecraft.core.BlockPos
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.CandleCakeBlock
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.level.material.PushReaction
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import net.minecraft.util.RandomSource

class HexCandleCakeBlock : CandleCakeBlock(HexicalBlocks.HEX_CANDLE_BLOCK.get(), Properties.of()
    .mapColor(MapColor.COLOR_PURPLE)
    .noOcclusion()
    .strength(0.1f)
    .sound(SoundType.CANDLE)
    .lightLevel { state -> if (state.getValue(BlockStateProperties.LIT)) 3 else 0 }
    .pushReaction(PushReaction.DESTROY)
), EntityBlock {

    init {
        this.registerDefaultState(
            this.stateDefinition.any()
                .setValue(BlockStateProperties.LIT, false)
        )
    }

    override fun useWithoutItem(state: BlockState, level: Level, pos: BlockPos, player: Player, hitResult: BlockHitResult): InteractionResult {
        if (player.isShiftKeyDown)
            return super.useWithoutItem(state, level, pos, player, hitResult)
        if (!state.getValue(BlockStateProperties.LIT))
            return super.useWithoutItem(state, level, pos, player, hitResult)

        val candle = level.getBlockEntity(pos) as? HexCandleCakeBlockEntity ?: return InteractionResult.PASS
        val stack = player.getItemInHand(InteractionHand.MAIN_HAND)

        if (IXplatAbstractions.INSTANCE.isPigment(stack)) {
            candle.setPigment(FrozenPigment(stack.copy(), player.uuid))
        } else {
            candle.setPigment(IXplatAbstractions.INSTANCE.getPigment(player))
        }

        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos)
        return InteractionResult.SUCCESS
    }

    override fun animateTick(state: BlockState, level: Level, pos: BlockPos, random: RandomSource) {
        if (!state.getValue(BlockStateProperties.LIT))
            return

        val blockEntity = level.getBlockEntity(pos)
        if (blockEntity !is HexCandleCakeBlockEntity)
            return

        val colorProvider = blockEntity.getPigment().colorProvider
        getParticleOffsets(state).forEach { offset: Vec3 ->
            spawnCandleParticles(level, Vec3.atLowerCornerOf(pos).add(offset), random, colorProvider)
        }
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = HexCandleCakeBlockEntity(pos, state)

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