package miyucomics.hexical

import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent

@EventBusSubscriber(modid = HexicalMain.MOD_ID, value = [Dist.CLIENT])

object HexicalModBusEvents {
    private lateinit var commonSetup: (FMLCommonSetupEvent) -> Unit

    fun registerCommonSetup(setup: (FMLCommonSetupEvent) -> Unit) {
        commonSetup = setup
    }

    @JvmStatic
    @SubscribeEvent
    fun onCommonSetup(event: FMLCommonSetupEvent) {
        if (::commonSetup.isInitialized) {
            commonSetup(event)
        }
    }
}