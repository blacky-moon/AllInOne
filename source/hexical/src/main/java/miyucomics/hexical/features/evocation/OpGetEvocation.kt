package miyucomics.hexical.features.evocation

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import miyucomics.hexical.misc.HexSerialization
import net.minecraft.server.level.ServerPlayer

object OpGetEvocation : ConstMediaAction {
    override val argc = 0

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        if (env.castingEntity !is ServerPlayer) throw MishapBadCaster()
        val player = env.castingEntity as ServerPlayer
        val deserialized = HexSerialization.deserializeHex(player.evocation, env.world.registryAccess())
        return listOf(ListIota(deserialized))
    }
}