package miyucomics.hexical.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import miyucomics.hexical.features.zap.ZapManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.SignalGetter;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SignalGetter.class)
public interface SignalGetterMixin {
    @WrapMethod(method = "hasSignal")
    private boolean addRedstonePower(BlockPos pos, Direction direction, Operation<Boolean> original) {
        if (!(this instanceof ServerLevel world))
            return original.call(pos, direction);
        return original.call(pos, direction) || ZapManager.hasMagicalPower(world, pos);
    }

    @WrapMethod(method = "getSignal")
    private int addStrongPower(BlockPos pos, Direction direction, Operation<Integer> original) {
        if (!(this instanceof ServerLevel world))
            return original.call(pos, direction);
        if (!ZapManager.hasMagicalPower(world, pos))
            return original.call(pos, direction);
        return Math.max(original.call(pos, direction), ZapManager.getMagicalPower(world, pos));
    }
}