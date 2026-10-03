package org.blacky_moon.hexcurio.common.inits

import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import org.blacky_moon.hexcurio.HexCurio

object HexCurioTabs : InitHook() {
    val TABS: DeferredRegister<CreativeModeTab> = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, HexCurio.MOD_ID)

    val HEXCURIO_TAB: DeferredHolder<CreativeModeTab, CreativeModeTab> = TABS.register("hexcurio_tab") { ->
        CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.hexcurio"))
            .icon { HexCurioItems.CURIO_MASK.get().defaultInstance }
            .displayItems { _, output ->
                output.accept(HexCurioItems.CURIO_MASK.get())
                output.accept(HexCurioItems.UNUSED_ITEM.get())
            }
            .build()
    }

    override fun init(bus: IEventBus) {
        TABS.register(bus)
    }
}