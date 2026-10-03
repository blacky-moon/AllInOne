package miyucomics.hexical.mixin;

import at.petrak.hexcasting.api.addldata.ADMediaHolder;
import at.petrak.hexcasting.api.utils.MediaHelper;
import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import miyucomics.hexical.features.wristpocket.WristpocketFieldKt;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(MediaHelper.class)
public class MediaHelperMixin {
	@Inject(method = "scanPlayerForMediaStuff", at = @At("RETURN"))
    private static void useWristpocketPhial(ServerPlayer player, CallbackInfoReturnable<List<ADMediaHolder>> cir) {
        ItemStack wristpocket = WristpocketFieldKt.getWristpocket(player);
        if (wristpocket.is(HexItems.BATTERY)) {
            ADMediaHolder holder = IXplatAbstractions.INSTANCE.findMediaHolder(wristpocket);
            if (holder != null && cir.getReturnValue() != null) {
                cir.getReturnValue().addFirst(holder);
            }
        }
    }
}