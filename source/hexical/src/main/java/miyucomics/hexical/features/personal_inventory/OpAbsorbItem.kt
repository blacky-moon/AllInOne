package miyucomics.hexical.features.personal_inventory

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.getItemEntity
import at.petrak.hexcasting.api.casting.getPositiveIntUnder
import at.petrak.hexcasting.api.casting.iota.BooleanIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import kotlin.math.min

object OpAbsorbItem : SpellAction {
    override val argc = 2
    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val caster = env.castingEntity as? ServerPlayer ?: throw MishapBadCaster()
        val target = args.getItemEntity(env.world, 0, argc)
        env.assertEntityInRange(target)
        val slot = args.getPositiveIntUnder(1, caster.inventory.containerSize, argc)
        return SpellAction.Result(Spell(target, slot), 0, listOf())
    }

    private data class Spell(val target: ItemEntity, val slot: Int) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {}
        override fun cast(env: CastingEnvironment, image: CastingImage): CastingImage {
            val result = pullEntityIntoSlot(env.castingEntity as ServerPlayer, target, slot)
            return image.copy(stack = image.stack.toList().plus(BooleanIota(result)))
        }
    }

    private fun pullEntityIntoSlot(player: ServerPlayer, target: ItemEntity, slot: Int): Boolean {
        val intruder = target.getItem()
        if (intruder.isEmpty) return false

        val existing = player.inventory.getItem(slot)
        when {
            existing.isEmpty -> {
                player.inventory.setItem(slot, intruder.copy())
                target.discard()
                player.containerMenu.sendAllDataToRemote()
                return true
            }
            ItemStack.isSameItemSameComponents(existing, intruder) -> {
                val maxStackSize = min(existing.maxStackSize, player.inventory.maxStackSize)
                val remainingSpace = maxStackSize - existing.count

                if (remainingSpace > 0) {
                    val toMove = min(remainingSpace, intruder.count)

                    existing.grow(toMove)
                    intruder.shrink(toMove)

                    if (intruder.isEmpty) {
                        target.discard()
                    } else {
                        target.setItem(intruder)
                    }

                    player.containerMenu.sendAllDataToRemote()
                    return true
                }
            }
        }

        return false
    }
}