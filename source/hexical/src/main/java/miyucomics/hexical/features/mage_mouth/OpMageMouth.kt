package miyucomics.hexical.features.mage_mouth

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getItemEntity
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import at.petrak.hexcasting.api.misc.MediaConstants
import miyucomics.hexical.inits.HexicalItems
import net.minecraft.core.component.DataComponents
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

object OpMageMouth : SpellAction {
    override val argc = 1
    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        if (env.castingEntity !is ServerPlayer)
            throw MishapBadCaster()

        val itemEntity = args.getItemEntity(env.world, 0, argc)
        val attemptToEat = itemEntity.item

        val isEdible = attemptToEat.`is`(Items.POTION) ||
                attemptToEat.`is`(Items.HONEY_BOTTLE) ||
                attemptToEat.`is`(Items.MILK_BUCKET) ||
                attemptToEat.has(DataComponents.FOOD)

        if (isEdible && !attemptToEat.`is`(HexicalItems.CAN_NOT_MAGE_MOUTH_TAG)) {
            return SpellAction.Result(Spell(itemEntity, attemptToEat), MediaConstants.DUST_UNIT, listOf())
        }
        throw InedibleItemMishap()
    }

    private data class Spell(val itemEntity: ItemEntity, val toEat: ItemStack) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            val caster = env.castingEntity as ServerPlayer
            val originalStack = caster.getItemInHand(env.castingHand)

            val count = toEat.count

            repeat(count) {
                caster.setItemInHand(env.castingHand, toEat)
                val leftover = toEat.finishUsingItem(env.world, caster)
                itemEntity.item = leftover
            }

            caster.setItemInHand(env.castingHand, originalStack)
        }
    }
}