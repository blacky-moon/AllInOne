package miyucomics.hexical.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import miyucomics.hexical.features.zap.ZapManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PistonBaseBlock.class)
public class PistonBlockMixin {

    // В 1.21.1 Mojang mappings метод называется getNeighborSignal
    @WrapMethod(method = "getNeighborSignal")
    private boolean addRedstonePower(SignalGetter signalGetter, BlockPos pos, Direction pistonFace, Operation<Boolean> original) {
        // Вызываем оригинальную логику проверки редстоуна
        boolean hasVanillaSignal = original.call(signalGetter, pos, pistonFace);

        // Если это серверный мир, проверяем магическую энергию ZapManager
        if (signalGetter instanceof ServerLevel serverLevel) {
            return hasVanillaSignal || ZapManager.hasMagicalPower(serverLevel, pos);
        }

        return hasVanillaSignal;
    }
}