package miyucomics.hexical.features.mage_blocks

import miyucomics.hexical.features.mage_blocks.modifiers.BouncyModifier
import miyucomics.hexical.features.mage_blocks.modifiers.RedstoneModifier
import miyucomics.hexical.features.mage_blocks.modifiers.VolatileModifier
import miyucomics.hexical.inits.HexicalBlocks
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.level.material.PushReaction
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class MageBlock : Block(BlockBehaviour.Properties.of()
    .mapColor(MapColor.NONE)
    .noOcclusion()
    .noLootTable()
    .instabreak()
    .isValidSpawn { _, _, _, _ -> false }
    .isRedstoneConductor { _, _, _ -> false }
    .isSuffocating { _, _, _ -> false }
    .isViewBlocking { _, _, _ -> false }
    .pushReaction(PushReaction.BLOCK)
    .sound(SoundType.AMETHYST)
), EntityBlock {

    override fun isSignalSource(state: BlockState): Boolean = true

    override fun getSignal(state: BlockState, world: BlockGetter, pos: BlockPos, direction: Direction): Int {
        val blockEntity = world.getBlockEntity(pos) as? MageBlockEntity ?: return 0
        if (blockEntity.hasModifier(RedstoneModifier.TYPE))
            return blockEntity.getModifier(RedstoneModifier.TYPE).power
        return 0
    }

    override fun fallOn(world: Level, state: BlockState, pos: BlockPos, entity: Entity, fallDistance: Float) {
        val blockEntity = world.getBlockEntity(pos) as? MageBlockEntity ?: return
        if (!blockEntity.hasModifier(BouncyModifier.TYPE))
            super.fallOn(world, state, pos, entity, fallDistance)
    }

    override fun updateEntityAfterFallOn(world: BlockGetter, entity: Entity) {
        val blockEntity = world.getBlockEntity(entity.blockPosition().below()) as? MageBlockEntity ?: return
        if (blockEntity.hasModifier(BouncyModifier.TYPE)) {
            val velocity = entity.getDeltaMovement()
            if (velocity.y < 0) {
                entity.setDeltaMovement(velocity.x, -velocity.y, velocity.z)
                entity.fallDistance = 0f
            }
        } else
            super.updateEntityAfterFallOn(world, entity)
    }

    override fun onRemove(state: BlockState, world: Level, pos: BlockPos, newState: BlockState, moved: Boolean) {
        if (state.`is`(newState.block))
            return

        val blockEntity = world.getBlockEntity(pos) as? MageBlockEntity

        if (blockEntity != null && blockEntity.hasModifier(VolatileModifier.TYPE)) {
            for (direction in Direction.entries) {
                val positionToTest = pos.relative(direction)
                val otherState = world.getBlockState(positionToTest)
                if (otherState.`is`(HexicalBlocks.MAGE_BLOCK.get())) {
                    world.removeBlock(positionToTest, false)
                    world.removeBlockEntity(positionToTest)
                }
            }
        }

        super.onRemove(state, world, pos, newState, moved)
    }
    override fun propagatesSkylightDown(state: BlockState, level: BlockGetter, pos: BlockPos): Boolean = true

    override fun getVisualShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape = Shapes.empty()

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = MageBlockEntity(pos, state)

    override fun <T : BlockEntity> getTicker(
        world: Level,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return BlockEntityTicker { level, pos, blockState, blockEntity ->
            (blockEntity as? MageBlockEntity)?.modifiers?.forEach { (_, modifier) ->
                modifier.tick(level, pos, blockState)
            }
        }
    }

    override fun getRenderShape(state: BlockState): RenderShape = RenderShape.INVISIBLE
}