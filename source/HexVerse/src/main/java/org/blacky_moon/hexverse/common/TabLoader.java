package org.blacky_moon.hexverse.common;

import at.petrak.hexcasting.common.lib.HexCreativeTabs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.blacky_moon.hexverse.HexVerseMain;


public class TabLoader {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, HexVerseMain.MOD_ID);
    static {CREATIVE_TABS.register(
            "hexverse", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.hexverse"))
                    .icon(() -> new ItemStack(CommonInit.HEX_SWORD.get()))
                    .withTabsBefore(HexCreativeTabs.HEX_KEY)
                    .displayItems(((itemDisplayParameters, output) -> {
                        CommonInit.ITEMS.getEntries().stream()
                                .map(DeferredHolder::get).forEach(output::accept);
                    }))

                    .build()
    );}
    public static void init(IEventBus modEventBus) {
        CREATIVE_TABS.register(modEventBus);
    }
}
