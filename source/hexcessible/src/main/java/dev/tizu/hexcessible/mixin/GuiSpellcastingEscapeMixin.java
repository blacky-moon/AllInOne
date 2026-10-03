package dev.tizu.hexcessible.mixin;

import at.petrak.hexcasting.client.gui.GuiSpellcasting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;

@Mixin(GuiSpellcasting.class)
public class GuiSpellcastingEscapeMixin {

    @Inject(
            method = "keyPressed",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (keyCode == 256) {
            try {
                GuiSpellcasting self = (GuiSpellcasting) (Object) this;

                Field drawStateField = GuiSpellcasting.class.getDeclaredField("drawState");
                drawStateField.setAccessible(true);
                Object drawState = drawStateField.get(self);

                Class<?> drawingClass = Class.forName("at.petrak.hexcasting.client.gui.GuiSpellcasting$PatternDrawState$Drawing");

                if (drawingClass.isInstance(drawState)) {
                    Class<?> betweenClass = Class.forName("at.petrak.hexcasting.client.gui.GuiSpellcasting$PatternDrawState$BetweenPatterns");
                    Object betweenInstance = betweenClass.getDeclaredField("INSTANCE").get(null);
                    drawStateField.set(self, betweenInstance);
                    cir.setReturnValue(true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}