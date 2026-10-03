package org.arcticquests.dev.oneironaut.oneironautt.casting.patterns.spells.great

import org.arcticquests.dev.oneironaut.oneironautt.network.ItemUpdatePacket
import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.core.Holder
import net.minecraft.core.component.DataComponents
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.EnchantedBookItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.item.enchantment.ItemEnchantments
import org.arcticquests.dev.oneironaut.oneironautt.casting.mishaps.MishapMissingEnchant
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautMiscRegistry
import ram.talia.hexal.api.getItemEntityOrItemFrame
import kotlin.math.pow

class OpApplyOvercastDamage : SpellAction {
    override val argc = 1
    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val holder = args.getEntity(env.world,0, argc)
        val target = args.getItemEntityOrItemFrame(0, argc,env.world)
        env.assertEntityInRange(holder)
        val stack: ItemStack = if (!(target.left().isEmpty)) {
            target.left().get().item
        } else {
            target.right().get().item
        }

        val registryAccess = env.world.registryAccess()
        val sharpnessHolder: Holder<Enchantment> = registryAccess
            .lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
            .getOrThrow(Enchantments.SHARPNESS)

        val level: Int
        val isBook = stack.item is EnchantedBookItem

        if (isBook) {
            val stackEnchants: ItemEnchantments = stack.get(DataComponents.STORED_ENCHANTMENTS)
                ?: ItemEnchantments.EMPTY
            level = stackEnchants.getLevel(sharpnessHolder)
            if (level <= 0) {
                throw MishapMissingEnchant(stack, sharpnessHolder.value())
            }
            return SpellAction.Result(
                Spell(stack, level, true, stackEnchants, holder, sharpnessHolder),
                (level.toDouble().pow(2) * MediaConstants.CRYSTAL_UNIT * 15).toLong(),
                listOf(ParticleSpray.cloud(holder.position(), 2.0))
            )
        } else {
            val stackEnchants: ItemEnchantments = stack.get(DataComponents.ENCHANTMENTS)
                ?: ItemEnchantments.EMPTY
            level = stackEnchants.getLevel(sharpnessHolder)
            if (level <= 0) {
                throw MishapMissingEnchant(stack, sharpnessHolder.value())
            }
            return SpellAction.Result(
                Spell(stack, level, false, stackEnchants, holder, sharpnessHolder),
                (level.toDouble().pow(2) * MediaConstants.CRYSTAL_UNIT * 10).toLong(),
                listOf(ParticleSpray.cloud(holder.position(), 2.0))
            )
        }
    }

    private data class Spell(
        val stack: ItemStack,
        val level: Int,
        val book: Boolean,
        val existingEnchantments: ItemEnchantments,
        val holder: Entity,
        val sharpnessHolder: Holder<Enchantment>
    ) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            val registryAccess = env.world.registryAccess()
            val enchantmentRegistry = registryAccess.lookupOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
            val overcastDamageHolder: Holder<Enchantment> = enchantmentRegistry.getOrThrow(OneironautMiscRegistry.OVERCAST_DAMAGE_ENCHANT)

            val mutable = ItemEnchantments.Mutable(ItemEnchantments.EMPTY)


            for (enchHolder in existingEnchantments.keySet()) {
                if (Enchantment.areCompatible(overcastDamageHolder, enchHolder)) {
                    mutable.set(enchHolder, existingEnchantments.getLevel(enchHolder))
                }
            }

            mutable.set(overcastDamageHolder, level)

            val component = mutable.toImmutable()
            if (book) {
                stack.set(DataComponents.STORED_ENCHANTMENTS, component)
            } else {
                stack.set(DataComponents.ENCHANTMENTS, component)
            }

            IXplatAbstractions.INSTANCE.sendPacketNear(
                stack.entityRepresentation?.position(),
                128.0,
                env.world,
                ItemUpdatePacket(stack, holder.id)
            )
        }
    }
}