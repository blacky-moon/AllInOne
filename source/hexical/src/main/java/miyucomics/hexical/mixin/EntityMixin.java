package miyucomics.hexical.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import miyucomics.hexical.features.mute.MutedComponent;
import miyucomics.hexical.inits.HexicalComponents;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Entity.class)
public class EntityMixin {
    @WrapMethod(method = "isSilent")
    private boolean muteEntities(Operation<Boolean> original) {
        MutedComponent muted = ((Entity)(Object)this).getCapability(HexicalComponents.MUTED_CAP);
        if (muted != null && muted.getMuted()) {
            return true;
        }
        return original.call();
    }
}