package blacky_moon.miyu.overevaluate.mixin;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import blacky_moon.miyu.overevaluate.actions.modifiers.OpKeepArguments;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = ConstMediaAction.class, remap = false)
public interface ConstMediaActionMixin {

    @WrapOperation(
            method = "operate",
            at = @At(
                    value = "INVOKE",
                    target = "Lat/petrak/hexcasting/api/casting/castables/ConstMediaAction;executeWithOpCount(Ljava/util/List;Lat/petrak/hexcasting/api/casting/eval/CastingEnvironment;)Lat/petrak/hexcasting/api/casting/castables/ConstMediaAction$CostMediaActionResult;"
            )
    )
    private ConstMediaAction.CostMediaActionResult dontEatArguments(
            ConstMediaAction instance,
            List<Iota> args,
            CastingEnvironment env,
            Operation<ConstMediaAction.CostMediaActionResult> original,
            CastingEnvironment envParam,
            CastingImage image,
            SpellContinuation continuation
    ) {
        ConstMediaAction.CostMediaActionResult result = original.call(instance, args, env);

        if (OpKeepArguments.shouldKeepArguments(image)) {
            List<Iota> modifiedStack = new ArrayList<>(args);
            modifiedStack.addAll(result.getResultStack());
            return new ConstMediaAction.CostMediaActionResult(
                    modifiedStack,
                    result.getOpCount()
            );
        }

        return result;
    }
}