package dev.kineticcat.complexhex.casting.actions.expr

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getPositiveIntUnderInclusive
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import dev.kineticcat.complexhex.api.getExpr
import dev.kineticcat.complexhex.api.util.Value
import org.ejml.simple.SimpleMatrix // Заменено JBLAS на EJML
import ram.talia.moreiotas.api.asActionResult // Обязательный импорт для .asActionResult

object OpMakeMatrix : ConstMediaAction {
    override val argc = 3

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val width = args.getPositiveIntUnderInclusive(0, 144)
        val height = args.getPositiveIntUnderInclusive(1, 144)
        val expr = args.getExpr(2)

        val data = Array(height) { row ->
            DoubleArray(width) { column ->
                expr("i", column.toDouble())("j", row.toDouble()).simp()
                    .let {
                        if (it is Value) it.x
                        else throw MishapInvalidIota.ofType(args[2], 2, "unsimplifiable_expr")
                    }
            }
        }

        return SimpleMatrix(data).asActionResult
    }
}