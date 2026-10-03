package org.blacky_moon.hexcurio.common.inits

import net.minecraft.world.item.Item
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister
import org.blacky_moon.hexcurio.HexCurio
import org.blacky_moon.hexcurio.common.item.CurioMaskItem

object HexCurioItems : InitHook() {
    val ITEMS: DeferredRegister.Items = DeferredRegister.createItems(HexCurio.MOD_ID)

    val CURIO_MASK: DeferredItem<CurioMaskItem> = ITEMS.registerItem("curio_mask") {
        CurioMaskItem(it.stacksTo(1))
    }

    val UNUSED_ITEM: DeferredItem<Item> = ITEMS.registerItem("unused_item") {
        Item(it)
    }

    override fun init(bus: IEventBus) {
        ITEMS.register(bus)
    }
}