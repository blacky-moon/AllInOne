package miyucomics.hexical.mixin;

import at.petrak.hexcasting.common.lib.HexItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientLevel.class)
public class ClientWorldMixin {
    @Inject(method = "getMarkerParticleTarget", at = @At("HEAD"), cancellable = true)
    public void showLights(CallbackInfoReturnable<Block> cir) {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null && client.player.isHolding(HexItems.SCRYING_LENS)) {
            cir.setReturnValue(Blocks.LIGHT);
        }
    }
}