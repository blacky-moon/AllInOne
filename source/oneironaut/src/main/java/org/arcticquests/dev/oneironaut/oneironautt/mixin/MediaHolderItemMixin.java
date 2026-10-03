package org.arcticquests.dev.oneironaut.oneironautt.mixin;

import at.petrak.hexcasting.api.item.MediaHolderItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MediaHolderItem.class)
public interface MediaHolderItemMixin {

    @Inject(method = "insertMedia", at = @At("HEAD"))
    private void onInsertMedia(ItemStack stack, long amountToInsert, boolean simulate, CallbackInfoReturnable<Long> cir) {
        if (!simulate && amountToInsert > 0) {
            MediaHolderItem self = (MediaHolderItem) this;
            long currentMedia = self.getMedia(stack);
            long maxMedia = self.getMaxMedia(stack);
            long actualInserted = Math.min(amountToInsert, maxMedia - currentMedia);

            if (actualInserted > 0) {
                oneironaut$recordMediaChange(stack, actualInserted);
            }
        }
    }

    @Inject(method = "withdrawMedia", at = @At("HEAD"))
    private void onWithdrawMedia(ItemStack stack, long cost, boolean simulate, CallbackInfoReturnable<Long> cir) {
        if (!simulate && cost > 0) {
            MediaHolderItem self = (MediaHolderItem) this;
            long currentMedia = self.getMedia(stack);
            long actualWithdrawn = Math.min(cost, currentMedia);

            if (actualWithdrawn > 0) {
                oneironaut$recordMediaChange(stack, -actualWithdrawn);
            }
        }
    }

    @Unique
    private static void oneironaut$recordMediaChange(ItemStack stack, long delta) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putLong("oneironaut_last_media_change", delta));
    }
}