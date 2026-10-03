package miyucomics.hexical.mixin;

import miyucomics.hexical.features.shaders.ShaderRenderer;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(
            method = "render(Lnet/minecraft/client/DeltaTracker;Z)V",
            at = @At("RETURN")
    )
    public void renderShader(net.minecraft.client.DeltaTracker deltaTracker, boolean tick, CallbackInfo ci) {
        float tickDelta = deltaTracker.getGameTimeDeltaPartialTick(true);
        ShaderRenderer.render(tickDelta);
    }
}