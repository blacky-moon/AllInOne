package miyucomics.hexical.features.amber_seal

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.block.BlockRenderDispatcher
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

class AmberSealBlockEntityRenderer(ctx: BlockEntityRendererProvider.Context) : BlockEntityRenderer<AmberSealBlockEntity> {
    private val blockRenderer = ctx.blockRenderDispatcher

    override fun render(
        seal: AmberSealBlockEntity,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        renderAmberSealInnerContents(
            blockRenderer,
            seal.encasedState,
            seal.encasedEntity,
            partialTick,
            poseStack,
            bufferSource,
            packedLight,
            packedOverlay,
            depth = 0
        )
    }

    companion object {
        // Должен совпадать с OpAmberSeal.MAX_NESTING_DEPTH (или быть чуть больше
        // как запас) — это отдельный лимит именно на рендер, защищающий от
        // StackOverflowError, если в мир каким-то образом попала печать глубже,
        // чем разрешает OpAmberSeal (например, через /give с NBT от игрока).
        private const val MAX_RENDER_DEPTH = 16

        fun renderAmberSealInnerContents(
            blockRenderer: BlockRenderDispatcher,
            state: BlockState,
            blockEntity: BlockEntity?,
            partialTick: Float,
            poseStack: PoseStack,
            bufferSource: MultiBufferSource,
            packedLight: Int,
            packedOverlay: Int,
            depth: Int = 0
        ) {
            if (depth >= MAX_RENDER_DEPTH) return // предохранитель от переполнения стека

            poseStack.pushPose()
            poseStack.translate(0.0625, 0.0625, 0.0625)
            poseStack.scale(0.875f, 0.875f, 0.875f)

            blockRenderer.renderSingleBlock(state, poseStack, bufferSource, packedLight, packedOverlay)

            when (blockEntity) {
                is AmberSealBlockEntity -> {

                    renderAmberSealInnerContents(
                        blockRenderer,
                        blockEntity.encasedState,
                        blockEntity.encasedEntity,
                        partialTick,
                        poseStack,
                        bufferSource,
                        packedLight,
                        packedOverlay,
                        depth = depth + 1
                    )
                }
                null -> { /* нечего рендерить дальше */ }
                else -> {
                    val dispatcher = Minecraft.getInstance().blockEntityRenderDispatcher
                    dispatcher.render(blockEntity, partialTick, poseStack, bufferSource)
                }
            }

            poseStack.popPose()
        }
    }
}