package miyucomics.hexical.features.personal_inventory

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import miyucomics.hexpose.iotas.ItemStackIota
import net.minecraft.server.level.ServerPlayer

object OpListPossessions : ConstMediaAction {
    override val argc = 0
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val caster = env.castingEntity as? ServerPlayer ?: throw MishapBadCaster()
        return (0 until caster.inventory.containerSize).mapNotNull { slot ->
            val stack = caster.inventory.getItem(slot)
            if (stack.isEmpty) null else ItemStackIota.createOptimized(stack)
        }.asActionResult
    }
}