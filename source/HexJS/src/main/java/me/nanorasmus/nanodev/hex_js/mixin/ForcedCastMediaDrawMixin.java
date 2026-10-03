package me.nanorasmus.nanodev.hex_js.mixin;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import me.nanorasmus.nanodev.hex_js.storage.StorageManager;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(CastingEnvironment.class)
public abstract class ForcedCastMediaDrawMixin {

    @Shadow public abstract ServerPlayer getCaster();

    @ModifyVariable(
            method = "extractMedia",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0 // Флаг allowOvercast в параметрах метода extractMedia
    )
    private boolean allowForcedCastMediaDraw(boolean allowOvercast) {
        ServerPlayer caster = getCaster();
        if (caster != null && StorageManager.currentlyForcedPlayers.contains(caster.getUUID())) {
            return true;
        }
        return allowOvercast;
    }
}