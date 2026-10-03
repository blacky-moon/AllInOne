package miyucomics.hexical.features.evocation

import miyucomics.hexical.HexicalMain
import miyucomics.hexical.misc.InitHook
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.player.Player
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.RenderHandEvent

// Подписываемся на игровую шину (GAME) только для клиента
@EventBusSubscriber(modid = HexicalMain.MOD_ID, value = [Dist.CLIENT])
object ClientEvocationReceiver : InitHook() {

    override fun init() {
        // Если здесь была другая инициализация, оставь её
    }
    @JvmStatic
    @SubscribeEvent
    fun onRenderHand(event: RenderHandEvent) {
        val player = Minecraft.getInstance().player ?: return

        if (player.evocationActive) {
            event.isCanceled = true

        }
    }
}