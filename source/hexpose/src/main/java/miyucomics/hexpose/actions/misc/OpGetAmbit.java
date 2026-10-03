package miyucomics.hexpose.actions.misc;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.*;
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class OpGetAmbit implements ConstMediaAction {
    public static final OpGetAmbit INSTANCE = new OpGetAmbit();

    private OpGetAmbit() {}

    @Override
    public int getArgc() {
        return 1;
    }

    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment env) {
        Iota iota = args.get(0);
        boolean result;
        if (iota instanceof EntityIota) {
            var entity = ((EntityIota) iota).getEntity(env.getWorld());
            if (entity == null) {
                result = false;
            } else {
                result = env.isEntityInRange(entity);
            }
        } else if (iota instanceof Vec3Iota) {
            Vec3 vec = ((Vec3Iota) iota).getVec3();
            result = env.isVecInRange(vec) && env.isVecInWorld(vec);
        } else {
            throw MishapInvalidIota.of(args.get(0), 0, "entity_or_vector");
        }
        return List.of(new BooleanIota(result));
    }
}