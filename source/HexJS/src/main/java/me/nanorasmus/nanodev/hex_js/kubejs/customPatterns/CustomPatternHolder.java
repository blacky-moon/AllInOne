package me.nanorasmus.nanodev.hex_js.kubejs.customPatterns;

import at.petrak.hexcasting.api.casting.castables.Action;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.sideeffects.EvalSound;
import at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.api.casting.mishaps.Mishap;
import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.script.ScriptType;
import me.nanorasmus.nanodev.hex_js.kubejs.HexKubeJSPlugin;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CustomPatternHolder implements Action {

    Component name;
    HexPattern pattern;
    boolean isGreat;
    boolean causesBlindDiversion;

    public CustomPatternHolder(String name, HexPattern pattern, boolean isGreat, boolean causesBlindDiversion) {
        this.name = Component.literal(name);
        this.pattern = pattern;
        this.isGreat = isGreat;
        this.causesBlindDiversion = causesBlindDiversion;
    }

    @NotNull
    @Override
    public OperationResult operate(@NotNull CastingEnvironment env, @NotNull CastingImage image, @NotNull SpellContinuation continuation) {
        Iota ravenmind = null;

        CustomPatternCastedEvent event = new CustomPatternCastedEvent(
                env.getCaster(),
                pattern,
                new ArrayList<>(image.getStack()),
                ravenmind,
                env
        );
        EventResult result = HexKubeJSPlugin.patternCastedEventHandler.post(ScriptType.SERVER, event);

        event = (CustomPatternCastedEvent) result.value();

        if (event == null) {
            List<OperatorSideEffect> sideEffects = new ArrayList<>();

            sideEffects.add(new OperatorSideEffect.DoMishap(
                    new CustomPatternMishap("The HexJS pattern you just cast is missing a call to the \"finish()\" function at the end of the event!"),
                    new Mishap.Context(pattern, this.name)
            ));

            return new OperationResult(image, sideEffects, continuation,new EvalSound(null,1));
        }

        List<OperatorSideEffect> sideEffects = new ArrayList<>();

        if (event.isMishapping()) {
            sideEffects.add(new OperatorSideEffect.DoMishap(
                    new CustomPatternMishap(event.getMishapMessage()),
                    new Mishap.Context(pattern, this.name)
            ));
        }

        CastingImage newImage = image.copy(
                event.getStack(),
                image.getParenCount(),
                image.getParenthesized(),
                image.getEscapeNext(),
                image.getSimulateNext(),
                image.getOpsConsumed(),
                image.getUserData()
        );

        return new OperationResult(newImage, sideEffects, continuation, new EvalSound(null,1));
    }
    public Component getDisplayName() {
        return this.name;
    }
}