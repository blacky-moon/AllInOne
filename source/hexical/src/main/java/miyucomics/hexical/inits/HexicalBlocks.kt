package miyucomics.hexical.inits

import miyucomics.hexical.HexicalMain
import miyucomics.hexical.features.amber_seal.AmberSealBlock
import miyucomics.hexical.features.amber_seal.AmberSealBlockEntity
import miyucomics.hexical.features.hex_candles.HexCandleBlock
import miyucomics.hexical.features.hex_candles.HexCandleBlockEntity
import miyucomics.hexical.features.hex_candles.HexCandleCakeBlock
import miyucomics.hexical.features.hex_candles.HexCandleCakeBlockEntity
import miyucomics.hexical.features.mage_blocks.MageBlock
import miyucomics.hexical.features.mage_blocks.MageBlockEntity
import miyucomics.hexical.features.media_jar.MediaJarBlock
import miyucomics.hexical.features.media_jar.MediaJarBlockEntity
import miyucomics.hexical.features.periwinkle.PeriwinkleFlowerBlock
import miyucomics.hexical.features.pedestal.PedestalBlock
import miyucomics.hexical.features.pedestal.PedestalBlockEntity
import miyucomics.hexical.features.sentinel_beds.SentinelBedBlock
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.MapColor
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.level.block.PinkPetalsBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.material.PushReaction
import net.minecraft.world.level.block.CarpetBlock
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.DeferredHolder
import java.util.function.Supplier

object HexicalBlocks {
    private val BLOCKS = DeferredRegister.create(Registries.BLOCK, HexicalMain.MOD_ID)
    private val BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, HexicalMain.MOD_ID)

    // === Blocks ===
    @JvmField val AMBER_SEAL_BLOCK: DeferredHolder<Block, AmberSealBlock> = BLOCKS.register("amber_seal", Supplier { AmberSealBlock() })
    @JvmField val HEX_CANDLE_BLOCK: DeferredHolder<Block, HexCandleBlock> = BLOCKS.register("hex_candle", Supplier { HexCandleBlock() })
    @JvmField val HEX_CANDLE_CAKE_BLOCK: DeferredHolder<Block, HexCandleCakeBlock> = BLOCKS.register("hex_candle_cake", Supplier { HexCandleCakeBlock() })
    @JvmField val MAGE_BLOCK: DeferredHolder<Block, MageBlock> = BLOCKS.register("mage_block", Supplier { MageBlock() })
    @JvmField val MEDIA_JAR_BLOCK: DeferredHolder<Block, MediaJarBlock> = BLOCKS.register("media_jar", Supplier { MediaJarBlock() })
    @JvmField val SENTINEL_BED_BLOCK: DeferredHolder<Block, SentinelBedBlock> = BLOCKS.register("sentinel_bed", Supplier { SentinelBedBlock() })

    @JvmField val PEDESTAL_BLOCK: DeferredHolder<Block, PedestalBlock> = BLOCKS.register("pedestal", Supplier {
        PedestalBlock(BlockBehaviour.Properties.of().noOcclusion())
    })

    @JvmField val CASTING_CARPET: DeferredHolder<Block, CarpetBlock> = BLOCKS.register("casting_carpet", Supplier {
        CarpetBlock(
            BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_PURPLE)
                .strength(0.1f)
                .sound(SoundType.WOOL)
                .ignitedByLava()
        )
    })

    @JvmField val PERIWINKLE_FLOWER: DeferredHolder<Block, PeriwinkleFlowerBlock> = BLOCKS.register("periwinkle", Supplier {
        PeriwinkleFlowerBlock(
            BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_PURPLE)
                .noCollission()
                .sound(SoundType.PINK_PETALS)
                .pushReaction(PushReaction.DESTROY)
        )
    })

    // === Block Entities ===
    @JvmField val AMBER_SEAL_BLOCK_ENTITY: DeferredHolder<BlockEntityType<*>, BlockEntityType<AmberSealBlockEntity>> =
        BLOCK_ENTITIES.register("amber_seal", Supplier {
            BlockEntityType.Builder.of(::AmberSealBlockEntity, AMBER_SEAL_BLOCK.get()).build(null)
        })

    @JvmField val HEX_CANDLE_BLOCK_ENTITY: DeferredHolder<BlockEntityType<*>, BlockEntityType<HexCandleBlockEntity>> =
        BLOCK_ENTITIES.register("hex_candle", Supplier {
            BlockEntityType.Builder.of(::HexCandleBlockEntity, HEX_CANDLE_BLOCK.get()).build(null)
        })

    @JvmField val HEX_CANDLE_CAKE_BLOCK_ENTITY: DeferredHolder<BlockEntityType<*>, BlockEntityType<HexCandleCakeBlockEntity>> =
        BLOCK_ENTITIES.register("hex_candle_cake", Supplier {
            BlockEntityType.Builder.of(::HexCandleCakeBlockEntity, HEX_CANDLE_CAKE_BLOCK.get()).build(null)
        })

    @JvmField val MEDIA_JAR_BLOCK_ENTITY: DeferredHolder<BlockEntityType<*>, BlockEntityType<MediaJarBlockEntity>> =
        BLOCK_ENTITIES.register("media_jar", Supplier {
            BlockEntityType.Builder.of(::MediaJarBlockEntity, MEDIA_JAR_BLOCK.get()).build(null)
        })

    @JvmField val MAGE_BLOCK_ENTITY: DeferredHolder<BlockEntityType<*>, BlockEntityType<MageBlockEntity>> =
        BLOCK_ENTITIES.register("mage_block", Supplier {
            BlockEntityType.Builder.of(::MageBlockEntity, MAGE_BLOCK.get()).build(null)
        })

    @JvmField val PEDESTAL_BLOCK_ENTITY: DeferredHolder<BlockEntityType<*>, BlockEntityType<PedestalBlockEntity>> =
        BLOCK_ENTITIES.register("pedestal", Supplier {
            BlockEntityType.Builder.of(::PedestalBlockEntity, PEDESTAL_BLOCK.get()).build(null)
        })

    fun init(modEventBus: IEventBus) {
        BLOCKS.register(modEventBus)
        BLOCK_ENTITIES.register(modEventBus)
    }
}