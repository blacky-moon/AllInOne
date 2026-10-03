package miyucomics.hexical.features.patchouli

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack

class DyeingItemPage : AbstractDyeingPage<ItemStack>() {
    override fun parseProvided(name: String): ItemStack = BuiltInRegistries.ITEM.get(ResourceLocation.parse(name)).defaultInstance
    override fun renderCustom(graphics: GuiGraphics, thing: ItemStack, x: Int, y: Int, mouseX: Int, mouseY: Int, tickDelta: Float) {
        parent.renderItemStack(graphics, x - 8, y - 8, mouseX, mouseY, thing)
    }
}