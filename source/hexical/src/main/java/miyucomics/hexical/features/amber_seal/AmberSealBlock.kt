package miyucomics.hexical.features.amber_seal

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.Property
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.phys.BlockHitResult

class AmberSealBlock : Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)), EntityBlock {
    override fun playerWillDestroy(world: Level, pos: BlockPos, state: BlockState, player: Player): BlockState {
        val seal = world.getBlockEntity(pos)
        if (!player.isCreative || seal !is AmberSealBlockEntity) {
            return super.playerWillDestroy(world, pos, state, player)
        }

        val drop = ItemEntity(world, pos.x.toDouble() + 0.5, pos.y.toDouble() + 0.5, pos.z.toDouble() + 0.5, seal.toItem())
        drop.setDefaultPickUpDelay()
        world.addFreshEntity(drop)
        return super.playerWillDestroy(world, pos, state, player)
    }

    override fun getDrops(state: BlockState, builder: LootParams.Builder): MutableList<ItemStack> {
        val list = super.getDrops(state, builder)
        val seal = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY)
        if (seal is AmberSealBlockEntity)
            list.add(seal.toItem())
        return list
    }

    override fun useWithoutItem(
        state: BlockState,
        world: Level,
        position: BlockPos,
        player: Player,
        hit: BlockHitResult
    ): InteractionResult {
        val seal = world.getBlockEntity(position) as? AmberSealBlockEntity ?: return InteractionResult.PASS
        val newState = seal.encasedState
        DanglingState.queuedBlockEntity = seal.encasedEntity
        world.setBlock(position, newState, Block.UPDATE_ALL or Block.UPDATE_KNOWN_SHAPE)
        world.playSound(player, position, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 1.0f, world.random.nextFloat() * 0.4f + 0.8f)
        return InteractionResult.sidedSuccess(world.isClientSide)
    }

    override fun getRenderShape(state: BlockState): RenderShape = RenderShape.MODEL

    override fun propagatesSkylightDown(state: BlockState, world: BlockGetter, pos: BlockPos): Boolean = true

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = AmberSealBlockEntity(pos, state)

    companion object {
        @JvmStatic
        fun copyRotationParameters(original: BlockState, reference: BlockState): BlockState {
            var output = copyPropertyIfPresent(original, reference, BlockStateProperties.HORIZONTAL_FACING)
            output = copyPropertyIfPresent(output, reference, BlockStateProperties.FACING)
            output = copyPropertyIfPresent(output, reference, BlockStateProperties.HORIZONTAL_AXIS)
            output = copyPropertyIfPresent(output, reference, BlockStateProperties.AXIS)
            output = copyPropertyIfPresent(output, reference, BlockStateProperties.ATTACHED)
            output = copyPropertyIfPresent(output, reference, BlockStateProperties.FACING_HOPPER)
            return output
        }

        private fun <T : Comparable<T>> copyPropertyIfPresent(
            original: BlockState,
            reference: BlockState,
            property: Property<T>
        ): BlockState {
            if (original.hasProperty(property) && reference.hasProperty(property))
                return original.setValue(property, reference.getValue(property))
            return original
        }
    }
}