package miyucomics.hexical.features.lamps

import miyucomics.hexical.inits.HexicalItems
import miyucomics.hexical.misc.InitHook
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.core.component.DataComponents
import net.minecraft.resources.ResourceLocation

object ArchLampModelProvider : InitHook() {
    override fun init() {
        ItemProperties.register(
            HexicalItems.ARCH_LAMP_ITEM.get(),
            ResourceLocation.withDefaultNamespace("active")
        ) { stack, _, _, _ ->
            // Читаем CustomData (аналог старого stack.nbt)
            val tag = stack.get(DataComponents.CUSTOM_DATA)?.copyTag()
            if (tag?.getBoolean("active") == true) 1.0f else 0.0f
        }
    }
}