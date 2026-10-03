package me.nanorasmus.nanodev.hex_js.misc


import at.petrak.hexcasting.api.casting.eval.CastResult
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM
import at.petrak.hexcasting.api.casting.eval.vm.ContinuationFrame
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import net.minecraft.server.level.ServerLevel
import kotlin.jvm.Throws


/**
 * kotlin doesn't check its exceptions so code extending kotlin compiled stuff can't actually properly throw exceptions
 * This has been stolen straight from Hex Gloop
 */
class JavaMishapThrower {
    companion object {
        @JvmStatic
        @Throws(Mishap::class)
        fun evaluate(next: ContinuationFrame, continuation: SpellContinuation, world: ServerLevel, harness: CastingVM): CastResult {
            try {
                return next.evaluate((continuation as SpellContinuation.NotDone).next, world, harness);
            } catch (mishap: Mishap) {
                throw mishap;
            }
        }
    }
}