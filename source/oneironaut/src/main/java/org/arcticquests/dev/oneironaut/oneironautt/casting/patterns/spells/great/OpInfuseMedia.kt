package org.arcticquests.dev.oneironaut.oneironautt.casting.patterns.spells.great

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.Iota
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import org.arcticquests.dev.oneironaut.oneironautt.casting.mishaps.MishapUninfusable
import org.arcticquests.dev.oneironaut.oneironautt.getInfuseResult
import org.arcticquests.dev.oneironaut.oneironautt.toVec3i

class OpInfuseMedia : SpellAction {
    override val argc = 1

    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val target = args.getVec3(0, argc).toVec3i()
        val targetv3d = args.getVec3(0, argc)
        env.assertVecInRange(targetv3d)

        val targetType = env.world.getBlockState(BlockPos(target))
        val (result, cost, advancement) = getInfuseResult(targetType, env.world)

        if (result == Blocks.BARRIER.defaultBlockState()) {
            throw MishapUninfusable.of(BlockPos(target))
        }

        return SpellAction.Result(
            Spell(BlockPos(target), result, cost, advancement),
            cost,
            listOf(ParticleSpray.cloud(targetv3d, 2.0))
        )
    }

    private data class Spell(val target: BlockPos, val result: BlockState, val cost: Long, val advancement: String?) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            val level = env.world
            level.setBlock(target, result, 3)

            val be = level.getBlockEntity(target)
            if (be != null) {
                be.setChanged()
                level.sendBlockUpdated(target, level.getBlockState(target), result, 3)
            }
        }
    }
}