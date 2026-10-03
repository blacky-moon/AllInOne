package miyucomics.hexical.inits

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.utils.putCompound
import at.petrak.hexcasting.common.items.ItemStaff
import at.petrak.hexcasting.xplat.IXplatAbstractions
import miyucomics.hexical.HexicalMain
import miyucomics.hexical.features.babelbug.BabelbugItem
import miyucomics.hexical.features.confection.HexburstItem
import miyucomics.hexical.features.confection.HextitoItem
import miyucomics.hexical.features.curios.CurioItem
import miyucomics.hexical.features.driver_dots.DriverDotItem
import miyucomics.hexical.features.grimoires.GrimoireItem
import miyucomics.hexical.features.lamps.ArchLampItem
import miyucomics.hexical.features.lamps.HandLampItem
import miyucomics.hexical.features.media_jar.MediaJarBlock
import miyucomics.hexical.features.media_jar.MediaJarItem
import miyucomics.hexical.features.media_log.MediaLogItem
import miyucomics.hexical.features.periwinkle.LeiItem
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.tags.TagKey
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.DeferredHolder
import java.util.function.Supplier

object HexicalItems {
    private val ITEMS = DeferredRegister.create(Registries.ITEM, HexicalMain.MOD_ID)
    private val TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, HexicalMain.MOD_ID)
    fun randomPlush(): ItemStack {
        return ItemStack(PLUSHIES[HexicalMain.RANDOM.nextInt(PLUSHIES.size)].get())
    }
    val CAN_NOT_MAGE_MOUTH_TAG: TagKey<Item> = TagKey.create(Registries.ITEM, HexicalMain.id("can_not_mage_mouth"))

    // === Block Items (перенесены из HexicalBlocks) ===
    @JvmField val AMBER_SEAL_ITEM: DeferredHolder<Item, BlockItem> = ITEMS.register("amber_seal", Supplier {
        BlockItem(HexicalBlocks.AMBER_SEAL_BLOCK.get(), Item.Properties().stacksTo(1))
    })

    @JvmField val MAGE_BLOCK_ITEM: DeferredHolder<Item, BlockItem> = ITEMS.register("mage_block", Supplier {
        BlockItem(HexicalBlocks.MAGE_BLOCK.get(), Item.Properties())
    })

    @JvmField val HEX_CANDLE_ITEM: DeferredHolder<Item, BlockItem> = ITEMS.register("hex_candle", Supplier {
        BlockItem(HexicalBlocks.HEX_CANDLE_BLOCK.get(), Item.Properties())
    })

    @JvmField val SENTINEL_BED_ITEM: DeferredHolder<Item, BlockItem> = ITEMS.register("sentinel_bed", Supplier {
        BlockItem(HexicalBlocks.SENTINEL_BED_BLOCK.get(), Item.Properties())
    })

    @JvmField val MEDIA_JAR_ITEM: DeferredHolder<Item, MediaJarItem> = ITEMS.register("media_jar", Supplier {
        MediaJarItem()
    })

    @JvmField val PERIWINKLE_FLOWER_ITEM: DeferredHolder<Item, BlockItem> = ITEMS.register("periwinkle", Supplier {
        BlockItem(HexicalBlocks.PERIWINKLE_FLOWER.get(), Item.Properties())
    })

    @JvmField val CASTING_CARPET_ITEM: DeferredHolder<Item, BlockItem> = ITEMS.register("casting_carpet", Supplier {
        BlockItem(HexicalBlocks.CASTING_CARPET.get(), Item.Properties())
    })

    @JvmField val PEDESTAL_ITEM: DeferredHolder<Item, BlockItem> = ITEMS.register("pedestal", Supplier {
        BlockItem(HexicalBlocks.PEDESTAL_BLOCK.get(), Item.Properties())
    })

    // === Обычные предметы ===
    @JvmField val HAND_LAMP_ITEM: DeferredHolder<Item, HandLampItem> = ITEMS.register("hand_lamp", Supplier { HandLampItem })
    @JvmField val ARCH_LAMP_ITEM: DeferredHolder<Item, ArchLampItem> = ITEMS.register("arch_lamp", Supplier { ArchLampItem })

    @JvmField val GRIMOIRE_ITEM: DeferredHolder<Item, GrimoireItem> = ITEMS.register("grimoire", Supplier { GrimoireItem })
    @JvmField val COPPER_DRIVER_DOT_ITEM: DeferredHolder<Item, DriverDotItem> = ITEMS.register("copper_driver_dot", Supplier { DriverDotItem(true) })
    @JvmField val IRON_DRIVER_DOT_ITEM: DeferredHolder<Item, DriverDotItem> = ITEMS.register("iron_driver_dot", Supplier { DriverDotItem(true) })
    @JvmField val GOLD_DRIVER_DOT_ITEM: DeferredHolder<Item, DriverDotItem> = ITEMS.register("gold_driver_dot", Supplier { DriverDotItem(true) })
    @JvmField val NETHERITE_DRIVER_DOT_ITEM: DeferredHolder<Item, DriverDotItem> = ITEMS.register("netherite_driver_dot", Supplier { DriverDotItem(false) })

    @JvmField val BABELBUG_ITEM: DeferredHolder<Item, BabelbugItem> = ITEMS.register("babelbug", Supplier { BabelbugItem })

    @JvmField val HEX_GUMMY: DeferredHolder<Item, Item> = ITEMS.register("hex_gummy", Supplier {
        Item(Item.Properties().food(
            FoodProperties.Builder()
                .nutrition(2)
                .saturationModifier(0.5f)
                .alwaysEdible()
                .fast()
                .build()
        ))
    })

    @JvmField val HEXBURST_ITEM: DeferredHolder<Item, HexburstItem> = ITEMS.register("hexburst", Supplier { HexburstItem })
    @JvmField val HEXTITO_ITEM: DeferredHolder<Item, HextitoItem> = ITEMS.register("hextito", Supplier { HextitoItem })

    @JvmField val MEDIA_LOG_ITEM: DeferredHolder<Item, MediaLogItem> = ITEMS.register("media_log", Supplier { MediaLogItem })
    @JvmField val LEI: DeferredHolder<Item, LeiItem> = ITEMS.register("lei", Supplier { LeiItem() })

    @JvmField val GAUNTLET_STAFF: DeferredHolder<Item, ItemStaff> = ITEMS.register("gauntlet_staff", Supplier { ItemStaff(Item.Properties().stacksTo(1)) })
    @JvmField val LIGHTNING_ROD_STAFF: DeferredHolder<Item, ItemStaff> = ITEMS.register("lightning_rod_staff", Supplier { ItemStaff(Item.Properties().stacksTo(1)) })

    val CURIO_NAMES: List<String> = listOf("bismuth", "clover", "compass", "conch", "cube", "flute", "handbell", "heart", "interlock", "key", "staff", "charm", "strange", "beauty", "truth", "up", "down")
    val CURIOS: List<DeferredHolder<Item, Item>> = CURIO_NAMES.map { name ->
        ITEMS.register("curio_$name", Supplier { CurioItem.getCurioFromName(name) })
    }

    @JvmField val CURIO_COMPASS: DeferredHolder<Item, Item> = CURIOS[CURIO_NAMES.indexOf("compass")]

    @JvmField val CURIO_STAFF: DeferredHolder<Item, Item> = CURIOS[CURIO_NAMES.indexOf("staff")]

    val PLUSHIE_NAMES: List<String> = listOf("hexxy", "irissy", "pentxxy", "quadxxy", "thothy", "flexxy")
    val PLUSHIES: List<DeferredHolder<Item, Item>> = PLUSHIE_NAMES.map { name ->
        ITEMS.register("plush_$name", Supplier { Item(Item.Properties().stacksTo(1)) })
    }

    val HEXICAL_GROUP: DeferredHolder<CreativeModeTab, CreativeModeTab> = TABS.register("general", Supplier {
        CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.hexical.general"))
            .icon { ItemStack(CURIO_COMPASS.get()) }
            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .displayItems { _, output ->
                output.accept(ItemStack(HAND_LAMP_ITEM.get()).also {
                    IXplatAbstractions.INSTANCE.findHexHolder(it)!!.writeHex(listOf(), null, 32000 * MediaConstants.DUST_UNIT)
                })
                output.accept(ItemStack(ARCH_LAMP_ITEM.get()).also {
                    IXplatAbstractions.INSTANCE.findHexHolder(it)!!.writeHex(listOf(), null, 32000 * MediaConstants.DUST_UNIT)
                })

                output.accept(ItemStack(HEX_GUMMY.get()))
                output.accept(ItemStack(GRIMOIRE_ITEM.get()))
                output.accept(ItemStack(COPPER_DRIVER_DOT_ITEM.get()))
                output.accept(ItemStack(IRON_DRIVER_DOT_ITEM.get()))
                output.accept(ItemStack(GOLD_DRIVER_DOT_ITEM.get()))
                output.accept(ItemStack(NETHERITE_DRIVER_DOT_ITEM.get()))
                output.accept(ItemStack(BABELBUG_ITEM.get()))
                output.accept(ItemStack(LEI.get()))
                output.accept(ItemStack(MEDIA_LOG_ITEM.get()))
                output.accept(ItemStack(GAUNTLET_STAFF.get()))
                output.accept(ItemStack(LIGHTNING_ROD_STAFF.get()))

                val mediaJarStack = ItemStack(MEDIA_JAR_ITEM.get())
                mediaJarStack.set(HexicalComponents.MEDIA_HOLDER.get(), MediaJarBlock.MAX_CAPACITY)
                output.accept(mediaJarStack)

                output.accept(ItemStack(HEX_CANDLE_ITEM.get()))
                output.accept(ItemStack(CASTING_CARPET_ITEM.get()))
                output.accept(ItemStack(SENTINEL_BED_ITEM.get()))
                output.accept(ItemStack(PERIWINKLE_FLOWER_ITEM.get()))
                output.accept(ItemStack(PEDESTAL_ITEM.get()))

                for (item in CURIOS) output.accept(item.get())
                for (item in PLUSHIES) output.accept(item.get())
            }
            .build()
    })

    fun init(eventBus: IEventBus) {
        ITEMS.register(eventBus)
        TABS.register(eventBus)
    }
}