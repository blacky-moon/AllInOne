package ram.talia.moreiotas.common.casting.actions.matrices

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import org.ejml.simple.SimpleMatrix
import ram.talia.moreiotas.api.asMatrix
import ram.talia.moreiotas.api.getNumOrVecOrMatrix

object OpDeterminantMatrix : ConstMediaAction {
    override val argc = 1

    private fun determinant(mat: SimpleMatrix): Double {
        return mat.determinant();
    }

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val mat = args.getNumOrVecOrMatrix(0, argc).asMatrix

        if (mat.numCols != mat.numRows)
            throw MishapInvalidIota.ofType(args[0], 0, "matrix.square")
        if (mat.numCols > 4)
            throw MishapInvalidIota.of(args[0], 0, "matrix.max_size", 4, 4, mat.numCols, mat.numRows)

        return determinant(mat).asActionResult
    }
}