package miyucomics.hexical.features.circle

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.env.CircleCastEnv
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock
import at.petrak.hexcasting.api.casting.mishaps.circle.MishapNoSpellCircle
import miyucomics.hexical.features.pedestal.PedestalBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.phys.Vec3

object OpAbsorbArm : SpellAction {
    override val argc = 1

    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        if (env !is CircleCastEnv) throw MishapNoSpellCircle()
        val circle = env.impetus ?: throw MishapNoSpellCircle()
        val bounds = circle.executionState?.bounds ?: throw MishapNoSpellCircle()

        val pedestal = args.getBlockPos(0, argc)

        // BlockBox.contains принимает BlockPos напрямую, нет нужды конвертировать в Vec3 или AABB
        if (!bounds.contains(pedestal)) throw OutsideCircleMishap()

        if (env.world.getBlockEntity(pedestal) !is PedestalBlockEntity)
            throw MishapBadBlock.of(pedestal, "pedestal")

        return SpellAction.Result(
            Spell(pedestal),
            0,
            listOf(ParticleSpray.burst(Vec3.atCenterOf(pedestal), 1.0))
        )
    }

    private data class Spell(val pedestal: BlockPos) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {}

        override fun cast(env: CastingEnvironment, image: CastingImage): CastingImage {
            val newUserData: CompoundTag = image.userData.copy()
            newUserData.putIntArray("impetus_hand", intArrayOf(pedestal.x, pedestal.y, pedestal.z))
            return image.copy(userData = newUserData)
        }
    }
}