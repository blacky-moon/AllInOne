package miyucomics.hexical.datagen.generators

import miyucomics.hexical.datagen.providers.DyeingProvider
import net.minecraft.core.HolderLookup
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import java.util.concurrent.CompletableFuture

class HexicalDyeingGenerator(
    private val packOutput: PackOutput,
    private val registries: CompletableFuture<HolderLookup.Provider>
) : DataProvider {

    override fun getName(): String = "Hexical Dyeing"

    override fun run(cache: CachedOutput): CompletableFuture<*> {
        // Теперь задачи принимают CachedOutput, PackOutput и CompletableFuture<HolderLookup.Provider>
        return CompletableFuture.allOf(
            *DyeingProvider.tasks.map { task -> task(cache, packOutput, registries) }.toTypedArray()
        )
    }
}