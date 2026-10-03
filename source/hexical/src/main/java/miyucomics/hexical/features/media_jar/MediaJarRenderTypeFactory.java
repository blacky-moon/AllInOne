package miyucomics.hexical.features.media_jar;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import miyucomics.hexical.HexicalShaders;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

public class MediaJarRenderTypeFactory {
    public static RenderType createFluidRenderType() {
        RenderType.CompositeState state = RenderType.CompositeState.builder()

                .setShaderState(new RenderStateShard.ShaderStateShard(HexicalShaders.INSTANCE::getMediaJarShader))
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setCullState(RenderStateShard.NO_CULL)
                .createCompositeState(true);

        return RenderType.create(
                "hexical_media_jar_fluid",
                DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL,
                VertexFormat.Mode.QUADS,
                256,
                false,
                true,
                state
        );
    }
}