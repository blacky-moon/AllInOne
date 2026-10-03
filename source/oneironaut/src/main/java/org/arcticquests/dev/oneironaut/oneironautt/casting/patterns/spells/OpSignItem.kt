package org.arcticquests.dev.oneironaut.oneironautt.casting.patterns.spells

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment.HeldItemInfo
import at.petrak.hexcasting.api.casting.eval.env.StaffCastEnv
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.component.CustomData
import org.arcticquests.dev.oneironaut.oneironautt.casting.mishaps.MishapNoStaff

class OpSignItem : ConstMediaAction {
    override val argc = 0
    override val mediaCost = MediaConstants.DUST_UNIT

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        if (env.castingEntity == null || env.castingEntity !is ServerPlayer) {
            throw MishapBadCaster()
        }
        if (env !is StaffCastEnv) {
            throw MishapNoStaff(Component.translatable("hexcasting.spell.oneironaut:signitem"))
        }

        // can be used on any item
        val heldInfo: HeldItemInfo = env.getHeldItemToOperateOn { it != null }
            ?: throw MishapBadOffhandItem(null, Component.nullToEmpty("no_item.offhand"))
        val itemToSign = heldInfo.stack

        val casterUuid = env.castingEntity!!.uuid

        // Pull the existing CUSTOM_DATA tag (or start a fresh one)
        val existingData = itemToSign.get(DataComponents.CUSTOM_DATA)
        val tag: CompoundTag = existingData?.copyTag() ?: CompoundTag()

        val existingSignature = if (tag.hasUUID("soulprint_signature")) {
            tag.getUUID("soulprint_signature")
        } else {
            null
        }

        if (existingSignature != null && existingSignature == casterUuid) {
            // Same caster re-signing -> clear the signature
            tag.remove("soulprint_signature")
        } else {
            // No signature, or different caster -> (re)sign it
            tag.putUUID("soulprint_signature", casterUuid)
        }

        itemToSign.set(DataComponents.CUSTOM_DATA, CustomData.of(tag))

        return listOf()
    }
}