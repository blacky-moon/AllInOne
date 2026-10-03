package miyucomics.hexical.features.patchouli

import com.google.gson.annotations.SerializedName
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.Level
import vazkii.patchouli.client.book.BookContentsBuilder
import vazkii.patchouli.client.book.BookEntry
import vazkii.patchouli.client.book.gui.GuiBook
import vazkii.patchouli.client.book.page.abstr.PageWithText

class BlockPage : PageWithText() {
    @SerializedName("block")
    var blockId: String? = null
    @Transient private var state: BlockState = Blocks.AIR.defaultBlockState()
    @Transient private var entity: BlockEntity? = null

    override fun build(level: Level, entry: BookEntry, builder: BookContentsBuilder, pageNum: Int) {
        super.build(level, entry, builder, pageNum)
        val block = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(blockId!!))
        state = block.defaultBlockState()
        if (block is EntityBlock) {
            this.entity = block.newBlockEntity(BlockPos.ZERO, state)
            this.entity?.setLevel(level)
        }
    }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, pticks: Float) {
        RenderSystem.enableBlend()
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        super.render(graphics, mouseX, mouseY, pticks)
        PageUtils.renderBlock(graphics, state, entity, GuiBook.PAGE_WIDTH / 2, 25, 25f, pticks)
    }

    override fun getTextHeight(): Int = 55
}