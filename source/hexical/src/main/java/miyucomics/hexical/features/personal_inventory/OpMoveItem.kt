package miyucomics.hexical.features.personal_inventory

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.getPositiveIntUnder
import at.petrak.hexcasting.api.casting.iota.BooleanIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import kotlin.math.min

object OpMoveItem : SpellAction {
    override val argc = 2
    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val caster = env.castingEntity as? ServerPlayer ?: throw MishapBadCaster()
        val a = args.getPositiveIntUnder(0, caster.inventory.containerSize, argc)
        val b = args.getPositiveIntUnder(1, caster.inventory.containerSize, argc)
        return SpellAction.Result(Spell(a, b), 0, listOf())
    }

    private data class Spell(val a: Int, val b: Int) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {}
        override fun cast(env: CastingEnvironment, image: CastingImage): CastingImage {
            val result = swapItem(env.castingEntity as ServerPlayer, a, b)
            return image.copy(stack = image.stack.toList().plus(BooleanIota(result)))
        }
    }

    private fun swapItem(player: ServerPlayer, a: Int, b: Int): Boolean {
        val from = player.inventory.getItem(a)
        val to = player.inventory.getItem(b)

        when {
            to.isEmpty -> {
                player.inventory.setItem(a, ItemStack.EMPTY)
                player.inventory.setItem(b, from)
                return true
            }
            ItemStack.isSameItemSameComponents(from, to) -> {
                val maxStackSize = min(to.maxStackSize, player.inventory.maxStackSize)
                val remainingSpace = maxStackSize - to.count

                if (remainingSpace > 0) {
                    val toMove = min(remainingSpace, from.count)
                    to.grow(toMove)
                    from.shrink(toMove)
                    player.containerMenu.sendAllDataToRemote()
                    return true
                }
            }
        }

        return false
    }
}