package miyucomics.hexpose.actions.text;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import miyucomics.hexpose.utils.ChatHandler;

import java.util.List;

public class OpGetMessage implements ConstMediaAction {
    public static final OpGetMessage INSTANCE = new OpGetMessage();

    private OpGetMessage() {}

    @Override
    public int getArgc() {
        return 0;
    }

    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment env) {
        return ChatHandler.getLast();
    }
}