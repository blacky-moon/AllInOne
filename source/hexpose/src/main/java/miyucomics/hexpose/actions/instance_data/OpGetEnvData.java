package miyucomics.hexpose.actions.instance_data;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;

import java.util.List;
import java.util.function.Function;

public class OpGetEnvData implements ConstMediaAction {
    private final Function<CastingEnvironment, List<Iota>> processor;

    public OpGetEnvData(Function<CastingEnvironment, List<Iota>> processor) {
        this.processor = processor;
    }

    @Override
    public int getArgc() {
        return 0;
    }

    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment env) {
        return processor.apply(env);
    }
}