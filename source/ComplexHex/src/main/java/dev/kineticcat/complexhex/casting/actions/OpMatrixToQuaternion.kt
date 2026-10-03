package dev.kineticcat.complexhex.casting.actions

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import dev.kineticcat.complexhex.stuff.Quaternion
import ram.talia.moreiotas.api.getMatrix
import kotlin.math.sqrt

object OpMatrixToQuaternion : ConstMediaAction {
    override val argc = 1

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val M = args.getMatrix(0)

        // ИСПРАВЛЕНИЕ 1: Используем getNumCols() и getNumRows() вместо .columns и .rows
        if (M.getNumCols() != 3 || M.getNumRows() != 3) {
            throw MishapInvalidIota.of(args[0],0, "not3x3matrix")
        }

        // ИСПРАВЛЕНИЕ 2: Используем M.get(row, col) вместо M[row, col]
        val tr = M.get(0, 0) + M.get(1, 1) + M.get(2, 2)
        val a: Double
        val b: Double
        val c: Double
        val d: Double

        if (tr > 0) {
            val S = sqrt(tr + 1.0) * 2 // S=4*a
            a = 0.25 * S
            b = (M.get(2, 1) - M.get(1, 2)) / S
            c = (M.get(0, 2) - M.get(2, 0)) / S
            d = (M.get(1, 0) - M.get(0, 1)) / S
        } else if ((M.get(0, 0) > M.get(1, 1)) && (M.get(0, 0) > M.get(2, 2))) {
            val S = sqrt(1.0 + M.get(0, 0) - M.get(1, 1) - M.get(2, 2)) * 2 // S=4*b
            a = (M.get(2, 1) - M.get(1, 2)) / S
            b = 0.25 * S
            c = (M.get(0, 1) + M.get(1, 0)) / S
            d = (M.get(0, 2) + M.get(2, 0)) / S
        } else if (M.get(1, 1) > M.get(2, 2)) {
            val S = sqrt(1.0 + M.get(1, 1) - M.get(0, 0) - M.get(2, 2)) * 2 // S=4*c
            a = (M.get(0, 2) - M.get(2, 0)) / S
            b = (M.get(0, 1) + M.get(1, 0)) / S
            c = 0.25 * S
            d = (M.get(1, 2) + M.get(2, 1)) / S
        } else {
            val S = sqrt(1.0 + M.get(2, 2) - M.get(0, 0) - M.get(1, 1)) * 2 // S=4*d
            a = (M.get(1, 0) - M.get(0, 1)) / S
            b = (M.get(0, 2) + M.get(2, 0)) / S
            c = (M.get(1, 2) + M.get(2, 1)) / S
            d = 0.25 * S
        }

        // Возвращаем результат через listOf(), так как asActionResult может быть недоступен
        return listOf(Quaternion(a, b, c, d).asIota())
    }
}