package miyucomics.hexical.inits

import at.petrak.hexcasting.api.client.ScryingLensOverlayRegistry
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.common.lib.HexItems
import com.mojang.datafixers.util.Pair
import miyucomics.hexical.features.amber_seal.AmberSealBlockEntityRenderer
import miyucomics.hexical.features.media_jar.MediaJarBlockEntity
import miyucomics.hexical.features.media_jar.MediaJarBlockEntityRenderer
import miyucomics.hexical.misc.TextUtilities
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.client.event.EntityRenderersEvent

object HexicalBlocksClient {
    /**
     * Регистрация BlockEntityRenderer.
     * Render layers больше не регистрируются кодом — используйте "render_type": "cutout" в JSON модели блока.
     */
    fun registerRenderers(event: EntityRenderersEvent.RegisterRenderers) {
        event.registerBlockEntityRenderer(HexicalBlocks.AMBER_SEAL_BLOCK_ENTITY.get(), ::AmberSealBlockEntityRenderer)
        event.registerBlockEntityRenderer(HexicalBlocks.MEDIA_JAR_BLOCK_ENTITY.get(), ::MediaJarBlockEntityRenderer)
    }

    /**
     * Scrying Lens overlay можно регистрировать в FMLClientSetupEvent.enqueueWork
     */
    fun initScryingOverlays() {
        ScryingLensOverlayRegistry.addDisplayer(HexicalBlocks.MEDIA_JAR_BLOCK.get()) { lines, _, pos, _, level, _ ->
            val jar = level.getBlockEntity(pos) as? MediaJarBlockEntity ?: return@addDisplayer
            val mediaAmount = jar.getMedia().toFloat() / MediaConstants.DUST_UNIT.toFloat()
            lines.add(Pair.of(
                ItemStack(HexItems.AMETHYST_DUST),
                Component.translatable("hexcasting.tooltip.media", TextUtilities.DUST_AMOUNT.format(mediaAmount))
            ))
        }
    }
}