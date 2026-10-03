package miyucomics.hexical.features.babelbug

import miyucomics.hexical.inits.HexicalItems
import miyucomics.hexical.misc.InitHook
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.core.component.DataComponents
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.component.CustomData

object BabelBugWingRenderer : InitHook() {
    override fun init() {
        // В 1.21.1 используем ItemProperties.register вместо ModelPredicateProviderRegistry
        // Предикат "active" должен быть объявлен в JSON модели предмета
        ItemProperties.register(
            HexicalItems.BABELBUG_ITEM.get(),
            ResourceLocation.withDefaultNamespace("active")
        ) { stack, _, _, _ ->
            // Чтение через Data Components вместо stack.nbt
            val customData = stack.get(DataComponents.CUSTOM_DATA) ?: return@register 0f
            if (customData.copyTag().getBoolean("active")) 1.0f else 0.0f
        }
    }
}