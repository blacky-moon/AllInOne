package dev.kineticcat.complexhex.casting.actions.bits

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import com.mojang.math.Transformation
import dev.kineticcat.complexhex.mixin.BITInvokers.DisplayInvoker
import net.minecraft.world.entity.Display
import org.ejml.simple.SimpleMatrix // Используем EJML
import org.joml.Matrix4f
import ram.talia.moreiotas.api.getMatrix

object OpSetBit4x4 : SpellAction {
    override val argc = 2

    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val e = args.getEntity(env.world, 0, argc) // Исправлен getEntity
        var mat = args.getMatrix(1) // Это уже SimpleMatrix (EJML)

        env.assertEntityInRange(e)
        if (e !is Display) throw MishapBadEntity.of(e, "bit")

        // EJML использует getNumRows() и getNumCols()
        if (!(mat.getNumRows() == 4 && mat.getNumCols() == 4)) {
            // Логика расширения матрицы до 4x4 через EJML
            val newMat = SimpleMatrix.identity(4)
            when (mat.getNumRows() to mat.getNumCols()) {
                3 to 3 -> newMat.insertIntoThis(0, 0, mat)
                3 to 4 -> newMat.insertIntoThis(0, 0, mat.extractMatrix(0, 3, 0, 4))
                4 to 3 -> newMat.insertIntoThis(0, 0, mat.extractMatrix(0, 4, 0, 3))
                4 to 4 -> { /* mat уже 4x4, ничего не делаем */ }
                else -> throw MishapInvalidIota.ofType(args[1], 1, "transformationmatrix")
            }
            mat = newMat
        }

        return SpellAction.Result(
            Spell(e, mat),
            MediaConstants.DUST_UNIT,
            listOf(ParticleSpray.burst(env.castingEntity?.position() ?: env.mishapSprayPos(), 1.0))
        )
    }

    private data class Spell(val e: Display, val mat: SimpleMatrix) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            // Конвертация SimpleMatrix (EJML) в Matrix4f (JOML) для Minecraft 1.21
            val matrix4f = Matrix4f()
            for (r in 0 until 4) {
                for (c in 0 until 4) {
                    matrix4f.set(c, r, mat.get(r, c).toFloat()) // set(col, row, value)
                }
            }
            (e as DisplayInvoker).invokeSetTransformation(Transformation(matrix4f))
            e.tick()
        }
    }
}