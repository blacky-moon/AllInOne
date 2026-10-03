package miyucomics.hexical.features.media_jar

import at.petrak.hexcasting.api.misc.MediaConstants
import miyucomics.hexical.HexicalMain
import miyucomics.hexical.features.transmuting.TransmutationResult
import miyucomics.hexical.features.transmuting.TransmutingHelper
import miyucomics.hexical.inits.HexicalBlocks
import miyucomics.hexical.inits.HexicalComponents
import miyucomics.hexical.inits.HexicalItems
import miyucomics.hexical.inits.HexicalSounds
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.component.DataComponents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class MediaJarBlock : Block(Properties.of()
    .lightLevel { _ -> 15 }
    .sound(SoundType.GLASS)
    .noOcclusion()
), EntityBlock {

    init {
        this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.WATERLOGGED, false))
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return Shapes.box(3.0 / 16, 0.0, 3.0 / 16, 13.0 / 16, 14.0 / 16, 13.0 / 16)
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = MediaJarBlockEntity(pos, state)

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        val fluidState = context.level.getFluidState(context.clickedPos)
        return this.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, fluidState.type == Fluids.WATER)
    }

    // ИСПРАВЛЕНО: когда банку ставят из инвентаря, стандартный BlockItem
    // восстанавливает NBT блок-энтити из BLOCK_ENTITY_DATA (если он есть —
    // например банка была получена разбитием блока). Но банка может прийти
    // и без BLOCK_ENTITY_DATA (из креативной вкладки, из крафта, после
    // дунканья предметов в руке) — тогда у неё есть только компонент
    // HexicalComponents.MEDIA_HOLDER, и его нужно перенести в блок-энтити
    // вручную, иначе поставленная банка окажется пустой.
    override fun setPlacedBy(level: Level, pos: BlockPos, state: BlockState, placer: LivingEntity?, stack: ItemStack) {
        super.setPlacedBy(level, pos, state, placer, stack)
        val blockEntity = level.getBlockEntity(pos)
        if (blockEntity is MediaJarBlockEntity) {
            val media = stack.get(HexicalComponents.MEDIA_HOLDER.get())
            if (media != null)
                blockEntity.setMedia(media)
        }
    }

    override fun playerWillDestroy(level: Level, pos: BlockPos, state: BlockState, player: Player): BlockState {
        val blockEntity = level.getBlockEntity(pos)

        if (blockEntity is MediaJarBlockEntity && !player.isCreative) {
            val itemStack = ItemStack(HexicalItems.MEDIA_JAR_ITEM.get())
            val nbt = blockEntity.saveWithoutMetadata(level.registryAccess())
            nbt.putString("id", HexicalMain.id("media_jar").toString())
            val customData = CustomData.of(nbt)

            itemStack.set(DataComponents.BLOCK_ENTITY_DATA, customData)
            itemStack.set(HexicalComponents.MEDIA_HOLDER.get(), blockEntity.getMedia())

            val item = ItemEntity(level, pos.x.toDouble() + 0.5, pos.y.toDouble() + 0.5, pos.z.toDouble() + 0.5, itemStack)
            item.setDefaultPickUpDelay()
            level.addFreshEntity(item)
        }

        return super.playerWillDestroy(level, pos, state, player)
    }

    override fun useWithoutItem(state: BlockState, level: Level, pos: BlockPos, player: Player, hitResult: BlockHitResult): InteractionResult {
        val jar = level.getBlockEntity(pos) as? MediaJarBlockEntity ?: return InteractionResult.PASS
        val stack = player.getItemInHand(InteractionHand.MAIN_HAND)
        player.swing(InteractionHand.MAIN_HAND)
        return when (val result = TransmutingHelper.transmuteItem(level, stack, jar.getMedia(), jar::insertMedia, jar::withdrawMedia)) {
            is TransmutationResult.AbsorbedMedia -> {
                level.playSound(null, player, HexicalSounds.AMETHYST_MELT.get(), SoundSource.BLOCKS, 1f, 1f)
                InteractionResult.SUCCESS
            }
            is TransmutationResult.TransmutedItems -> {
                level.playSound(null, pos, HexicalSounds.ITEM_DUNKS.get(), SoundSource.BLOCKS, 1f, 1f)
                for (outputStack in result.output) {
                    if (!player.getInventory().add(outputStack)) {
                        val itemEntity = ItemEntity(level, pos.x.toDouble() + 0.5, pos.y.toDouble() + 0.5, pos.z.toDouble() + 0.5, outputStack)
                        itemEntity.setDefaultPickUpDelay()
                        level.addFreshEntity(itemEntity)
                    }
                }
                InteractionResult.SUCCESS
            }
            is TransmutationResult.RefilledHolder -> {
                level.playSound(null, pos, HexicalSounds.ITEM_DUNKS.get(), SoundSource.BLOCKS, 1f, 1f)
                InteractionResult.SUCCESS
            }
            is TransmutationResult.Pass -> InteractionResult.FAIL
        }
    }

    override fun updateShape(state: BlockState, direction: Direction, neighborState: BlockState, level: LevelAccessor, pos: BlockPos, neighborPos: BlockPos): BlockState {
        if (state.getValue(BlockStateProperties.WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level))
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos)
    }

    override fun getFluidState(state: BlockState): FluidState {
        return if (state.getValue(BlockStateProperties.WATERLOGGED)) Fluids.WATER.getSource(false) else super.getFluidState(state)
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(BlockStateProperties.WATERLOGGED)
    }

    companion object {
        const val MAX_CAPACITY = MediaConstants.CRYSTAL_UNIT * 64
    }
}