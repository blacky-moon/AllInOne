package miyucomics.hexical.features.item_cache

import at.petrak.hexcasting.api.utils.getList
import miyucomics.hexical.features.player.types.PlayerTicker
import miyucomics.hexical.features.wristpocket.wristpocket
import miyucomics.hexical.inits.HexicalItems
import miyucomics.hexical.misc.HexSerialization
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.Tag
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player

// Hexical adds a lot of items that require quick and easy access, e.g. driver dots, babelbugs, and grimoires
// Rather than do an inventory scan per-pattern which would outrageously tank performance,
// there is an item cache that is mixined into every player and updates at the start of every tick
// Then, it is trivial to look up whether a driver dot hex is registered for a given pattern
// or if a given pattern has a grimoire expansion associated with it
// or if the player has a babelbug
class PlayerItemCacheTicker : PlayerTicker {
    override fun tick(player: Player) {
        if (player !is ServerPlayer)
            return

        val cache = player.itemCache()
        cache.babelbugProgram = null
        cache.grimoireMacros.clear()
        cache.copperDriverDotMacros.clear()
        cache.ironDriverDotMacros.clear()
        cache.goldDriverDotMacros.clear()
        cache.netheriteDriverDotProgram = null

        val inventory = player.inventory
        val lookup = player.serverLevel().registryAccess()

        inventory.offhand
            .plus(inventory.items)
            .plus(inventory.armor)
            .plus(player.wristpocket)
            .plus(player.enderChestInventory.items)
            .reversed()
            .forEach { stack ->
                // Читаем CustomData (аналог старого NBT)
                val tag = stack.get(DataComponents.CUSTOM_DATA)?.copyTag()

                when {
                    stack.`is`(HexicalItems.BABELBUG_ITEM.get()) && tag != null && tag.getBoolean("active") -> {
                        // Записанная программа теперь хранится не в CUSTOM_DATA, а в стандартном
                        // компоненте hex-casting HexDataComponents.IOTA_HOLDER_IOTA (см. BabelbugItem.writeDatum).
                        val iota = stack.get(at.petrak.hexcasting.common.lib.HexDataComponents.IOTA_HOLDER_IOTA)
                        cache.babelbugProgram = (iota as? at.petrak.hexcasting.api.casting.iota.ListIota)?.list?.toList()
                    }
                    stack.`is`(HexicalItems.GRIMOIRE_ITEM.get()) && tag != null -> {
                        if (tag.contains("expansions")) {
                            val expansions = tag.getCompound("expansions")
                            expansions.allKeys.forEach { key ->
                                cache.grimoireMacros[key] = HexSerialization.backwardsCompatibleReadHex(expansions, key, lookup)
                            }
                        }
                    }
                    stack.`is`(HexicalItems.COPPER_DRIVER_DOT_ITEM.get()) && tag != null && tag.contains("pattern") -> {
                        val pattern = tag.getString("pattern")
                        val list = tag.getList("program", Tag.TAG_COMPOUND)
                        cache.copperDriverDotMacros[pattern] = HexSerialization.deserializeHex(list, lookup)
                    }
                    stack.`is`(HexicalItems.IRON_DRIVER_DOT_ITEM.get()) && tag != null && tag.contains("pattern") -> {
                        val pattern = tag.getString("pattern")
                        val list = tag.getList("program", Tag.TAG_COMPOUND)
                        cache.ironDriverDotMacros[pattern] = HexSerialization.deserializeHex(list, lookup)
                    }
                    stack.`is`(HexicalItems.GOLD_DRIVER_DOT_ITEM.get()) && tag != null && tag.contains("pattern") -> {
                        val pattern = tag.getString("pattern")
                        val list = tag.getList("program", Tag.TAG_COMPOUND)
                        cache.goldDriverDotMacros[pattern] = HexSerialization.deserializeHex(list, lookup)
                    }
                    stack.`is`(HexicalItems.NETHERITE_DRIVER_DOT_ITEM.get()) && tag != null && tag.contains("program") -> {
                        val list = tag.getList("program", Tag.TAG_COMPOUND)
                        cache.netheriteDriverDotProgram = HexSerialization.deserializeHex(list, lookup)
                    }
                }
            }
    }
}