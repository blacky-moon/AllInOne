package miyucomics.hexical.features.mute

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.iota.Iota
import miyucomics.hexical.inits.HexicalComponents

object OpIsMute : ConstMediaAction {
    override val argc = 1
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val target = args.getEntity(env.world, 0, argc)
        env.assertEntityInRange(target)
        val muted = target.getCapability(HexicalComponents.MUTED_CAP)?.muted ?: false
        return muted.asActionResult
    }
}