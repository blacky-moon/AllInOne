package miyucomics.hexical.mixin;

import miyucomics.hexical.HexicalMain;
import miyucomics.hexical.features.periwinkle.WooleyedEffectRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.Gui;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class InGameHudMixin {
    @Unique
    private static final ResourceLocation AMETHYST_HEARTS = HexicalMain.id("textures/gui/amethyst_hearts.png");

    @Inject(
            method = "renderHeart",
            at = @At("HEAD"),
            cancellable = true
    )
    private void amethystHearts(
            GuiGraphics graphics,
            Gui.HeartType type,
            int x,
            int y,
            boolean hardcore,
            boolean halfHeart,
            boolean blinking,
            CallbackInfo ci
    ) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        if (!player.hasEffect(WooleyedEffectRegister.INSTANCE.getWOOLEYED_EFFECT())) return;

        // Используем blit с указанием размеров текстуры
        int textureWidth = 18;
        int textureHeight = 18;

        if (type == Gui.HeartType.NORMAL) {
            int u = halfHeart ? 9 : 0;
            int v = 0;
            graphics.blit(AMETHYST_HEARTS, x, y, u, v, 9, 9, textureWidth, textureHeight);
            ci.cancel();
        } else if (type == Gui.HeartType.CONTAINER) {
            int u = 0;
            int v = 9;
            graphics.blit(AMETHYST_HEARTS, x, y, u, v, 9, 9, textureWidth, textureHeight);
            ci.cancel();
        }
    }
}