package miyucomics.hexical.features.confection

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getList
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import miyucomics.hexical.inits.HexicalAdvancements
import miyucomics.hexical.inits.HexicalComponents
import miyucomics.hexical.inits.HexicalItems
import miyucomics.hexical.misc.CastingUtils
import miyucomics.hexical.misc.HexSerialization
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.Vec3

object OpConjureHextito : SpellAction {
    override val argc = 2

    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val position = args.getVec3(0, argc)
        env.assertVecInRange(position)
        CastingUtils.assertNoTruename(args[1], env)
        return SpellAction.Result(
            Spell(position, args.getList(1, argc).toList()),
            MediaConstants.DUST_UNIT / 2,
            listOf(ParticleSpray.burst(position, 1.0))
        )
    }

    private data class Spell(val position: Vec3, val hex: List<Iota>) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            val stack = ItemStack(HexicalItems.HEXTITO_ITEM.get(), 1)

            // В методе cast:
            val listTag = HexSerialization.serializeHex(hex, env.world.registryAccess())
            val wrapper = CompoundTag().apply { put("hex", listTag) }
            stack.set(HexicalComponents.HEXTITO_HEX.get(), wrapper)

            val entity = ItemEntity(env.world, position.x, position.y, position.z, stack)
            entity.setPickUpDelay(1)
            env.world.addFreshEntity(entity)

            if (env is HextitoCastEnv) {
                val caster = env.caster
                if (caster is ServerPlayer) {
                    HexicalAdvancements.HEXTITO_QUINE.trigger(caster)
                }
            }
        }
    }
}