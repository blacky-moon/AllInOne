package miyucomics.hexical.datagen.generators

import miyucomics.hexical.inits.HexicalBlocks
import miyucomics.hexical.inits.HexicalItems
import net.minecraft.data.PackOutput
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.neoforged.neoforge.client.model.generators.ConfiguredModel
import net.neoforged.neoforge.client.model.generators.ItemModelProvider
import net.neoforged.neoforge.client.model.generators.ModelFile
import net.neoforged.neoforge.common.data.ExistingFileHelper

private const val MOD_ID = "hexical"

class HexicalBlockStateGenerator(
    output: PackOutput,
    existingFileHelper: ExistingFileHelper
) : BlockStateProvider(output, MOD_ID, existingFileHelper) {

    override fun registerStatesAndModels() {
        // Простые блоки
        simpleBlock(HexicalBlocks.MAGE_BLOCK.get())
        simpleBlock(HexicalBlocks.PEDESTAL_BLOCK.get())
        simpleBlock(HexicalBlocks.PERIWINKLE_FLOWER.get())
        simpleBlock(HexicalBlocks.CASTING_CARPET.get())

        // Свечи - используем готовые методы из BlockStateProvider
        // candleBlock() и candleCakeBlock() доступны, но могут быть protected
        // Вместо этого создаём модели вручную
        val hexCandle = HexicalBlocks.HEX_CANDLE_BLOCK.get()
        val hexCandleCake = HexicalBlocks.HEX_CANDLE_CAKE_BLOCK.get()

        // Для свечи используем простую модель
        simpleBlock(hexCandle, models().getExistingFile(mcLoc("block/candle")))

        // Для свечи на торте используем модель торта со свечой
        simpleBlock(hexCandleCake, models().getExistingFile(mcLoc("block/candle_cake")))

        // Sentinel Bed с вращением
        val sentinelBed = HexicalBlocks.SENTINEL_BED_BLOCK.get()
        val model = models().cubeAll(name(sentinelBed), blockTexture(sentinelBed))

        getVariantBuilder(sentinelBed).forAllStates { state ->
            val rotation = state.getValue(BlockStateProperties.ROTATION_16)
            val rotAngle = (rotation * 22.5f).toInt()
            ConfiguredModel.builder()
                .modelFile(model)
                .rotationY(rotAngle)
                .build()
        }
    }

    // Вспомогательный метод для получения имени блока
    private fun name(block: net.minecraft.world.level.block.Block): String {
        return net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block).path
    }
}

class HexicalItemModelGenerator(
    output: PackOutput,
    existingFileHelper: ExistingFileHelper
) : ItemModelProvider(output, MOD_ID, existingFileHelper) {

    override fun registerModels() {
        for (curio in HexicalItems.CURIOS) {
            val item = curio.get()
            val name = item.builtInRegistryHolder().key().location().path
            when (curio) {
                HexicalItems.CURIO_COMPASS -> withExistingParent(name, "minecraft:item/compass")
                HexicalItems.CURIO_STAFF -> withExistingParent(name, "minecraft:item/handheld_rod")
                else -> basicItem(item)
            }
        }
        for (plushie in HexicalItems.PLUSHIES) {
            basicItem(plushie.get())
        }
    }
}