package miyucomics.hexical.mixin;

import miyucomics.hexical.features.charms.CharmUtilities;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphics.class)
public class DrawContextMixin {
    @Inject(
            method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;IIII)V",
            at = @At("HEAD"),
            remap = true
    )
    public void renderCharmedItemMedia(LivingEntity entity, Level level, ItemStack stack, int x, int y, int seed, int p_281995_, CallbackInfo ci) {
        if (!CharmUtilities.isStackCharmed(stack)) return;

        GuiGraphics graphics = (GuiGraphics) (Object) this;
        float progress = (float) CharmUtilities.getMedia(stack) / CharmUtilities.getMaxMedia(stack);
        int color = CharmUtilities.getBarColor(stack);

        graphics.fill(RenderType.guiOverlay(), x + 2, y + 2, x + 3, y + 13, 0x80_000000 | (color & 0xFFFFFF));
        graphics.fill(RenderType.guiOverlay(), x + 2, y + 2, x + 3, y + Math.round(progress * 10) + 2, color);
    }
}