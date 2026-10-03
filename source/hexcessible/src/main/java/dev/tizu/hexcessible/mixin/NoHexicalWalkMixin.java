package dev.tizu.hexcessible.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import at.petrak.hexcasting.client.gui.GuiSpellcasting;
import dev.tizu.hexcessible.Hexcessible;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;

@Mixin(KeyMapping.class)
public class NoHexicalWalkMixin {
    @Unique
    private static final List<String> DISALLOWED = List.of("key.forward",
            "key.back", "key.left", "key.right", "key.jump", "key.sneak");

    @Inject(method = "setDown", at = @At("HEAD"), cancellable = true)
    private void blockPressedWhileCasting(boolean pressed, CallbackInfo ci) {
        if (!Hexcessible.cfg().noHexicalWalk)
            return;
        Minecraft client = Minecraft.getInstance();
        if (!(client.screen instanceof GuiSpellcasting))
            return;
        KeyMapping self = (KeyMapping) (Object) this;
        String id = self.getName();
        if (DISALLOWED.contains(id) && pressed)
            ci.cancel();
    }
}