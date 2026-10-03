package miyucomics.hexical.features.jailbreak

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.common.items.magic.ItemPackagedHex
import miyucomics.hexical.features.charms.CharmUtilities
import miyucomics.hexical.features.curios.CurioItem
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData

object OpJailbreakDevice : SpellAction {
    override val argc = 0
    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val heldData = env.getHeldItemToOperateOn { it.item is ItemPackagedHex || it.item is CurioItem }
        if (heldData == null)
            throw MishapBadOffhandItem.of(null, "crackable_item")

        val stack = heldData.stack
        if (stack.item is ItemPackagedHex && (stack.item as ItemPackagedHex).hasHex(stack))
            throw IllegalJailbreakMishap()

        if (stack.item is CurioItem && CharmUtilities.isStackCharmed(stack))
            throw IllegalJailbreakMishap()

        return SpellAction.Result(Spell(stack), MediaConstants.CRYSTAL_UNIT, listOf())
    }

    private data class Spell(val stack: ItemStack) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            // Читаем CustomData (аналог старого NBT), модифицируем и записываем обратно
            val customData = stack.get(DataComponents.CUSTOM_DATA)
            val tag = customData?.copyTag() ?: CompoundTag()
            tag.putBoolean("cracked", true)
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag))
        }
    }
}