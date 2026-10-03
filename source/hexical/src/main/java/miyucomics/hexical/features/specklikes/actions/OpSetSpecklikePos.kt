package miyucomics.hexical.features.specklikes.actions

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity
import miyucomics.hexical.features.specklikes.BaseSpecklike
import net.minecraft.world.phys.Vec3

object OpSetSpecklikePos : SpellAction {
    override val argc = 2

    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val specklike = args.getEntity(env.world,0, argc)
        if (specklike !is BaseSpecklike)
            throw MishapBadEntity.of(specklike, "specklike")
        val position = args.getVec3(1, argc)
        env.assertVecInRange(position)
        return SpellAction.Result(Spell(specklike, position), 0, listOf())
    }

    private data class Spell(val specklike: BaseSpecklike, val position: Vec3) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            // В 1.21.1 setPosition -> setPos, standingEyeHeight -> eyeHeight
            specklike.setPos(position.subtract(0.0, specklike.eyeHeight.toDouble(), 0.0))
        }
    }
}