package miyucomics.hexical.features.mage_blocks.modifiers

import at.petrak.hexcasting.api.casting.iota.Iota
import com.mojang.datafixers.util.Pair as DfuPair
import miyucomics.hexical.HexicalMain
import miyucomics.hexical.features.mage_blocks.MageBlockModifier
import miyucomics.hexical.features.mage_blocks.MageBlockModifierType
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.ByteTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

class VolatileModifier : MageBlockModifier {
    override val type: MageBlockModifierType<*> = TYPE

    override fun getScryingLens(): DfuPair<ItemStack, Component> =
        DfuPair(ItemStack(Items.TNT), Component.translatable("hexical.mage_block.volatile"))

    override fun serialize(registries: HolderLookup.Provider): Tag = ByteTag.valueOf(0)

    companion object {
        val TYPE: MageBlockModifierType<VolatileModifier> = object : MageBlockModifierType<VolatileModifier>() {
            override val argc: Int = 0
            override val id = HexicalMain.id("volatile")
            override fun construct(args: List<Iota>) = VolatileModifier()
            override fun deserialize(element: Tag, registries: HolderLookup.Provider) = VolatileModifier()
        }
    }
}