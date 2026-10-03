package miyucomics.hexical.features.specklikes.strand

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getPattern
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.Vec3Iota
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3
import kotlin.math.max

object OpDrawPattern : ConstMediaAction {
    override val argc = 1

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val pattern = args.getPattern(0, argc)
        val lines = pattern.toLines(1f, pattern.getCenter(1f).negated()).toMutableList()
        val scaling = max(
            lines.maxBy { vector -> vector.x }.x - lines.minBy { vector -> vector.x }.x,
            lines.maxBy { vector -> vector.y }.y - lines.minBy { vector -> vector.y }.y
        )
        for (i in lines.indices) {
            lines[i] = Vec2(lines[i].x, -lines[i].y).scale(1f / scaling)
        }
        return lines.map { Vec3Iota(Vec3(it.x.toDouble(), it.y.toDouble(), 0.0)) }.asActionResult
    }
}