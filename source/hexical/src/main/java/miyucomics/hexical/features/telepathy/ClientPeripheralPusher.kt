package miyucomics.hexical.features.telepathy

import miyucomics.hexical.compat.HexicalCompat
import miyucomics.hexical.inits.HexicalKeybinds
import miyucomics.hexical.misc.InitHook
import net.minecraft.client.Minecraft
import net.neoforged.neoforge.client.event.ClientTickEvent
import net.neoforged.neoforge.client.event.InputEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.network.PacketDistributor

object ClientPeripheralPusher : InitHook() {
    private var previousState = mutableMapOf<String, Boolean>()

    override fun init() {
        NeoForge.EVENT_BUS.addListener<ClientTickEvent.Post> { event ->
            val client = Minecraft.getInstance()
            val player = client.player ?: return@addListener

            val keys = listOf(
                client.options.keyUp, client.options.keyLeft, client.options.keyRight,
                client.options.keyDown, client.options.keyJump, client.options.keyShift,
                client.options.keyUse, client.options.keyAttack, client.options.keySprint,
                HexicalKeybinds.TELEPATHY_KEYBIND, HexicalKeybinds.EVOKE_KEYBIND,
                HexicalKeybinds.GAUNLET_KEYBIND
            )
            for (key in keys) {
                val pressed = key.isDown
                val prev = previousState[key.name]
                if (prev != pressed) {
                    // Клиентский предохранитель только для открытия посоха:
                    // если предмета нет — не шлём пакет и не сохраняем состояние "нажато",
                    // чтобы не спамить сервер впустую. Реальная защита всё равно на сервере.
                    if (key.name == HexicalKeybinds.GAUNLET_KEYBIND.name && pressed) {
                        val hasStaff = HexicalCompat.getGauntletStaff(player) != null
                        if (!hasStaff) continue
                    }

                    val payload = if (pressed) KeyPressedPayload(key.name)
                    else KeyReleasedPayload(key.name)
                    PacketDistributor.sendToServer(payload)
                    previousState[key.name] = pressed
                }
            }
        }

        NeoForge.EVENT_BUS.addListener<InputEvent.MouseScrollingEvent> { event ->
            if (HexicalKeybinds.TELEPATHY_KEYBIND.isDown) {
                PacketDistributor.sendToServer(ScrollPayload(event.scrollDeltaY.toInt()))
                event.isCanceled = true
            }
        }
    }
}