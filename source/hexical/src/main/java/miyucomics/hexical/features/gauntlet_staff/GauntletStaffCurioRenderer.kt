package miyucomics.hexical.features.gauntlet_staff

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.Minecraft
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.HumanoidModel
import net.minecraft.client.model.geom.ModelLayers
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.RenderLayerParent
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.world.entity.HumanoidArm
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import top.theillusivec4.curios.api.SlotContext
import top.theillusivec4.curios.api.client.ICurioRenderer

class GauntletStaffCurioRenderer(private val model: HumanoidModel<LivingEntity>) : ICurioRenderer {
    override fun <T : LivingEntity, M : EntityModel<T>> render(
        stack: ItemStack,
        slotContext: SlotContext,
        matrixStack: PoseStack,
        renderLayerParent: RenderLayerParent<T, M>,
        renderTypeBuffer: MultiBufferSource,
        light: Int,
        limbSwing: Float,
        limbSwingAmount: Float,
        partialTicks: Float,
        ageInTicks: Float,
        netHeadYaw: Float,
        headPitch: Float
    ) {
        val entity = slotContext.entity()

        model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch)
        model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks)

        val mainArm = entity.mainArm
        val arm = if (mainArm == HumanoidArm.RIGHT) model.rightArm else model.leftArm

        matrixStack.pushPose()


        arm.translateAndRotate(matrixStack)

        matrixStack.translate(0.0, 5.0 / 16.0, 0.0)

        if (mainArm == HumanoidArm.LEFT) {
            matrixStack.mulPose(Axis.YP.rotationDegrees(180.0f))
        }

        matrixStack.scale(0.75f, 0.75f, 0.75f)

        val mc = Minecraft.getInstance()
        mc.itemRenderer.renderStatic(
            stack,
            ItemDisplayContext.THIRD_PERSON_RIGHT_HAND,
            light,
            OverlayTexture.NO_OVERLAY,
            matrixStack,
            renderTypeBuffer,
            mc.level,
            0
        )
        matrixStack.popPose()
    }

    companion object {
        fun create(): GauntletStaffCurioRenderer {
            val bakedLayer = Minecraft.getInstance().entityModels.bakeLayer(ModelLayers.PLAYER)
            return GauntletStaffCurioRenderer(HumanoidModel(bakedLayer))
        }
    }
}