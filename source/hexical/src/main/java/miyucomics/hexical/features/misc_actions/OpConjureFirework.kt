package miyucomics.hexical.features.misc_actions

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getBool
import at.petrak.hexcasting.api.casting.getIntBetween
import at.petrak.hexcasting.api.casting.getList
import at.petrak.hexcasting.api.casting.getPositiveIntUnderInclusive
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.Vec3Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import it.unimi.dsi.fastutil.ints.IntList
import net.minecraft.core.component.DataComponents
import net.minecraft.world.entity.projectile.FireworkRocketEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.FireworkExplosion
import net.minecraft.world.item.component.Fireworks
import net.minecraft.world.phys.Vec3

object OpConjureFirework : SpellAction {
    override val argc = 8
    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val position = args.getVec3(0, argc)
        env.assertVecInRange(position)
        val velocity = args.getVec3(1, argc)
        val duration = args.getIntBetween(2, 1, 3, argc)
        val shape = args.getPositiveIntUnderInclusive(3, 4, argc)
        val colors = args.getList(4, argc)
        if (!colors.nonEmpty)
            throw MishapInvalidIota.of(args[3], 3, "nonempty_list")
        val trueColors = colors.map {
            if (it !is Vec3Iota) throw MishapInvalidIota.of(args[3], 3, "vector_list")
            translateVectorToColor(it.vec3)
        }
        val fades = args.getList(5, argc).map {
            if (it !is Vec3Iota) throw MishapInvalidIota.of(args[3], 2, "vector_list")
            translateVectorToColor(it.vec3)
        }
        val flicker = args.getBool(6, argc)
        val trail = args.getBool(7, argc)
        return SpellAction.Result(Spell(position, velocity, duration, shape, trueColors, fades, flicker, trail), MediaConstants.SHARD_UNIT, listOf(ParticleSpray.burst(position, 1.0)))
    }

    private data class Spell(
        val position: Vec3, val desiredVelocity: Vec3, val duration: Int, val shape: Int,
        val colors: List<Int>, val fades: List<Int>, val flicker: Boolean, val trail: Boolean
    ) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            // ИСПРАВЛЕНО: Полный переход с NBT на Data Components для фейерверков
            val explosion = FireworkExplosion(
                FireworkExplosion.Shape.entries[shape],
                colors as IntList, fades as IntList, trail, flicker
            )
            val fireworksComponent = Fireworks(duration, listOf(explosion))

            val fireworkStack = ItemStack(Items.FIREWORK_ROCKET)
            fireworkStack.set(DataComponents.FIREWORKS, fireworksComponent)

            val rocket = FireworkRocketEntity(env.world, fireworkStack, position.x, position.y, position.z, true)
            rocket.setDeltaMovement(desiredVelocity.x, desiredVelocity.y, desiredVelocity.z)
            env.world.addFreshEntity(rocket)
        }
    }

    private fun translateVectorToColor(vector: Vec3): Int {
        val r = (vector.x.coerceIn(0.0, 1.0) * 255).toInt()
        val g = (vector.y.coerceIn(0.0, 1.0) * 255).toInt()
        val b = (vector.z.coerceIn(0.0, 1.0) * 255).toInt()
        return (255 shl 24) or (r shl 16) or (g shl 8) or b
    }
}