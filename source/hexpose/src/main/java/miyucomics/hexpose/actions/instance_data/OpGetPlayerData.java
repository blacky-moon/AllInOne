package miyucomics.hexpose.actions.instance_data;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.EntityIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.function.Function;

public class OpGetPlayerData implements ConstMediaAction {
    private final Function<ServerPlayer, List<Iota>> processor;

    public OpGetPlayerData(Function<ServerPlayer, List<Iota>> processor) {
        this.processor = processor;
    }

    @Override
    public int getArgc() {
        return 1;
    }

    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment env) {
        Iota arg = args.get(0);
        if (!(arg instanceof EntityIota)) {
            throw MishapInvalidIota.ofType(arg, 0, "entity");
        }
        var entity = ((EntityIota) arg).getEntity(env.getWorld());
        if (entity == null) {
            throw MishapInvalidIota.ofType(arg, 0, "entity");
        }
        env.assertEntityInRange(entity);
        if (!(entity instanceof ServerPlayer)) {
            throw MishapInvalidIota.ofType(arg, 0, "player");
        }
        return processor.apply((ServerPlayer) entity);
    }
}