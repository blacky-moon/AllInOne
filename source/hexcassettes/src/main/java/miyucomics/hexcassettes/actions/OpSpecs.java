package miyucomics.hexcassettes.actions;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.DoubleIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster;
import miyucomics.hexcassettes.PlayerEntityMinterface;

import java.util.List;

public class OpSpecs implements ConstMediaAction {
    @Override
    public int getArgc() {
        return 0;
    }

    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment env) {
        var caster = env.getCastingEntity();
        if (!(caster instanceof PlayerEntityMinterface minterface))
            throw new MishapBadCaster();
        return List.of(new DoubleIota(minterface.getCassetteState().owned));
    }
}