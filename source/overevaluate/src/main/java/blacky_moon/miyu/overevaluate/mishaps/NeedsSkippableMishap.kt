package blacky_moon.miyu.overevaluate.mishaps


import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.pigment.FrozenPigment
import blacky_moon.miyu.overevaluate.Overevaluate
import net.minecraft.network.chat.Component
import net.minecraft.world.item.DyeColor


class NeedsSkippableMishap : Mishap() {
    override fun accentColor(env: CastingEnvironment, errorCtx: Context): FrozenPigment = dyeColor(DyeColor.BLUE)
    override fun errorMessage(env: CastingEnvironment, errorCtx: Context): Component = error(Overevaluate.MOD_ID + ":needs_skippable")
    override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {}
}