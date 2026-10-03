package miyucomics.hexical.features.telepathy

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getPlayer
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.world.entity.player.Player
class OpGetKeybindOther(private val key: String) : ConstMediaAction {
	override val argc = 1
	override val mediaCost = MediaConstants.DUST_UNIT / 10
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val target = args.getPlayer(env.world,0, argc) as? Player ?: return (-1).asActionResult
        if (!target.serverKeybindActive().getOrDefault(key, false))
            return (-1).asActionResult
        return target.serverKeybindDuration().getOrDefault(key, 0).asActionResult
    }
}