package miyucomics.hexical.features.grimoires

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getList
import at.petrak.hexcasting.api.casting.getPattern
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem
import miyucomics.hexical.inits.HexicalItems
import miyucomics.hexical.misc.CastingUtils
import miyucomics.hexical.misc.HexSerialization
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData
import net.minecraft.nbt.CompoundTag

object OpGrimoireWrite : SpellAction {
    override val argc = 2
    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val itemInfo = env.getHeldItemToOperateOn { stack -> stack.`is`(HexicalItems.GRIMOIRE_ITEM.get()) }
        if (itemInfo == null)
            throw MishapBadOffhandItem.of(null, "grimoire")

        val stack = itemInfo.stack
        val customData = stack.get(DataComponents.CUSTOM_DATA)
        val tag = customData?.copyTag() ?: CompoundTag()

        if (tag.contains("expansions") && tag.getCompound("expansions").allKeys.size > 512)
            throw MishapBadOffhandItem.of(null, "nonfull_grimoire")

        OpGrimoireIndex.populateGrimoireMetadata(stack)
        CastingUtils.assertNoTruename(args[1], env)

        return SpellAction.Result(
            Spell(stack, args.getPattern(0, argc), args.getList(1, argc).toList()),
            0,
            listOf()
        )
    }

    private data class Spell(
        val stack: ItemStack,
        val key: HexPattern,
        val expansion: List<Iota>
    ) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            val customData = stack.get(DataComponents.CUSTOM_DATA)
            val tag = customData?.copyTag() ?: CompoundTag()

            if (!tag.contains("expansions"))
                tag.put("expansions", CompoundTag())

            val lookup = env.world.registryAccess()

            tag.getCompound("expansions").put(
                key.anglesSignature(),
                HexSerialization.serializeHex(expansion, lookup)
            )

            if (!tag.contains("metadata"))
                tag.put("metadata", CompoundTag())

            val data = CompoundTag()
            data.putInt("direction", key.startDir.ordinal)
            tag.getCompound("metadata").put(key.anglesSignature(), data)

            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag))
        }
    }
}