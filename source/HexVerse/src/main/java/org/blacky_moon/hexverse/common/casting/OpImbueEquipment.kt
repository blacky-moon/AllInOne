package org.blacky_moon.hexverse.common.casting

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.iota.PatternIota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import org.blacky_moon.hexverse.common.item.HexHolder
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.item.ItemStack
import org.blacky_moon.hexverse.common.item.HexSnowballItem

class OpImbueEquipment : ConstMediaAction {

    override val argc: Int = 1

    override val mediaCost: Long = 5 * MediaConstants.SHARD_UNIT

    @Throws(Mishap::class)
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val rawArg = args[0]
        val listIota = rawArg as? ListIota
            ?: throw MishapInvalidIota(
                rawArg, 0,
                Component.translatable("hexverse.mishap.invalid_iota.list")
            )

        val patterns = ArrayList<Iota>()
        for (iota in listIota.list) {
            if (iota is PatternIota) {
                patterns.add(iota)
            } else {
                throw MishapInvalidIota(
                    iota, 0,
                    Component.translatable("hexverse.mishap.invalid_iota.pattern")
                )
            }
        }

        val targetStack = getItemStack(env)

        if (targetStack.isEmpty || targetStack.item !is HexHolder) {
            throw MishapBadOffhandItem(
                targetStack.copy(),
                Component.translatable("hexverse.mishap.bad_offhand.equipment")
            )
        }

        val hexHolder = targetStack.item as HexHolder
        val pigment = env.pigment
        hexHolder.writeHex(targetStack, patterns, pigment, 0)
        return listOf()
    }

    private fun getItemStack(env: CastingEnvironment): ItemStack {
        val castingHand = env.castingHand
        val otherHand = if (castingHand == InteractionHand.MAIN_HAND)
            InteractionHand.OFF_HAND
        else
            InteractionHand.MAIN_HAND

        val caster = env.castingEntity as? ServerPlayer
            ?: throw MishapBadOffhandItem(
                ItemStack.EMPTY,
                Component.translatable("hexverse.mishap.no_player")
            )

        return caster.getItemInHand(otherHand)
    }
}