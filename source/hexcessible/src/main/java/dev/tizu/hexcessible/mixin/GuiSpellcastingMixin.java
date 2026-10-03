package dev.tizu.hexcessible.mixin;

import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.client.gui.GuiSpellcasting;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.tizu.hexcessible.Utils;
import dev.tizu.hexcessible.accessor.DrawStateMixinAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GuiSpellcasting.class)
public class GuiSpellcastingMixin {

    @Unique
    private static final int ANIM_DURATION_TICKS = 5; 
    @Unique
    private long hexcessible$copyTime = 0;
    @Unique
    private double hexcessible$copiedX = 0;
    @Unique
    private double hexcessible$copiedY = 0;

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void onRightClickCopyPattern(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            if (!((Object) this instanceof DrawStateMixinAccessor accessor)) return;

            HexPattern pat = accessor.getPatternAt((int) mouseX, (int) mouseY);
            if (pat != null) {
                String sigStr = Utils.angle(pat.getAngles());
                String resultToCopy = "_" + sigStr;

                long window = Minecraft.getInstance().getWindow().getWindow();
                GLFW.glfwSetClipboardString(window, resultToCopy);

                
                this.hexcessible$copyTime = System.currentTimeMillis();
                this.hexcessible$copiedX = mouseX;
                this.hexcessible$copiedY = mouseY;

                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void renderCopiedNotification(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick, CallbackInfo ci) {
        if (this.hexcessible$copyTime == 0) return;

        
        long elapsed = System.currentTimeMillis() - this.hexcessible$copyTime;
        long durationMs = ANIM_DURATION_TICKS * 50L;

        if (elapsed > durationMs) {
            this.hexcessible$copyTime = 0; 
            return;
        }

        
        float progress = (float) elapsed / (float) durationMs;

        
        float floatOffset = progress * 15.0f;
        float currentY = (float) this.hexcessible$copiedY - floatOffset;

        
        float alpha = 1.0f - progress;

        
        Component text = Component.literal("Copied");
        Font font = Minecraft.getInstance().font;
        int textWidth = font.width(text);


        float renderX = (float) this.hexcessible$copiedX - (textWidth / 2.0f);

        
        int alphaInt = (int) (alpha * 255) << 24;
        int textColor = 0x55FF55 | alphaInt; 

        
        RenderSystem.enableBlend();
        graphics.drawString(font, text, (int) renderX, (int) currentY, textColor, true);
        RenderSystem.disableBlend();
    }
}