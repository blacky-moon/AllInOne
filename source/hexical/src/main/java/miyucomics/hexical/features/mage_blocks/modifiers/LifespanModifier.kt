package miyucomics.hexical.features.mage_blocks.modifiers

import at.petrak.hexcasting.api.casting.getPositiveInt
import at.petrak.hexcasting.api.casting.iota.Iota
import com.mojang.datafixers.util.Pair as DfuPair
import miyucomics.hexical.HexicalMain
import miyucomics.hexical.features.mage_blocks.MageBlockModifier
import miyucomics.hexical.features.mage_blocks.MageBlockModifierType
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.IntTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

class LifespanModifier : MageBlockModifier {
    override val type: MageBlockModifierType<*> = TYPE
    var lifespan = 0

    override fun getScryingLens(): DfuPair<ItemStack, Component> =
        DfuPair(ItemStack(Items.CLOCK), Component.translatable("hexical.mage_block.lifespan", lifespan / 20f))

    override fun serialize(registries: HolderLookup.Provider): Tag = IntTag.valueOf(lifespan)

    override fun tick(world: Level, pos: BlockPos, state: BlockState) {
        lifespan--
        if (lifespan <= 0) {
            world.removeBlock(pos, false)
        }
    }

    companion object {
        val TYPE: MageBlockModifierType<LifespanModifier> = object : MageBlockModifierType<LifespanModifier>() {
            override val argc: Int = 1
            override val id = HexicalMain.id("lifespan")
            override fun construct(args: List<Iota>) = LifespanModifier().also {
                it.lifespan = args.getPositiveInt(1, 2)
            }
            override fun deserialize(element: Tag, registries: HolderLookup.Provider) =

                LifespanModifier().also { it.lifespan = (element as IntTag).asInt }
        }
    }
}