package miyucomics.hexpose.actions.instance_data;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import net.minecraft.server.level.ServerLevel;

import java.util.List;
import java.util.function.Function;

public class OpGetWorldData implements ConstMediaAction {
    private final Function<ServerLevel, List<Iota>> processor;

    public OpGetWorldData(Function<ServerLevel, List<Iota>> processor) {
        this.processor = processor;
    }

    @Override
    public int getArgc() {
        return 0;
    }

    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment env) {
        return processor.apply(env.getWorld());
    }
}