package miyucomics.hexpose.actions.text;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import miyucomics.hexpose.iotas.TextIota;
import miyucomics.hexpose.utils.TextUtils;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class OpSplitText implements ConstMediaAction {
    public static final OpSplitText INSTANCE = new OpSplitText();

    private OpSplitText() {}

    @Override
    public int getArgc() {
        return 1;
    }

    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment env) {
        Component text = TextIota.getText(args, 0, getArgc());
        List<Component> split = TextUtils.split(text);
        List<Iota> result = new ArrayList<>();
        for (Component component : split) {
            result.add(new TextIota(component));
        }
        return List.of(new at.petrak.hexcasting.api.casting.iota.ListIota(result));
    }
}