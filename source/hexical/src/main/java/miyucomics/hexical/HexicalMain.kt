package miyucomics.hexical

import at.petrak.hexcasting.common.lib.hex.HexActions
import at.petrak.hexcasting.common.lib.hex.HexArithmetics
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import miyucomics.hexical.features.pattern_manipulation.PatternArithmetic
import miyucomics.hexical.features.periwinkle.WooleyedEffectRegister
import miyucomics.hexical.features.player.HexicalAttachments
import miyucomics.hexical.features.sentinel_beds.SentinelBedPoi
import miyucomics.hexical.features.transmuting.TransmutingHelper
import miyucomics.hexical.inits.*
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.RandomSource
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.neoforge.registries.RegisterEvent

@Mod(HexicalMain.MOD_ID)
class HexicalMain(modEventBus: IEventBus, modContainer: ModContainer) {
    init {
        MOD_EVENT_BUS = modEventBus

        HexicalSounds.init(modEventBus)
        HexicalBlocks.init(modEventBus)
        HexicalItems.init(modEventBus)
        HexicalEntities.init(modEventBus)
        HexicalComponents.register(modEventBus)
        WooleyedEffectRegister.init(modEventBus)
        HexicalAttachments.init(modEventBus)
        HexicalRecipes.init(modEventBus) // <-- ДОБАВЬТЕ ЭТУ СТРОКУ

        modEventBus.addListener(::registerAll)

        // FMLCommonSetupEvent подходит для инициализации хуков, так как реестры уже заморожены
        modEventBus.addListener(::onInitialize)
    }

    private fun registerAll(event: RegisterEvent) {
        event.register(HexActions.REGISTRY.key(), HexicalActions::registerAll)
        event.register(HexArithmetics.REGISTRY.key()) { helper ->
            helper.register(id("patterns"), PatternArithmetic)
        }
        event.register(HexIotaTypes.REGISTRY.key(), HexicalIota::registerAll)
        event.register(Registries.TRIGGER_TYPE, HexicalAdvancements::registerAll)
        event.register(Registries.CUSTOM_STAT) { helper ->
            helper.register(HexicalAdvancements.EVOCATION_STATISTIC, HexicalAdvancements.EVOCATION_STATISTIC)
        }
        event.register(Registries.POINT_OF_INTEREST_TYPE, SentinelBedPoi::registerAll)
    }

    private fun onInitialize(event: FMLCommonSetupEvent) {
        // Инициализация серверных хуков
        // Важно: хуки, использующие NeoForge.EVENT_BUS, должны делать это аккуратно
        HexicalHooksServer.init()
    }

    companion object {
        const val MOD_ID: String = "hexical"

        @JvmField
        val RANDOM: RandomSource = RandomSource.create()

        lateinit var MOD_EVENT_BUS: IEventBus

        @JvmStatic
        fun id(path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(MOD_ID, path)
    }
}