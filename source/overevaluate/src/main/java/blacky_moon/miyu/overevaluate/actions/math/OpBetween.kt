package blacky_moon.miyu.overevaluate.actions.math
import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.Vec3Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import net.minecraft.util.CubicSampler
import net.minecraft.world.phys.AABB
import kotlin.math.max
import kotlin.math.min

object OpBetween : ConstMediaAction {
    override val argc = 3

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val test = args[0]
        val a = args[1]
        val b = args[2]

        val isBetween = when (test) {
            is Vec3Iota if a is Vec3Iota && b is Vec3Iota -> {
                AABB(a.vec3, b.vec3).contains(test.vec3)
            }

            is DoubleIota if a is DoubleIota && b is DoubleIota -> {
                test.double >= min(a.double, b.double) && test.double <= max(a.double, b.double)
            }

            else -> throw MishapInvalidIota.of(test, 2, "all_numbers_or_all_vectors")
        }

        return isBetween.asActionResult
    }

}