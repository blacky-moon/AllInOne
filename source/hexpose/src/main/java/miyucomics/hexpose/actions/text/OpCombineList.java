package miyucomics.hexpose.actions.text;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.ListIota;
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota;
import miyucomics.hexpose.iotas.TextIota;
import miyucomics.hexpose.utils.TextUtils;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class OpCombineList implements ConstMediaAction {
    public static final OpCombineList INSTANCE = new OpCombineList();

    private OpCombineList() {}

    @Override
    public int getArgc() {
        return 1;
    }

    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment env) {
        Iota arg = args.get(0);
        if (!(arg instanceof ListIota)) {
            throw MishapInvalidIota.ofType(arg, 0, "list");
        }
        ListIota listIota = (ListIota) arg;
        List<Component> components = new ArrayList<>();
        for (Iota iota : listIota.getList()) {
            components.add(iota.display());
        }
        return TextIota.asActionResult(TextUtils.mergeText(components));
    }
}