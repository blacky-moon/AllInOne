package ram.talia.moreiotas.common.casting.actions.matrices

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getPositiveIntUnderInclusive
import at.petrak.hexcasting.api.casting.iota.Iota
import org.ejml.simple.SimpleMatrix
import ram.talia.moreiotas.MoreIotasConfig
import ram.talia.moreiotas.api.asActionResult

object OpIdentityMatrix : ConstMediaAction {
    override val argc = 1

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        return SimpleMatrix.identity(args.getPositiveIntUnderInclusive(0, MoreIotasConfig.maxMatrixSize.get(), argc)).asActionResult;
    }
}