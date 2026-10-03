package org.blacky_moon.hexverse;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.blacky_moon.hexverse.client.ClientInit;
@Mod(value = HexVerseMain.MOD_ID, dist = Dist.CLIENT)
public class HexVerseClient {

    public HexVerseClient(IEventBus modEventBus, ModContainer container) {
        ClientInit.init(modEventBus, container);
    }
}