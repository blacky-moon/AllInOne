package blacky_moon.miyu.overevaluate.actions.jumble

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota

object OpDissolveJumble : ConstMediaAction {
    override val argc = 1

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val jumble = args.getJumble(0, argc)
        return listOf(
            DoubleIota(jumble.args.toDouble()),
            ListIota(jumble.jumble.map { DoubleIota(it.toDouble()) })
        )
    }
}