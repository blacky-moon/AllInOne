package miyucomics.hexical.features.toast

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.toasts.Toast
import net.minecraft.client.gui.components.toasts.ToastComponent
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack

class HexToast(
    private val title: Component,
    private val description: Component,
    private val stack: ItemStack
) : Toast {
    private var newlyCreated = true
    private var startTime: Long = 0

    override fun render(guiGraphics: GuiGraphics, toastComponent: ToastComponent, time: Long): Toast.Visibility {
        if (newlyCreated) {
            newlyCreated = false
            startTime = time
        }

        val font = toastComponent.minecraft.font

        guiGraphics.blitSprite(TEXTURE, 0, 0, this.width(), this.height())
        guiGraphics.drawString(font, title, 30, 7, -11534256, false)
        guiGraphics.drawString(font, description, 30, 18, -16777216, false)
        guiGraphics.renderFakeItem(stack, 8, 8)

        return if ((time - startTime).toDouble() >= 5000.0 * toastComponent.notificationDisplayTimeMultiplier)
            Toast.Visibility.HIDE
        else
            Toast.Visibility.SHOW
    }

    companion object {
        private val TEXTURE = ResourceLocation.withDefaultNamespace("toast/advancement")
    }
}