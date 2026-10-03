package blacky_moon.miyu.overevaluate.mishaps

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.pigment.FrozenPigment
import blacky_moon.miyu.overevaluate.Overevaluate
import net.minecraft.world.item.DyeColor

import net.minecraft.network.chat.Component;

class ThemisMishap(val received: Iota?) : Mishap() {
    override fun accentColor(env: CastingEnvironment, errorCtx: Context): FrozenPigment = dyeColor(DyeColor.GREEN)
    override fun errorMessage(env: CastingEnvironment, errorCtx: Context) :  Component {
        if (received == null)
            return error(Overevaluate.MOD_ID + ":themis_empty")
        return error(Overevaluate.MOD_ID + ":themis", received.display())
    }
    override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {}
}