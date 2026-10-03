package miyucomics.hexical.features.pigments

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.getItemEntity
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.item.PigmentItem
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.common.lib.HexItems
import at.petrak.hexcasting.xplat.IXplatAbstractions
import miyucomics.hexical.features.dyes.DyeIota
import miyucomics.hexical.features.dyes.getColoredDye
import miyucomics.hexpose.iotas.IdentifierIota
import miyucomics.hexpose.iotas.ItemStackIota

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.DyeItem
import net.minecraft.world.item.ItemStack

object OpToPigment : ConstMediaAction {
    override val argc = 1
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        if (env.castingEntity == null)
            throw MishapBadCaster()
        val caster = env.castingEntity as LivingEntity

        val colorizer = when (args[0]) {
            is DyeIota -> {
                val dyeColor = args.getColoredDye(0, argc)
                val pigmentItem = HexItems.DYE_PIGMENTS[dyeColor]!!
                FrozenPigment(ItemStack(pigmentItem), caster.uuid)
            }
            is EntityIota -> {
                when (val entity = args.getEntity(env.world, 0, argc)) {
                    is Player -> IXplatAbstractions.INSTANCE.getPigment(entity)
                    is ItemEntity -> {
                        val item = args.getItemEntity(env.world, 0, argc)
                        env.assertEntityInRange(item)
                        val stack = item.item
                        if (stack.item is PigmentItem)
                            FrozenPigment(stack, caster.uuid)
                        else
                            null
                    }
                    else -> null
                }
            }
            is IdentifierIota -> when (val item = BuiltInRegistries.ITEM.get(IdentifierIota.getIdentifier(args,0, argc))) {
                is PigmentItem -> FrozenPigment(ItemStack(item), caster.uuid)
                is DyeItem -> {
                    val dyeColor = item.dyeColor
                    val pigmentItem = HexItems.DYE_PIGMENTS[dyeColor]!!
                    FrozenPigment(ItemStack(pigmentItem), caster.uuid)
                }
                else -> null
            }
            is ItemStackIota -> {
                val stack = ItemStackIota.getItemStack(args,0, argc)
                when (stack.item) {
                    is PigmentItem -> FrozenPigment(stack, caster.uuid)
                    is DyeItem -> {
                        val dyeColor = (stack.item as DyeItem).dyeColor
                        val pigmentItem = HexItems.DYE_PIGMENTS[dyeColor]!!
                        FrozenPigment(ItemStack(pigmentItem), caster.uuid)
                    }
                    else -> null
                }
            }
            else -> null
        }

        if (colorizer == null)
            throw MishapInvalidIota.of(args[0], 0, "to_pigment")

        return listOf(PigmentIota(colorizer))
    }
}