package blacky_moon.miyu.overevaluate

import at.petrak.hexcasting.api.casting.eval.vm.ContinuationFrame
import at.petrak.hexcasting.common.lib.HexRegistries
import at.petrak.hexcasting.common.lib.hex.HexActions
import blacky_moon.miyu.overevaluate.actions.jumble.JumbleIota
import blacky_moon.miyu.overevaluate.frames.ApepFrame
import blacky_moon.miyu.overevaluate.frames.AthenaFrame
import blacky_moon.miyu.overevaluate.frames.NephthysFrame
import blacky_moon.miyu.overevaluate.frames.SisyphusFrame
import blacky_moon.miyu.overevaluate.frames.ThemisFrame

import net.minecraft.resources.ResourceLocation
import net.minecraft.util.RandomSource
import net.neoforged.bus.api.IEventBus
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.neoforge.registries.RegisterEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@Mod(Overevaluate.MOD_ID)
class Overevaluate(modEventBus: IEventBus, modContainer: ModContainer) {
    val LOGGER: Logger = LogManager.getLogger(MOD_ID)

    init {
        MOD_EVENT_BUS = modEventBus
        modEventBus.addListener(::registerAll)
        modEventBus.register(this)
    }
    private fun registerAll(event : RegisterEvent) {
        event.register(HexActions.REGISTRY.key(), OverevaluateActions::init)
    }
    @SubscribeEvent
    fun onRegister(event: RegisterEvent) {
        event.register(HexRegistries.CONTINUATION_TYPE) { helper ->
            helper.register(id("apep"), ApepFrame.TYPE)
            helper.register(id("athena"), AthenaFrame.TYPE)
            helper.register(id("nephthys"), NephthysFrame.TYPE)
            helper.register(id("sisyphus"), SisyphusFrame.TYPE)
            helper.register(id("themis"), ThemisFrame.TYPE)
        }
        event.register(HexRegistries.IOTA_TYPE) { helper ->
            helper.register(id("jumble"), JumbleIota.TYPE)
        }
    }

    @SubscribeEvent
    fun onCommonSetup(event: FMLCommonSetupEvent) {
        LOGGER.info("Hello! This is working!")
    }

    companion object {
        const val MOD_ID: String = "overevaluate"

        @JvmField
        val RANDOM: RandomSource = RandomSource.create()

        lateinit var MOD_EVENT_BUS: IEventBus

        @JvmStatic
        fun id(path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(MOD_ID, path)
    }
}