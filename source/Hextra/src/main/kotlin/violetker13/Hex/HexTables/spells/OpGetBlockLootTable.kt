package violetker13.Hex.HexTables.spells

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import miyucomics.hexpose.iotas.IdentifierIota
import miyucomics.hexpose.iotas.getIdentifier
import net.minecraft.item.Item
import net.minecraft.loot.LootPool
import net.minecraft.loot.LootTable
import net.minecraft.loot.entry.CombinedEntry
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.loot.entry.LootPoolEntry
import net.minecraft.registry.Registries
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import java.lang.reflect.Field

class OpGetBlockLootTable : ConstMediaAction {

    override val argc = 1

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val id = args.getIdentifier(0, argc)
        val serverWorld = env.world as? ServerWorld
            ?: throw MishapInvalidIota.of(args[0], 0, "block_id")

        val lootTableId = when {
            Registries.BLOCK.containsId(id)       -> Registries.BLOCK.get(id).lootTableId
            Registries.ENTITY_TYPE.containsId(id) -> Registries.ENTITY_TYPE.get(id).lootTableId
            else -> throw MishapInvalidIota.of(args[0], 0, "block_id")
        }

        val lootTable = serverWorld.server.lootManager.getLootTable(lootTableId)

        val drops = mutableSetOf<Identifier>()
        collectAllItems(lootTable, drops)

        return listOf(ListIota(drops.map { IdentifierIota(it) }))
    }

    private fun collectAllItems(lootTable: LootTable, out: MutableSet<Identifier>) {
        for (pool: LootPool in lootTable.pools)
            for (entry: LootPoolEntry in pool.entries)
                collectFromEntry(entry, out)
    }

    private fun collectFromEntry(entry: LootPoolEntry, out: MutableSet<Identifier>) {
        when (entry) {
            is ItemEntry -> out.add(Registries.ITEM.getId(ITEM_FIELD.get(entry) as Item))
            is CombinedEntry -> (CHILDREN_FIELD.get(entry) as Array<*>)
                .filterIsInstance<LootPoolEntry>()
                .forEach { collectFromEntry(it, out) }
        }
    }

    companion object {
        // инициализируем один раз при загрузке класса
        private val ITEM_FIELD: Field = ItemEntry::class.java
            .getDeclaredField("item").also { it.isAccessible = true }

        private val CHILDREN_FIELD: Field = CombinedEntry::class.java
            .getDeclaredField("children").also { it.isAccessible = true }
    }
}