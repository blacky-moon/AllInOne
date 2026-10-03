package miyucomics.hexical.features.flora

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import miyucomics.hexpose.iotas.IdentifierIota
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.BlockTags
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.DoublePlantBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3

object OpConjureFlora : SpellAction {
    override val argc = 2

    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val position = args.getBlockPos(0, argc)
        env.assertPosInRange(position)

        val id = IdentifierIota.getIdentifier(args,1, argc)
        if (!BuiltInRegistries.BLOCK.containsKey(id))
            throw MishapInvalidIota.of(args[1], 0, "conjurable_flora_id")

        val targetBlock = BuiltInRegistries.BLOCK.get(id)
        val recipe = ConjureFloraHook.getRecipe(env.world as net.minecraft.server.level.ServerLevel, targetBlock)
            ?: throw MishapInvalidIota.of(args[1], 0, "conjurable_flora_id")

        val targetState = env.world.getBlockState(position)

        // ИСПРАВЛЕНО: POTTED_PLANTS удален. Ищем potted_<name> в реестре блоков.
        if (targetState.`is`(BlockTags.FLOWER_POTS)) {
            val blockName = BuiltInRegistries.BLOCK.getKey(recipe.state.block).path
            val pottedId = ResourceLocation.withDefaultNamespace("potted_$blockName")
            val pottedBlockOpt = BuiltInRegistries.BLOCK.getOptional(pottedId)

            if (pottedBlockOpt.isPresent) {
                return SpellAction.Result(
                    // ИСПРАВЛЕНО: defaultBlockState() требует скобок
                    PotPlant(position, pottedBlockOpt.get().defaultBlockState()),
                    recipe.cost,
                    listOf(ParticleSpray.cloud(Vec3.atCenterOf(position), 1.0))
                )
            }
        }

        if (!targetState.canBeReplaced())
            throw MishapBadBlock.of(position, "flower_spawnable")

        // ИСПРАВЛЕНО: TallPlantBlock -> DoublePlantBlock
        if (recipe.state.hasProperty(DoublePlantBlock.HALF) && !env.world.getBlockState(position.above()).canBeReplaced())
            throw MishapBadBlock.of(position.above(), "flower_spawnable")

        if (!env.world.getBlockState(position.below()).isFaceSturdy(env.world, position.below(), Direction.UP))
            throw MishapBadBlock.of(position.below(), "solid_platform")

        return SpellAction.Result(
            GroundPlant(position, recipe.state),
            recipe.cost,
            listOf(ParticleSpray.cloud(Vec3.atCenterOf(position), 1.0))
        )
    }

    private data class GroundPlant(val position: BlockPos, val flower: BlockState) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            if (flower.block is DoublePlantBlock) {
                DoublePlantBlock.placeAt(env.world, flower, position, Block.UPDATE_ALL or Block.UPDATE_KNOWN_SHAPE)
            } else {
                env.world.setBlock(position, flower, 3)
            }
        }
    }

    private data class PotPlant(val position: BlockPos, val flower: BlockState) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            env.world.setBlock(position, flower, 3)
        }
    }
}