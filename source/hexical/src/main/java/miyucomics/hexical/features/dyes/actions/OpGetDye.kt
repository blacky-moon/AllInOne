package miyucomics.hexical.features.dyes.actions

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.iota.*
import miyucomics.hexical.features.dyes.DyeIota
import miyucomics.hexical.features.dyes.DyeOption
import miyucomics.hexical.features.dyes.DyeingUtils
import miyucomics.hexpose.iotas.IdentifierIota
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.server.level.ServerLevel
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.animal.Cat
import net.minecraft.world.entity.animal.Sheep
import net.minecraft.world.entity.animal.Wolf
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.monster.Shulker
import net.minecraft.world.item.BlockItem
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SignBlock
import net.minecraft.world.level.block.entity.SignBlockEntity

object OpGetDye : ConstMediaAction {
    override val argc = 1

    override fun execute(args: List<Iota>, env: CastingEnvironment) = listOf(
        when (args[0]) {
            is EntityIota -> {
                val entity = args.getEntity(env.world,0, argc)
                env.assertEntityInRange(entity)
                processEntity(entity)
            }
            is IdentifierIota -> {
                when (val item = BuiltInRegistries.ITEM.get(IdentifierIota.getIdentifier(args,0, argc))) {
                    is BlockItem -> getDyeFromBlock(item.block)
                    else -> {
                        if (DyeingUtils.getDye(item) != null)
                            DyeIota(DyeingUtils.getDye(item)!!)
                        else
                            NullIota()
                    }
                }
            }
            is Vec3Iota -> {
                val position = args.getBlockPos(0, argc)
                env.assertPosInRange(position)
                processVec3d(position, env.world as ServerLevel)
            }
            else -> NullIota()
        }
    )

    private fun processEntity(entity: Entity): Iota {
        return when (entity) {
            is Cat -> DyeIota(DyeOption.fromDyeColor(entity.collarColor))
            is ItemEntity -> {
                when (val item = entity.item.item) {
                    is BlockItem -> getDyeFromBlock(item.block)
                    else -> {
                        if (DyeingUtils.getDye(item) != null)
                            DyeIota(DyeingUtils.getDye(item)!!)
                        else
                            NullIota()
                    }
                }
            }
            is Sheep -> DyeIota(DyeOption.fromDyeColor(entity.color))
            is Shulker -> DyeIota(DyeOption.fromDyeColor(entity.variant.orElse(null)))
            is Wolf -> DyeIota(DyeOption.fromDyeColor(entity.collarColor))
            else -> NullIota()
        }
    }

    private fun processVec3d(position: BlockPos, world: ServerLevel): Iota {
        val state = world.getBlockState(position)
        if (state.block is SignBlock) {
            val sign = world.getBlockEntity(position) as SignBlockEntity
            return ListIota(listOf(
                DyeIota(DyeOption.fromDyeColor(sign.frontText.color)),
                DyeIota(DyeOption.fromDyeColor(sign.backText.color))
            ))
        }
        return getDyeFromBlock(world.getBlockState(position).block)
    }

    private fun getDyeFromBlock(block: Block): Iota {
        val dye = DyeingUtils.getDye(block) ?: return NullIota()
        return DyeIota(dye)
    }
}