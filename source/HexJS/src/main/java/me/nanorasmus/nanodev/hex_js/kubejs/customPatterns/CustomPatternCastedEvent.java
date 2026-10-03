package me.nanorasmus.nanodev.hex_js.kubejs.customPatterns;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import dev.latvian.mods.kubejs.event.KubeEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class CustomPatternCastedEvent implements KubeEvent {

    private final Entity caster;
    private final HexPattern pattern;
    private List<Iota> stack;
    private Iota ravenmind;
    private final CastingEnvironment env;

    private boolean shouldMishap = false;
    private String mishapMessage = "Mishap message not found!";

    public CustomPatternCastedEvent(Entity caster, HexPattern pattern, List<Iota> stack,
                                    Iota ravenmind, CastingEnvironment env) {
        this.caster = caster;
        this.pattern = pattern;
        this.stack = new ArrayList<>(stack);
        this.ravenmind = ravenmind;
        this.env = env;
    }

    public Entity getCaster() { return caster; }

    public List<Iota> getStack() { return stack; }
    public void setStack(List<Iota> newStack) { stack = newStack; }

    public Iota getRavenmind() { return ravenmind; }
    public void setRavenmind(Iota newRavenmind) { ravenmind = newRavenmind; }

    public HexPattern getPattern() { return pattern; }

    public boolean isInAmbit(Vec3 location) {
        return env.isVecInRange(location);
    }

    public void scheduleMishap(String mishapMessage) {
        shouldMishap = true;
        this.mishapMessage = mishapMessage;
    }

    public boolean isMishapping() { return shouldMishap; }
    public String getMishapMessage() { return mishapMessage; }

    /** Возвращает остаток, который не удалось списать (0 = всё списано). */
    public long tryConsumeMedia(long media) {
        return env.extractMedia(media, false);
    }
}