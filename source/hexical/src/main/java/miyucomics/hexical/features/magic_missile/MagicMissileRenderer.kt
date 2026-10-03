package miyucomics.hexical.features.magic_missile

import com.mojang.blaze3d.vertex.PoseStack
import miyucomics.hexical.HexicalMain
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation

class MagicMissileRenderer(context: EntityRendererProvider.Context) : EntityRenderer<MagicMissileEntity>(context) {

    override fun getTextureLocation(entity: MagicMissileEntity): ResourceLocation = TEXTURE

    companion object {
        val TEXTURE: ResourceLocation = ResourceLocation.fromNamespaceAndPath(
            HexicalMain.MOD_ID,
            "textures/entity/magic_missile.png"
        )
    }
}