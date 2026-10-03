package org.blacky_moon.hexcurio

import at.petrak.hexcasting.common.lib.hex.HexActions
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.RandomSource
import net.neoforged.bus.api.IEventBus
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.neoforge.registries.RegisterEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.blacky_moon.hexcurio.client.HexCurioClient
import org.blacky_moon.hexcurio.common.inits.HexCurioActions
import org.blacky_moon.hexcurio.common.inits.InitHook
import kotlin.reflect.KClass
import kotlin.reflect.cast

@Mod(HexCurio.MOD_ID)
class HexCurio(modEventBus: IEventBus, modContainer: ModContainer)  {
    val LOGGER: Logger = LogManager.getLogger(MOD_ID)

    init {
        MOD_EVENT_BUS = modEventBus
        modEventBus.addListener(::registerAll)
        modEventBus.register(this)
        InitHook::class.sealedSubclasses.forEach { klass ->
            klass.objectInstance?.init(modEventBus)
        }
    }
    private fun registerAll(event : RegisterEvent) {
        event.register(HexActions.REGISTRY.key(), HexCurioActions::init)
    }

    @SubscribeEvent
    fun commonSetup(event: FMLCommonSetupEvent) {
        LOGGER.info("Loaded")
    }
    @SubscribeEvent
    fun clientSetup(event: FMLClientSetupEvent) {
        HexCurioClient.init(event)
    }
    companion object {
        const val MOD_ID: String = "hexcurio"

        @JvmField
        val RANDOM: RandomSource = RandomSource.create()

        lateinit var MOD_EVENT_BUS: IEventBus

        @JvmStatic
        fun id(path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(MOD_ID, path)
    }
}