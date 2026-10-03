package blacky_moon.miyu.overevaluate.mixin;

import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import blacky_moon.miyu.overevaluate.actions.modifiers.OpKeepArguments;
import com.llamalad7.mixinextras.sugar.Local;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

// SpellAction.operate() — дефолтная реализация в интерфейсе (Kotlin компилирует её
// либо в SpellAction.DefaultImpls, либо (с -Xjvm-default=all/compatibility) прямо
// в SpellAction.class как non-abstract метод интерфейса).
// Она берёт `argc` элементов через stack.takeLast()+removeLast(), зовёт execute/
// executeWithUserdata и строит image2 = image.copy(stack = stack, ...) БЕЗ
// возврата args обратно в стек. Чтобы реализовать "ponder"/keep-arguments,
// нужно после построения image2 вернуть args обратно на верх стека.
//
// ВАЖНО: если сборка не находит класс SpellAction$DefaultImpls (Kotlin с
// -Xjvm-default=all не генерирует его), поменяй Mixin target ниже на
// SpellAction.class — сигнатура @At/@Inject останется той же.
@Mixin(value = SpellAction.DefaultImpls.class, remap = false)
public class SpellActionMixin {

    @Inject(
            method = "operate",
            at = @At(
                    value = "INVOKE",
                    target = "Lat/petrak/hexcasting/api/casting/eval/vm/CastingImage;copy$default(Lat/petrak/hexcasting/api/casting/eval/vm/CastingImage;Ljava/util/List;ILjava/util/List;ZZJLnet/minecraft/nbt/CompoundTag;ILjava/lang/Object;)Lat/petrak/hexcasting/api/casting/eval/vm/CastingImage;"
            )
    )
    private static void dontEatArguments(
            SpellAction $this,
            CastingEnvironment env,
            CastingImage image,
            SpellContinuation continuation,
            CallbackInfoReturnable<OperationResult> cir,
            @Local(name = "args") List<Iota> args,
            @Local(name = "stack") List<Iota> stack
    ) {
        if (OpKeepArguments.shouldKeepArguments(image))
            stack.addAll(args);
    }
}