package miyucomics.hexical.inits

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.KeyMapping
import net.neoforged.fml.ModList
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent
import org.lwjgl.glfw.GLFW

object HexicalKeybinds {
    val TELEPATHY_KEYBIND = KeyMapping("key.hexical.telepathy", GLFW.GLFW_KEY_G, "key.categories.hexical")
    val EVOKE_KEYBIND = KeyMapping("key.hexical.evoke", GLFW.GLFW_KEY_R, "key.categories.hexical")

    // Создаем кнопку, но пока не регистрируем её безусловно
    val GAUNLET_KEYBIND = KeyMapping("key.hexical.open_gauntlet", InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_4, "key.categories.hexical")

    @JvmStatic
    fun registerKeyMappings(event: RegisterKeyMappingsEvent) {
        event.register(TELEPATHY_KEYBIND)
        event.register(EVOKE_KEYBIND)


        if (ModList.get().isLoaded("curios")) {
            event.register(GAUNLET_KEYBIND)
        }
    }
}