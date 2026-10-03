package miyucomics.hexical.mixin;

import miyucomics.hexical.inits.HexicalComponents;
import miyucomics.hexical.inits.HexicalItems;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    @Shadow
    public abstract ItemStack getItem();

    @Inject(method = "tick", at = @At("HEAD"))
    void deactivateDroppedLamp(CallbackInfo ci) {
        ItemStack stack = this.getItem();
        if (!((ItemEntity)(Object)this).level().isClientSide && stack.is(HexicalItems.ARCH_LAMP_ITEM.get())) {
            // Устанавливаем компонент LAMP_ACTIVE в false
            stack.set(HexicalComponents.LAMP_ACTIVE.get(), false);
        }
    }
}