package io.github.techtastic.hexweb.casting.mishap

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.GarbageIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.pigment.FrozenPigment
import net.minecraft.network.chat.Component

class MishapBlacklistUrl(val blacklist: String): Mishap() {
    override fun accentColor(ctx: CastingEnvironment, errorCtx: Context) = FrozenPigment.DEFAULT.get()

    override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context) = Component.translatable("hexweb.mishap.blacklist_url", blacklist)

    override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
    }
}