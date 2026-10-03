package miyucomics.hexpose.actions.text;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import miyucomics.hexpose.iotas.TextIota;
import net.minecraft.network.chat.Component;

import java.util.List;

public class OpCreateText implements ConstMediaAction {
    public static final OpCreateText INSTANCE = new OpCreateText();

    private OpCreateText() {}

    @Override
    public int getArgc() {
        return 1;
    }

    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment env) {
        Iota arg = args.get(0);
        if (arg instanceof TextIota) {
            Component text = ((TextIota) arg).getText();
            return TextIota.asActionResult(Component.literal(text.getString()));
        }
        return TextIota.asActionResult(arg.display());
    }
}