package miyucomics.hexical.features.mage_hand

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getBool
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.Vec3

object OpMageHand : SpellAction {
    override val argc = 4
    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        // ИСПРАВЛЕНО: Получаем уровень через сущность кастера
        val level = env.world as ServerLevel

        val toUse = when (val tool = args[0]) {
            is EntityIota -> {
                val entity = tool.getEntity(level)
                if (entity is ItemEntity) {
                    env.assertEntityInRange(entity)
                    entity
                } else {
                    throw MishapInvalidIota.of(tool, 0, "item_entity_or_null")
                }
            }
            is NullIota -> null
            else -> throw MishapInvalidIota.of(tool, 0, "item_entity_or_null")
        }

        val position = args.getVec3(1, argc)
        env.assertVecInRange(position)
        val rotation = args.getVec3(2, argc).normalize()
        return SpellAction.Result(Spell(toUse, position, rotation, args.getBool(3, argc)), MediaConstants.DUST_UNIT, listOf())
    }

    private data class Spell(val toUse: ItemEntity?, val position: Vec3, val rotation: Vec3, val sneak: Boolean) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            // ИСПРАВЛЕНО: Получаем уровень через сущность кастера
            val level = env.world
            val tool = toUse?.item ?: ItemStack.EMPTY
            val dispense = toUse?.position() ?: position

            FakeInteractionUtils.rightClickAt(level, tool, env.castingEntity as? ServerPlayer, position, rotation, sneak = sneak).forEach {
                val droppedItem = ItemEntity(level, dispense.x, dispense.y, dispense.z, it)
                level.addFreshEntity(droppedItem)
            }
            toUse?.discard()
        }
    }
}