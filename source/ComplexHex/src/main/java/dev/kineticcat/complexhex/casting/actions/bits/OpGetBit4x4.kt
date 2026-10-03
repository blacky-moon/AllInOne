package dev.kineticcat.complexhex.casting.actions.bits

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity
import dev.kineticcat.complexhex.mixin.BITInvokers.DisplayInvoker
import net.minecraft.world.entity.Display
import org.ejml.simple.SimpleMatrix
import ram.talia.moreiotas.api.asActionResult // <-- ОБЯЗАТЕЛЬНЫЙ ИМПОРТ!

object OpGetBit4x4 : ConstMediaAction {
    override val argc = 1

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {

        val e = args.getEntity(env.world, 0, argc)

        if (e !is Display) throw MishapBadEntity.of(e, "bit")
        env.assertEntityInRange(e)

        val transformMatrix = (e as DisplayInvoker).invokeCreateTransformation(e.entityData).matrix

        val simpleMat = SimpleMatrix(4, 4, true,
            transformMatrix.m00().toDouble(), transformMatrix.m01().toDouble(), transformMatrix.m02().toDouble(), transformMatrix.m03().toDouble(),
            transformMatrix.m10().toDouble(), transformMatrix.m11().toDouble(), transformMatrix.m12().toDouble(), transformMatrix.m13().toDouble(),
            transformMatrix.m20().toDouble(), transformMatrix.m21().toDouble(), transformMatrix.m22().toDouble(), transformMatrix.m23().toDouble(),
            transformMatrix.m30().toDouble(), transformMatrix.m31().toDouble(), transformMatrix.m32().toDouble(), transformMatrix.m33().toDouble()
        )

        return simpleMat.asActionResult
    }
}