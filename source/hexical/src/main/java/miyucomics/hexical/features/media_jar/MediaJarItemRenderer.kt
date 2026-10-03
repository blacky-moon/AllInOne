package miyucomics.hexical.features.media_jar

import com.mojang.blaze3d.vertex.PoseStack
import miyucomics.hexical.inits.HexicalBlocks
import miyucomics.hexical.inits.HexicalComponents
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions

object MediaJarItemRenderer : BlockEntityWithoutLevelRenderer(null, null) {
    override fun renderByItem(stack: ItemStack, displayContext: ItemDisplayContext, matrices: PoseStack, vertexConsumers: MultiBufferSource, light: Int, overlay: Int) {
        Minecraft.getInstance().blockRenderer.renderSingleBlock(HexicalBlocks.MEDIA_JAR_BLOCK.get().defaultBlockState(), matrices, vertexConsumers, light, overlay)

        // ИСПРАВЛЕНО: заполненность ItemStack-версии банки хранится в
        // HexicalComponents.MEDIA_HOLDER, а не в BLOCK_ENTITY_DATA (это поле
        // почти всегда пустое/не совпадает с реальным значением предмета).
        val media = stack.get(HexicalComponents.MEDIA_HOLDER.get()) ?: 0L
        MediaJarRenderer.renderFluid(matrices, vertexConsumers, media.toFloat() / MediaJarBlock.MAX_CAPACITY.toFloat())
    }
}