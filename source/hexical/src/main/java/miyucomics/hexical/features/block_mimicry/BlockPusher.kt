package miyucomics.hexical.features.block_mimicry

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.piston.PistonStructureResolver
import net.minecraft.world.level.gameevent.GameEvent

object BlockPusher {
    fun pushBlocks(world: ServerLevel, start: BlockPos, direction: Direction): Boolean {
        val pistonHandler = PistonStructureResolver(world, start.relative(direction.opposite), direction, true)
        if (!pistonHandler.resolve())
            return false
        executePush(world, pistonHandler.toPush, pistonHandler.toDestroy, direction)
        world.playSound(null, start, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.25f, world.random.nextFloat() * 0.25f + 0.6f)
        return true
    }

    private fun executePush(world: ServerLevel, toPush: List<BlockPos>, toBreak: List<BlockPos>, direction: Direction) {
        for (pos in toBreak)
            world.destroyBlock(pos, true)

        val memory: MutableMap<BlockPos, net.minecraft.world.level.block.state.BlockState> = HashMap()
        for (pos in toPush)
            memory[pos] = world.getBlockState(pos)

        for (pos in toPush)
            world.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL or Block.UPDATE_IMMEDIATE)

        for (pos in toPush) {
            val state = memory[pos]!!
            val newPos = pos.relative(direction)
            world.setBlock(newPos, state, Block.UPDATE_ALL or Block.UPDATE_IMMEDIATE)
            world.updateNeighborsAt(newPos, state.block)
            world.gameEvent(GameEvent.BLOCK_CHANGE, newPos, GameEvent.Context.of(state))
        }

        for (pos in toPush) {
            val state = memory[pos]!!
            val newPos = pos.relative(direction)

            for (dir in Direction.entries) {
                val neighborPos = newPos.relative(dir)
                if (world.isLoaded(neighborPos)) {
                    world.updateNeighborsAt(neighborPos, world.getBlockState(neighborPos).block)
                }
            }
            // Аналог world.updateNeighborsAlways
            world.updateNeighborsAt(newPos, state.block)
        }
    }
}