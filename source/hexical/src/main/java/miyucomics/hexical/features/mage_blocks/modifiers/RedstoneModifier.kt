package miyucomics.hexical.features.mage_blocks.modifiers

import at.petrak.hexcasting.api.casting.getPositiveIntUnderInclusive
import at.petrak.hexcasting.api.casting.iota.Iota
import com.mojang.datafixers.util.Pair as DfuPair
import miyucomics.hexical.HexicalMain
import miyucomics.hexical.features.mage_blocks.MageBlockModifier
import miyucomics.hexical.features.mage_blocks.MageBlockModifierType
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.IntTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

class RedstoneModifier : MageBlockModifier {
    override val type: MageBlockModifierType<*> = TYPE
    var power = 0

    override fun getScryingLens(): DfuPair<ItemStack, Component> =
        DfuPair(ItemStack(Items.REDSTONE), Component.translatable("hexical.mage_block.redstone", power))

    override fun serialize(registries: HolderLookup.Provider): Tag = IntTag.valueOf(power)

    companion object {
        val TYPE: MageBlockModifierType<RedstoneModifier> = object : MageBlockModifierType<RedstoneModifier>() {
            override val argc: Int = 1
            override val id = HexicalMain.id("redstone")
            override fun construct(args: List<Iota>) = RedstoneModifier().also {
                it.power = args.getPositiveIntUnderInclusive(1, 15, 2)
            }
            override fun deserialize(element: Tag, registries: HolderLookup.Provider) =
                // ИСПРАВЛЕНО: используем стандартный Mojang-каст вместо extension-свойства asInt
                RedstoneModifier().also { it.power = (element as IntTag).asInt }
        }
    }
}