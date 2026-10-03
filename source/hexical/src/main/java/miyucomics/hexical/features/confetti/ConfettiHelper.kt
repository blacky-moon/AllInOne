package miyucomics.hexical.features.confetti

import miyucomics.hexical.HexicalMain
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.network.PacketDistributor

object ConfettiHelper {
    fun spawn(world: ServerLevel, pos: Vec3, dir: Vec3, speed: Double) {
        val payload = ConfettiPayload(
            HexicalMain.RANDOM.nextLong(),
            pos,
            dir,
            speed
        )
        val chunkPos = world.getChunkAt(BlockPos.containing(pos)).pos
        PacketDistributor.sendToPlayersTrackingChunk(world, chunkPos, payload)
    }
}