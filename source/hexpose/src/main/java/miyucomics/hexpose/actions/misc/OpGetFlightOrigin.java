package miyucomics.hexpose.actions.misc;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.NullIota;
import at.petrak.hexcasting.api.casting.iota.Vec3Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class OpGetFlightOrigin implements ConstMediaAction {
    public static final OpGetFlightOrigin INSTANCE = new OpGetFlightOrigin();

    private OpGetFlightOrigin() {}

    @Override
    public int getArgc() {
        return 0;
    }

    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment env) {
        var entity = env.getCastingEntity();
        if (!(entity instanceof ServerPlayer)) {
            throw new MishapBadCaster();
        }

        var flight = IXplatAbstractions.INSTANCE.getFlight((ServerPlayer) entity);
        if (flight == null || flight.radius() < 0.0) {
            return List.of(new NullIota());
        }

        return List.of(new Vec3Iota(flight.origin()));
    }
}