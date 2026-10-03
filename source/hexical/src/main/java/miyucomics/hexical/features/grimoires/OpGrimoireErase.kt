package miyucomics.hexical.features.grimoires

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getPattern
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem
import miyucomics.hexical.inits.HexicalItems
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData
import net.minecraft.nbt.CompoundTag

object OpGrimoireErase : SpellAction {
    override val argc = 1
    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val itemInfo = env.getHeldItemToOperateOn { stack -> stack.`is`(HexicalItems.GRIMOIRE_ITEM.get()) }
        if (itemInfo == null)
            throw MishapBadOffhandItem.of(null, "grimoire")
        val stack = itemInfo.stack
        OpGrimoireIndex.populateGrimoireMetadata(stack)
        val pattern = args.getPattern(0, argc)
        return SpellAction.Result(Spell(stack, pattern), 0, listOf())
    }

    private data class Spell(val stack: ItemStack, val key: HexPattern) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            val customData = stack.get(DataComponents.CUSTOM_DATA) ?: return

            val tag = customData.copyTag()
            if (!tag.contains("expansions")) return

            tag.getCompound("expansions").remove(key.anglesSignature())
            tag.getCompound("metadata").remove(key.anglesSignature())

            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag))
        }
    }
}