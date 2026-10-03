package miyucomics.hexical.features.amber_seal

import com.mojang.blaze3d.vertex.PoseStack
import miyucomics.hexical.inits.HexicalBlocks
import miyucomics.hexical.inits.HexicalComponents
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn

@OnlyIn(Dist.CLIENT)
object AmberSealItemRenderer : BlockEntityWithoutLevelRenderer(null, null) {

    override fun renderByItem(
        stack: ItemStack,
        displayContext: ItemDisplayContext,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        val client = Minecraft.getInstance()
        client.blockRenderer.renderSingleBlock(
            HexicalBlocks.AMBER_SEAL_BLOCK.get().defaultBlockState(),
            poseStack,
            bufferSource,
            packedLight,
            packedOverlay
        )
        val contents = stack.get(HexicalComponents.AMBER_SEAL_CONTENTS.get()) ?: return


        val registryAccess = client.level?.registryAccess()
        val nestedEntity = if (contents.blockEntityTag != null && registryAccess != null) {
            net.minecraft.world.level.block.entity.BlockEntity.loadStatic(
                net.minecraft.core.BlockPos.ZERO,
                contents.blockState,
                contents.blockEntityTag,
                registryAccess
            )
        } else null

        AmberSealBlockEntityRenderer.renderAmberSealInnerContents(
            client.blockRenderer,
            contents.blockState,
            nestedEntity,
            0f,
            poseStack,
            bufferSource,
            packedLight,
            packedOverlay,
            depth = 0
        )
    }
}