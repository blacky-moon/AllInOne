package miyucomics.hexical.datagen

import miyucomics.hexical.datagen.generators.*
import miyucomics.hexical.datagen.providers.DyeingProvider
import miyucomics.hexical.datagen.providers.FloraProvider
import miyucomics.hexical.datagen.providers.TransmutationProvider
import net.minecraft.core.HolderLookup
import net.minecraft.data.DataGenerator
import net.minecraft.data.PackOutput
import net.minecraft.data.loot.LootTableProvider
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.common.data.AdvancementProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper
import net.neoforged.neoforge.data.event.GatherDataEvent
import java.util.Collections
import java.util.concurrent.CompletableFuture

@EventBusSubscriber
object HexicalDatagen {
    @JvmStatic
    @SubscribeEvent
    fun gatherData(event: GatherDataEvent) {
        val generator: DataGenerator = event.generator
        val packOutput: PackOutput = generator.packOutput
        val lookupProvider: CompletableFuture<HolderLookup.Provider> = event.lookupProvider
        val existingFileHelper: ExistingFileHelper = event.existingFileHelper

        DyeingProvider.init()
        FloraProvider.init()
        TransmutationProvider.init()

        // Server data
        generator.addProvider(event.includeServer(), HexicalRecipeGenerator(packOutput, lookupProvider))

        generator.addProvider(event.includeServer(), AdvancementProvider(
            packOutput,
            lookupProvider,
            existingFileHelper,
            listOf(HexicalAdvancementGenerator(lookupProvider))
        ))

        // ✅ ИСПРАВЛЕНИЕ: Правильный конструктор LootTableProvider для 1.21.1
        // 1-й аргумент: PackOutput
        // 2-й аргумент: CompletableFuture<HolderLookup.Provider>
        // 3-й аргумент: Список SubProviderEntry, где мы передаем ссылку на конструктор ::HexicalBlockLootTableGenerator
        generator.addProvider(event.includeServer(), LootTableProvider(
            packOutput,
            Collections.emptySet(),
            listOf(
                LootTableProvider.SubProviderEntry(
                    { HexicalBlockLootTableGenerator(it) },
                    LootContextParamSets.BLOCK
                )
            ),
            lookupProvider
        ))

        // Client data
        generator.addProvider(event.includeClient(), HexicalBlockStateGenerator(packOutput, existingFileHelper))
        generator.addProvider(event.includeClient(), HexicalItemModelGenerator(packOutput, existingFileHelper))
        generator.addProvider(event.includeClient(), HexicalPatchouliGenerator(packOutput))
        generator.addProvider(event.includeClient(), HexicalDyeingGenerator(packOutput, lookupProvider))
    }
}