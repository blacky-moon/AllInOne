package org.blacky_moon.hexverse.client;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.blacky_moon.hexverse.common.CommonInit;

public class ClientInit {
    public static void init(IEventBus modEventBus, ModContainer container) {
        modEventBus.addListener(ClientInit::registerRenderers);
    }

    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(
                CommonInit.HEX_SNOWBALL_PROJECTILE.get(),
                HexSnowballRenderer::new
        );
    }
}