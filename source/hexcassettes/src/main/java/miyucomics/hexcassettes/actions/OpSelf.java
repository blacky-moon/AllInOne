package miyucomics.hexcassettes.actions;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.NullIota;
import miyucomics.hexcassettes.CassetteCastEnv;
import miyucomics.hexcassettes.HexcassettesMain;
import miyucomics.hexpose.iotas.TextIota;

import java.util.List;

public class OpSelf implements ConstMediaAction {
    @Override
    public int getArgc() {
        return 0;
    }

    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment env) {
        if (!(env instanceof CassetteCastEnv cassetteEnv))
            return List.of(new NullIota());
        return List.of(new TextIota(
                HexcassettesMain.deserializeKey(cassetteEnv.key, env.getWorld().registryAccess())
        ));
    }
}