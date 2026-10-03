package miyucomics.hexical.features.spike

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.getPositiveDoubleUnderInclusive
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import miyucomics.hexical.inits.HexicalEntities
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import kotlin.math.floor
import kotlin.math.abs

object OpConjureSpike : SpellAction {
    override val argc = 3

    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val position = args.getBlockPos(0, argc)
        env.assertPosInRange(position)

        val offset = args.getBlockPos(1, argc)

        // Определяем направление по смещению.
        // В 1.21+ нет Direction.fromVector(int, int, int), поэтому используем логику
        val direction = getDirectionFromOffset(offset)
            ?: throw MishapInvalidIota.of(args[1], 1, "axis_vector")

        // Проверка блока на прочность грани
        val blockState = env.world.getBlockState(position)
        if (!blockState.isFaceSturdy(env.world, position, direction)) {
            throw MishapBadBlock.of(position, "solid_platform")
        }

        // Проверка на наличие других шипов
        // Box заменен на AABB, Vec3d на Vec3
        val center = Vec3.atCenterOf(position.offset(offset))
        val box = AABB.ofSize(center, 0.9, 0.9, 0.9)

        val existingSpikes = env.world.getEntitiesOfClass(
            SpikeEntity::class.java,
            box
        ) { true }

        if (existingSpikes.isNotEmpty()) {
            return SpellAction.Result(Noop(position), 0, listOf())
        }

        val delay = floor(args.getPositiveDoubleUnderInclusive(2, 10.0, argc) * 20.0).toInt()

        // Vec3.atBottomCenterOf заменяет Vec3d.ofBottomCenter
        val spawn = Vec3.atBottomCenterOf(position).add(Vec3.atLowerCornerOf(direction.normal))

        return SpellAction.Result(
            Spell(spawn, direction, delay),
            MediaConstants.DUST_UNIT,
            listOf(ParticleSpray.cloud(spawn, 1.0))
        )
    }

    private fun getDirectionFromOffset(offset: BlockPos): Direction? {
        // Находим ось с наибольшим абсолютным значением
        val x = abs(offset.x)
        val y = abs(offset.y)
        val z = abs(offset.z)

        return when {
            x >= y && x >= z -> if (offset.x > 0) Direction.EAST else Direction.WEST
            y >= x && y >= z -> if (offset.y > 0) Direction.UP else Direction.DOWN
            else -> if (offset.z > 0) Direction.SOUTH else Direction.NORTH
        }
    }

    private data class Noop(val position: BlockPos) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {}
    }

    private data class Spell(val position: Vec3, val direction: Direction, val delay: Int) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            // Создаем сущность
            val spike = SpikeEntity(
                env.world as net.minecraft.server.level.ServerLevel,
                position.x,
                position.y,
                position.z,
                direction,
                delay
            )

            val caster = env.castingEntity
            if (caster is Player) {
                spike.setConjurer(caster)
            }

            // spawnEntity заменен на addFreshEntity
            env.world.addFreshEntity(spike)
        }
    }
}