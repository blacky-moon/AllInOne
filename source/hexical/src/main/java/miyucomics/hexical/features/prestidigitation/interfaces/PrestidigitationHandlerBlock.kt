package miyucomics.hexical.features.prestidigitation.interfaces

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

abstract class PrestidigitationHandlerBlock : PrestidigitationHandler {
    abstract fun canAffectBlock(env: CastingEnvironment, pos: BlockPos): Boolean
    abstract fun affect(env: CastingEnvironment, pos: BlockPos)

    companion object {
        fun getBlockState(env: CastingEnvironment, pos: BlockPos): BlockState = env.world.getBlockState(pos)
        fun getBlock(env: CastingEnvironment, pos: BlockPos): Block = getBlockState(env, pos).block
        fun setBlockState(env: CastingEnvironment, pos: BlockPos, state: BlockState) {
            env.world.setBlock(pos, state, 3)
        }

        fun <T : BlockEntity> blockEntity(handledClass: Class<T>, effect: (ServerLevel, BlockPos, T) -> Unit) = object : PrestidigitationHandlerBlock() {
            override fun canAffectBlock(env: CastingEnvironment, pos: BlockPos): Boolean {
                return handledClass.isInstance(env.world.getBlockEntity(pos))
            }
            override fun affect(env: CastingEnvironment, pos: BlockPos) {
                @Suppress("UNCHECKED_CAST")
                effect(env.world as ServerLevel, pos, env.world.getBlockEntity(pos) as T)
            }
        }
    }
}