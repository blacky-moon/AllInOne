package miyucomics.hexcassettes.actions;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.ListIota;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster;
import miyucomics.hexcassettes.HexcassettesMain;
import miyucomics.hexcassettes.PlayerEntityMinterface;
import miyucomics.hexpose.iotas.TextIota;

import java.util.ArrayList;
import java.util.List;

public class OpBusy implements ConstMediaAction {
    @Override
    public int getArgc() {
        return 0;
    }

    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment env) {
        var caster = env.getCastingEntity();
        if (!(caster instanceof PlayerEntityMinterface minterface))
            throw new MishapBadCaster();

        List<Iota> result = new ArrayList<>();
        for (String key : minterface.getCassetteState().hexes.keySet()) {
            result.add(new TextIota(HexcassettesMain.deserializeKey(key, env.getWorld().registryAccess())));
        }
        return List.of(new ListIota(result));
    }
}