package miyucomics.hexical.features.charms

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.iota.Vec3Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import net.minecraft.util.FastColor
import kotlin.math.max
import kotlin.math.min

object OpMediaColorCharmed : ConstMediaAction {
    override val argc = 1
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        if (env !is CharmCastEnv)
            throw NeedsCharmedItemMishap()

        val stack = env.getCurrentStack()
        if (stack.isEmpty)
            throw NeedsCharmedItemMishap()

        when (val iota = args[0]) {
            is NullIota -> {
                val compound = CharmUtilities.getCompound(stack)
                compound.remove("media_bar_color")
                CharmUtilities.setCompound(stack, compound)
            }
            is Vec3Iota -> {
                val compound = CharmUtilities.getCompound(stack)
                compound.putInt("media_bar_color", FastColor.ARGB32.color(
                    255,
                    (max(min(iota.vec3.x, 1.0), 0.0) * 255).toInt(),
                    (max(min(iota.vec3.y, 1.0), 0.0) * 255).toInt(),
                    (max(min(iota.vec3.z, 1.0), 0.0) * 255).toInt()
                ))
                CharmUtilities.setCompound(stack, compound)
            }
            else -> throw MishapInvalidIota.of(iota, 0, "vector_or_null")
        }
        return emptyList()
    }
}