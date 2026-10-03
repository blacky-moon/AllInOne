package miyucomics.hexical.datagen.generators

import miyucomics.hexical.inits.HexicalBlocks
import net.minecraft.core.HolderLookup
import net.minecraft.data.loot.BlockLootSubProvider
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.level.block.Block

class HexicalBlockLootTableGenerator(
    registries: HolderLookup.Provider // Теперь принимаем готовый Provider, а не CompletableFuture
) : BlockLootSubProvider(setOf(), FeatureFlags.REGISTRY.allFlags(), registries) {

    override fun generate() {
        dropSelf(HexicalBlocks.CASTING_CARPET.get())
        add(HexicalBlocks.HEX_CANDLE_BLOCK.get(), createCandleDrops(HexicalBlocks.HEX_CANDLE_BLOCK.get()))
        add(HexicalBlocks.HEX_CANDLE_CAKE_BLOCK.get(), createCandleCakeDrops(HexicalBlocks.HEX_CANDLE_CAKE_BLOCK.get()))
        dropSelf(HexicalBlocks.PEDESTAL_BLOCK.get())
        dropSelf(HexicalBlocks.PERIWINKLE_FLOWER.get())
        dropSelf(HexicalBlocks.SENTINEL_BED_BLOCK.get())
    }

    override fun getKnownBlocks(): Iterable<Block> {
        return listOf(
            HexicalBlocks.CASTING_CARPET.get(),
            HexicalBlocks.HEX_CANDLE_BLOCK.get(),
            HexicalBlocks.HEX_CANDLE_CAKE_BLOCK.get(),
            HexicalBlocks.PEDESTAL_BLOCK.get(),
            HexicalBlocks.PERIWINKLE_FLOWER.get(),
            HexicalBlocks.SENTINEL_BED_BLOCK.get()
        )
    }
}