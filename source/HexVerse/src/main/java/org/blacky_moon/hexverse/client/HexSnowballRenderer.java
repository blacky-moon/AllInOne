package org.blacky_moon.hexverse.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import org.blacky_moon.hexverse.common.entity.HexSnowballProjectile;

public class HexSnowballRenderer extends ThrownItemRenderer<HexSnowballProjectile> {

    public HexSnowballRenderer(EntityRendererProvider.Context context) {
        super(context, 1.0F, false);
    }
}