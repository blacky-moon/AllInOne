package org.arcticquests.dev.oneironaut.oneironautt;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautFluidTypes;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

@EventBusSubscriber(modid = Oneironaut.MODID, value = Dist.CLIENT)
public class OneironautClientFluidExtensions {

    private static final Vector3f THOUGHT_SLURRY_FOG_COLOR = new Vector3f(0.678F, 0.451F, 0.808F);

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return ResourceLocation.parse("oneironaut:block/thought_slurry");
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return ResourceLocation.parse("oneironaut:block/thought_slurry_flowing");
            }

            @Override
            public ResourceLocation getOverlayTexture() {
                return ResourceLocation.parse("minecraft:block/water_overlay");
            }

            @Override
            public int getTintColor() {
                return 0xFFAD73CE;
            }

            @Override
            public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape) {
                float fogStart = 10.0F;
                float fogEnd = 12.0F;
                RenderSystem.setShaderFogStart(fogStart);
                RenderSystem.setShaderFogEnd(fogEnd);
            }

            @Override
            public @NotNull Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
                return THOUGHT_SLURRY_FOG_COLOR;
            }
        }, OneironautFluidTypes.THOUGHT_SLURRY_TYPE.get());
    }
}