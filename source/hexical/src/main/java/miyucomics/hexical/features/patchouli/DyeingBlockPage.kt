package miyucomics.hexical.features.patchouli

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

class DyeingBlockPage : AbstractDyeingPage<BlockBundle>() {
    override fun parseProvided(name: String): BlockBundle {
        val block = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(name))
        val state = block.defaultBlockState()
        val entity = if (block is EntityBlock) block.newBlockEntity(BlockPos.ZERO, state) else null
        entity?.setLevel(Minecraft.getInstance().level!!)
        return BlockBundle(state, entity)
    }

    override fun renderCustom(graphics: GuiGraphics, thing: BlockBundle, x: Int, y: Int, mouseX: Int, mouseY: Int, tickDelta: Float) {
        PageUtils.renderBlock(graphics, thing.state, thing.entity, x, y, 10f, tickDelta)
    }
}

data class BlockBundle(val state: BlockState, val entity: BlockEntity?)