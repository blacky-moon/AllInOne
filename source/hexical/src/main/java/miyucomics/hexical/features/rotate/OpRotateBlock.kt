package miyucomics.hexical.features.rotate

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.block.state.BlockState

object OpRotateBlock : SpellAction {
    override val argc = 2

    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val pos = args.getBlockPos(0, argc)
        env.assertPosInRange(pos)
        val state = env.world.getBlockState(pos)
        val rotation = args.getVec3(1, argc)
        val direction = Direction.fromDelta(
            rotation.x.toInt(), rotation.y.toInt(), rotation.z.toInt()
        ) ?: throw MishapInvalidIota.of(args[1], 0, "axis_vector")

        val result = BlockRotationHandlers.modify(state, direction)
            ?: throw MishapBadBlock.of(pos, "rotatable")
        return SpellAction.Result(
            Spell(pos, result),
            MediaConstants.DUST_UNIT / 8,
            listOf(ParticleSpray.burst(pos.center, 1.0))
        )
    }

    private data class Spell(val pos: BlockPos, val state: BlockState) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            if (!env.canEditBlockAt(pos) ||
                !IXplatAbstractions.INSTANCE.isBreakingAllowed(
                    env.world, pos, env.world.getBlockState(pos),
                    env.castingEntity as? ServerPlayer
                )
            ) return
            env.world.setBlock(pos, state, 3)
            env.world.updateNeighborsAt(pos, state.block)
        }
    }
}