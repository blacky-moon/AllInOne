package blacky_moon.miyu.overevaluate.mixin;

import at.petrak.hexcasting.api.casting.eval.CastResult;
import at.petrak.hexcasting.api.casting.eval.ExecutionClientView;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import blacky_moon.miyu.overevaluate.frames.AthenaFrame;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = CastingVM.class, remap = false)
public class CastingVMMixin {

    @WrapMethod(method = "executeInner")
    private CastResult onMishapCatch(Iota iota, ServerLevel world, SpellContinuation continuation, Operation<CastResult> original) {
        return AthenaFrame.handleAthena(iota, (CastingVM) (Object) this, world, continuation, original);
    }

    @Inject(
            method = "queueExecuteAndWrapIotas",
            at = @At(
                    value = "INVOKE",
                    target = "Lat/petrak/hexcasting/api/casting/eval/CastingEnvironment;postExecution(Lat/petrak/hexcasting/api/casting/eval/CastResult;)V"
            )
    )
    private void decrementPondering(List<? extends Iota> iotas, ServerLevel world, CallbackInfoReturnable<ExecutionClientView> cir) {
        CompoundTag userData = ((CastingVM) (Object) this).getImage().getUserData();
        if (userData.contains("pondering", Tag.TAG_INT)) {
            switch (userData.getInt("pondering")) {
                case 2 -> userData.putInt("pondering", 1);
                case 1 -> userData.remove("pondering");
            }
        }
    }
}