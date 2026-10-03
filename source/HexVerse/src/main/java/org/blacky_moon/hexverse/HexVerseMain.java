package org.blacky_moon.hexverse;

import at.petrak.hexcasting.common.lib.hex.HexActions;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.blacky_moon.hexverse.common.CommonInit;
import org.blacky_moon.hexverse.common.TabLoader;
import org.blacky_moon.hexverse.common.casting.HexVersePatterns;

@Mod(value = HexVerseMain.MOD_ID)
public class HexVerseMain {
    public static final String MOD_ID = "hexverse";

    public HexVerseMain(IEventBus modEventBus, ModContainer container) {
        CommonInit.init(modEventBus, container);
        TabLoader.init(modEventBus);
        modEventBus.addListener(this::registerAll);
    }


        private void registerAll(RegisterEvent event) {
        event.register(
                HexActions.REGISTRY.key(),
                HexVersePatterns::registerAll
        );
    }
}