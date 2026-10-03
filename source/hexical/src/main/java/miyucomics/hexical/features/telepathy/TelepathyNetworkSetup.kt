package miyucomics.hexical.features.telepathy

import miyucomics.hexical.HexicalMain
import miyucomics.hexical.misc.InitHook
import net.minecraft.server.level.ServerPlayer
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent

object TelepathyNetworkSetup : InitHook() {
    override fun init() {
        HexicalMain.MOD_EVENT_BUS.addListener<RegisterPayloadHandlersEvent> { event ->
            val registrar = event.registrar(HexicalMain.MOD_ID)

            registrar.playToServer(KeyPressedPayload.TYPE, KeyPressedPayload.STREAM_CODEC) { payload, context ->
                val player = context.player() as? ServerPlayer ?: return@playToServer
                ServerPeripheralReceiver.handleKeyPressed(payload, player)
            }

            registrar.playToServer(KeyReleasedPayload.TYPE, KeyReleasedPayload.STREAM_CODEC) { payload, context ->
                val player = context.player() as? ServerPlayer ?: return@playToServer
                ServerPeripheralReceiver.handleKeyReleased(payload, player)
            }

            registrar.playToServer(ScrollPayload.TYPE, ScrollPayload.STREAM_CODEC) { payload, context ->
                val player = context.player() as? ServerPlayer ?: return@playToServer
                ServerPeripheralReceiver.handleScroll(payload, player)
            }
        }
    }
}