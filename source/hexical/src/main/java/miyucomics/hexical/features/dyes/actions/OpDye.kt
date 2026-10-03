package miyucomics.hexical.features.dyes.actions

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.Vec3Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import miyucomics.hexical.features.dyes.DyeOption
import miyucomics.hexical.features.dyes.DyeableMishap
import miyucomics.hexical.features.dyes.DyeingUtils
import miyucomics.hexical.features.dyes.entity.DyeEntityHandler
import miyucomics.hexical.features.dyes.entity.DyeingEntityRegistry
import miyucomics.hexical.features.dyes.getDye
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.Property
import net.minecraft.world.entity.Entity
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.Vec3

object OpDye : SpellAction {
    override val argc = 2
    private const val COST = MediaConstants.DUST_UNIT / 8

    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val dye = args.getDye(1, argc)
        when (args[0]) {
            is EntityIota -> {
                val entity = args.getEntity(env.world,0, argc)
                env.assertEntityInRange(entity)
                val handler = DyeingEntityRegistry.resolve(entity, dye) ?: throw DyeableMishap(entity.position())
                return SpellAction.Result(EntitySpell(entity, dye, handler), COST, listOf(ParticleSpray.cloud(entity.position(), 1.0)))
            }
            is Vec3Iota -> {
                val position = args.getBlockPos(0, argc)
                env.assertPosInRange(position)
                val state = env.world.getBlockState(position)
                val result = DyeingUtils.getResult(state.block, dye) ?: throw DyeableMishap(Vec3.atCenterOf(position))
                return SpellAction.Result(BlockSpell(position, state, result), COST, listOf(ParticleSpray.cloud(Vec3.atCenterOf(position), 1.0)))
            }
            else -> throw MishapInvalidIota.of(args[0], 1, "entity_or_vector")
        }
    }

    private data class EntitySpell(val entity: Entity, val dye: DyeOption, val handler: DyeEntityHandler<*>) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            handler.affectSafely(entity, dye)
        }
    }

    private data class BlockSpell(val position: BlockPos, val old: BlockState, val result: Block) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            var newState = result.defaultBlockState()

            // В 1.21.1 getProperties() возвращает Collection<Property<?>>, а не Map.
            // Поэтому .keys не нужен, итерируемся напрямую.
            for (prop in old.properties) {
                if (newState.properties.contains(prop)) {
                    @Suppress("UNCHECKED_CAST")
                    val castedProp = prop as Property<Comparable<Any>>
                    // with() заменен на setValue(), get() на getValue()
                    newState = newState.setValue(castedProp, old.getValue(castedProp) as Comparable<Any>)
                }
            }
            env.world.setBlock(position, newState, 3)
        }
    }
}