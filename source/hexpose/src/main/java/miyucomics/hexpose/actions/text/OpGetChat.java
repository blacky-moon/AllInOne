package miyucomics.hexpose.actions.text;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import miyucomics.hexpose.utils.ChatHandler;

import java.util.List;

public class OpGetChat implements ConstMediaAction {
    public static final OpGetChat INSTANCE = new OpGetChat();

    private OpGetChat() {}

    @Override
    public int getArgc() {
        return 0;
    }

    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment env) {
        return List.of(new at.petrak.hexcasting.api.casting.iota.ListIota(ChatHandler.getLog()));
    }
}