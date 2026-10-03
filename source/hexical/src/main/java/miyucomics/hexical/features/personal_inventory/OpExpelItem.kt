package miyucomics.hexical.features.personal_inventory

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.getPositiveIntUnder
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack

object OpExpelItem : SpellAction {
    override val argc = 1
    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val caster = env.castingEntity as? ServerPlayer ?: throw MishapBadCaster()
        val slot = args.getPositiveIntUnder(0, caster.inventory.containerSize, argc)
        return SpellAction.Result(Spell(slot), 0, listOf())
    }

    private data class Spell(val slot: Int) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {}
        override fun cast(env: CastingEnvironment, image: CastingImage): CastingImage {
            val caster = env.castingEntity as ServerPlayer
            val target = caster.inventory.getItem(slot)
            if (target.isEmpty) {
                return image.copy(stack = image.stack.toList().plus(NullIota()))
            }

            val copied = target.copy()
            caster.inventory.setItem(slot, ItemStack.EMPTY)

            val itemEntity: ItemEntity? = caster.drop(copied, false, true)
            if (itemEntity == null) {
                caster.inventory.setItem(slot, target)
                return image.copy(stack = image.stack.toList().plus(NullIota()))
            }

            caster.containerMenu.sendAllDataToRemote()
            return image.copy(stack = image.stack.toList().plus(EntityIota(itemEntity)))
        }
    }
}