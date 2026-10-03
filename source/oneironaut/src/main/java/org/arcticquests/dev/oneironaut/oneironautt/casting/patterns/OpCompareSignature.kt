package org.arcticquests.dev.oneironaut.oneironautt.casting.patterns

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.BooleanIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem
import at.petrak.hexcasting.api.misc.MediaConstants
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Items
import org.arcticquests.dev.oneironaut.oneironautt.getSoulprint
import java.util.UUID

class OpCompareSignature : ConstMediaAction {
    override val argc: Int = 1
    override val mediaCost: Long = MediaConstants.DUST_UNIT / 10

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val checkedSignature = args.getSoulprint(0, argc)

        val readItemInfo: CastingEnvironment.HeldItemInfo =
            env.getHeldItemToOperateOn { it.item != Items.AIR }
                ?: throw MishapBadOffhandItem(null, Component.translatable("no_item.offhand"))

        val readItem = readItemInfo.stack

        val customData = readItem.get(DataComponents.CUSTOM_DATA)
        if (customData != null) {
            val tag: CompoundTag = customData.copyTag()
            if (tag.hasUUID("soulprint_signature")) {
                val signature: UUID = tag.getUUID("soulprint_signature")
                if (signature == checkedSignature) {
                    return listOf(BooleanIota(true))
                }
            }
        }

        return listOf(BooleanIota(false))
    }
}