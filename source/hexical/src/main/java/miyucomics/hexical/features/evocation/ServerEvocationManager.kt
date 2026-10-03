package miyucomics.hexical.features.evocation

import miyucomics.hexical.inits.HexicalSounds
import miyucomics.hexical.misc.CastingUtils
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundSource
import net.neoforged.neoforge.network.PacketDistributor

object ServerEvocationManager {
    const val EVOKE_DURATION: Int = 20

    fun startEvocation(player: ServerPlayer, server: MinecraftServer) {
        if (!CastingUtils.isEnlightened(player)) return

        player.evocationActive = true
        player.evocationDuration = EVOKE_DURATION

        player.level().playSound(
            null, player.x, player.y, player.z,
            HexicalSounds.EVOKING_MURMUR.get(), SoundSource.PLAYERS, 1f, 1f
        )

        for (receiver in server.playerList.players) {
            PacketDistributor.sendToPlayer(receiver, StartEvokePayload(player.uuid))
        }
    }

    fun endEvocation(player: ServerPlayer, server: MinecraftServer) {
        if (!CastingUtils.isEnlightened(player)) return

        player.evocationActive = false

        for (receiver in server.playerList.players) {
            PacketDistributor.sendToPlayer(receiver, EndEvokePayload(player.uuid))
        }
    }
}
