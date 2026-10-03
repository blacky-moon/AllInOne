package blacky_moon.miyu.overevaluate.actions.metaevals


import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getBool
import at.petrak.hexcasting.api.casting.iota.Iota
import blacky_moon.miyu.overevaluate.mishaps.AssertMishap

object OpMaat : ConstMediaAction {
    override val argc = 2
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        if (!args.getBool(0, argc))
            throw AssertMishap(args[1])
        return listOf()
    }
}