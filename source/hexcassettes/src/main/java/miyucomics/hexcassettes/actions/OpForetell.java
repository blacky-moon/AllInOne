package miyucomics.hexcassettes.actions;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.DoubleIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.NullIota;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster;
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota;
import miyucomics.hexcassettes.HexcassettesMain;
import miyucomics.hexcassettes.PlayerEntityMinterface;
import miyucomics.hexpose.iotas.TextIota;
import net.minecraft.network.chat.Component;

import java.util.List;

public class OpForetell implements ConstMediaAction {
    @Override
    public int getArgc() {
        return 1;
    }

    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment env) {
        var caster = env.getCastingEntity();
        if (!(caster instanceof PlayerEntityMinterface minterface))
            throw new MishapBadCaster();

        Component text;
        if (args.get(0) instanceof TextIota ti) {
            text = ti.getText();
        } else {
            throw MishapInvalidIota.ofType(args.get(0), 0, "text");
        }

        String pattern = HexcassettesMain.serializeKey(text, env.getWorld().registryAccess());
        var queuedHex = minterface.getCassetteState().hexes.get(pattern);
        if (queuedHex != null) {
            return List.of(new DoubleIota(queuedHex.delay));
        }
        return List.of(new NullIota());
    }
}