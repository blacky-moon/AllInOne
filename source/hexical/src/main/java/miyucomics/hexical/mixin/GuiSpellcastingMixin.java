package miyucomics.hexical.mixin;

import at.petrak.hexcasting.client.gui.GuiSpellcasting;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import miyucomics.hexical.compat.HexicalCompat;
import miyucomics.hexical.inits.HexicalBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = GuiSpellcasting.class, remap = false)
public class GuiSpellcastingMixin {
    @Unique
    private long hexical$lastTime = System.currentTimeMillis();
    @Unique
    private double hexical$zoomFactor = 0.0;

    @ModifyReturnValue(method = "hexSize", at = @At("RETURN"))
    private float thinkyCarpet(float original) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return original;

        long now = System.currentTimeMillis();
        long deltaTime = now - hexical$lastTime;
        hexical$lastTime = now;
        if (deltaTime > 100) deltaTime = 16;
        boolean onCarpet = player.getBlockStateOn().is(HexicalBlocks.CASTING_CARPET.get());
        if (onCarpet) {
            hexical$zoomFactor += (double) deltaTime / 500.0;
        } else {
            hexical$zoomFactor -= (double) deltaTime / 500.0;
        }
        hexical$zoomFactor = Mth.clamp(hexical$zoomFactor, 0.0, 0.35);

        return original - (float)(hexical$zoomFactor * 32.0);
    }

    @ModifyVariable(method = "tick", at = @At("STORE"), ordinal = 0)
    private ItemStack hexical$overrideHeldItem(ItemStack original) {
        Player player = Minecraft.getInstance().player;
        if (player != null && HexicalCompat.INSTANCE.isGauntletStaffEquipped(player)) {
            ItemStack gauntlet = HexicalCompat.INSTANCE.getGauntletStaff(player);
            if (gauntlet != null && !gauntlet.isEmpty()) {
                return gauntlet;
            }
        }
        return original;
    }
}