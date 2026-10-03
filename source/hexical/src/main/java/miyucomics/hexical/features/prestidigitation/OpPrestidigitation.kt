package miyucomics.hexical.features.prestidigitation

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.Vec3Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import miyucomics.hexical.features.prestidigitation.interfaces.PrestidigitationHandlerBlock
import miyucomics.hexical.features.prestidigitation.interfaces.PrestidigitationHandlerEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3

object OpPrestidigitation : SpellAction {
    override val argc = 1
    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        return when (val iota = args[0]) {
            is EntityIota -> {
                val entity = iota.getEntity(env.world)
                env.assertEntityInRange(entity)
                SpellAction.Result(
                    EntitySpell(entity, PrestidigitationHandlers.resolve(env, entity) ?: throw MishapNoPrestidigitation(entity.position())),
                    MediaConstants.DUST_UNIT / 10,
                    listOf(ParticleSpray.cloud(entity.position(), 1.0))
                )
            }
            is Vec3Iota -> {
                val pos = BlockPos.containing(iota.vec3)
                env.assertPosInRange(pos)
                SpellAction.Result(
                    BlockSpell(pos, PrestidigitationHandlers.resolve(env, pos) ?: throw MishapNoPrestidigitation(Vec3.atCenterOf(pos))),
                    MediaConstants.DUST_UNIT / 10,
                    listOf(ParticleSpray.cloud(Vec3.atCenterOf(pos), 1.0))
                )
            }
            else -> throw MishapInvalidIota.of(args[0], 0, "entity_or_vector")
        }
    }

    private data class BlockSpell(val pos: BlockPos, val handler: PrestidigitationHandlerBlock) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            handler.affect(env, pos)
        }
    }

    private data class EntitySpell(val entity: Entity, val handler: PrestidigitationHandlerEntity<*>) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            handler.affectSafely(env, entity)
        }
    }
}