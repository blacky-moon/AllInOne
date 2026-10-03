package miyucomics.hexical.features.misc_actions

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.getPositiveDoubleUnderInclusive
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.Vec3Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.core.particles.VibrationParticleOption
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.gameevent.BlockPositionSource
import net.minecraft.world.level.gameevent.EntityPositionSource
import net.minecraft.world.level.gameevent.PositionSource
import net.minecraft.world.phys.Vec3

object OpVibrate : SpellAction {
    override val argc = 3
    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val level = env.castingEntity?.level() as? ServerLevel ?: throw IllegalStateException("No server level")

        val to = when (val test = args[1]) {
            is EntityIota -> {
                val entity = test.getEntity(level)
                EntityPositionSource(entity, entity.getEyeHeight(entity.getPose()))
            }
            is Vec3Iota -> BlockPositionSource(args.getBlockPos(1, argc))
            else -> throw MishapInvalidIota.of(args[0], 1, "entity_or_vector")
        }
        return SpellAction.Result(
            Spell(args.getVec3(0, argc), to, args.getPositiveDoubleUnderInclusive(2, 10.0, argc) * 20),
            MediaConstants.DUST_UNIT / 100,
            listOf()
        )
    }

    private data class Spell(val from: Vec3, val to: PositionSource, val duration: Double) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            val level = env.world as ServerLevel
            val particle = VibrationParticleOption(to, duration.toInt())

            for (player in level.players()) {
                if (player is ServerPlayer) {
                    level.sendParticles(
                        player,
                        particle,
                        true,
                        from.x, from.y, from.z,
                        1,
                        0.0, 0.0, 0.0,
                        1.0
                    )
                }
            }
        }
    }
}