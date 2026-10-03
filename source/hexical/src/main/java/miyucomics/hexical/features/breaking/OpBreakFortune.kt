package miyucomics.hexical.features.breaking

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.getPositiveIntUnderInclusive
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.mod.HexConfig
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.core.registries.Registries
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.block.Block

object OpBreakFortune : SpellAction {
    override val argc = 2
    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val pos = args.getBlockPos(0, argc)
        env.assertPosInRange(pos)
        val level = args.getPositiveIntUnderInclusive(1, 2, argc)
        return SpellAction.Result(Spell(pos, level), MediaConstants.DUST_UNIT + level * MediaConstants.DUST_UNIT * 2, listOf())
    }

    private data class Spell(val pos: BlockPos, val level: Int) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            val state = env.world.getBlockState(pos)
            if (
                !state.isAir
                && state.getDestroySpeed(env.world, pos) >= 0f
                && IXplatAbstractions.INSTANCE.isBreakingAllowed(env.world, pos, state, env.castingEntity as? ServerPlayer)
            ) {
                val blockEntity = env.world.getBlockEntity(pos)
                val tool = ItemStack(Items.DIAMOND_PICKAXE)
                val registry = env.world.registryAccess().registryOrThrow(Registries.ENCHANTMENT)
                val fortuneHolder = registry.getHolder(Enchantments.FORTUNE)
                if (fortuneHolder.isPresent) {
                    tool.enchant(fortuneHolder.get(), level + 1)
                }
                Block.getDrops(state, env.world, pos, blockEntity, null, tool)
                    .forEach { drop ->
                        Block.popResource(env.world, pos, drop)
                    }
                env.world.removeBlock(pos, false)
            }
        }
    }
}