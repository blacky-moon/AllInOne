package miyucomics.hexical.features.amber_seal

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.misc.MediaConstants
import miyucomics.hexical.HexicalMain
import miyucomics.hexical.inits.HexicalBlocks
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.item.DyeColor
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

object OpAmberSeal : SpellAction {
    override val argc = 1
    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val position = args.getBlockPos(0, argc)
        env.assertPosInRange(position)
        return SpellAction.Result(Spell(position), MediaConstants.CRYSTAL_UNIT, listOf())
    }

    /**
     * Блок нельзя запечатать, если он неразрушим (бедрок и подобные,
     * getDestroySpeed < 0), воздух, либо если вложенность уже достигла предела.
     */
    private fun isValidToSeal(state: BlockState, level: Level, pos: BlockPos): Boolean {
        if (state.isAir) return false
        if (state.getDestroySpeed(level, pos) < 0f) return false
        return true
    }

    private data class Spell(val position: BlockPos) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            val level = env.world
            val originalState = level.getBlockState(position)
            val originalEntity = level.getBlockEntity(position)

            if (!isValidToSeal(originalState, level, position))
                throw CannotSealMishap()

            if (AmberSealBlockEntity.nestingDepthOf(originalEntity) >= AmberSealBlockEntity.MAX_NESTING_DEPTH)
                throw SealTooDeepMishap()

            val newBlockState = AmberSealBlock.copyRotationParameters(
                HexicalBlocks.AMBER_SEAL_BLOCK.get().defaultBlockState(),
                originalState
            )

            level.setBlock(position, newBlockState, Block.UPDATE_ALL or Block.UPDATE_KNOWN_SHAPE)

            val newEntity = level.getBlockEntity(position) as? AmberSealBlockEntity
                ?: return

            try {
                newEntity.init(originalState, originalEntity)
            } catch (e: IllegalArgumentException) {
                // init() тоже сам себя защищает (самоссылка / переполнение
                // глубины) — на случай, если структура изменилась между
                // проверкой выше и вызовом init на другом потоке/тике.
                level.setBlock(position, originalState, Block.UPDATE_ALL or Block.UPDATE_KNOWN_SHAPE)
                throw SealTooDeepMishap()
            }
        }
    }

    class CannotSealMishap : Mishap() {
        override fun accentColor(env: CastingEnvironment, errorCtx: Context) = dyeColor(DyeColor.RED)
        override fun errorMessage(env: CastingEnvironment, errorCtx: Context): Component =
            error("${HexicalMain.MOD_ID}:cannot_seal_block")
        override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {}
    }

    class SealTooDeepMishap : Mishap() {
        override fun accentColor(env: CastingEnvironment, errorCtx: Context) = dyeColor(DyeColor.RED)
        override fun errorMessage(env: CastingEnvironment, errorCtx: Context): Component =
            error("${HexicalMain.MOD_ID}:seal_too_deep")
        override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {}
    }
}