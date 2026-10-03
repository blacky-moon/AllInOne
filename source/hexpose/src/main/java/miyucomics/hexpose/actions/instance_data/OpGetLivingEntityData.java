package miyucomics.hexpose.actions.instance_data;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.EntityIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.function.Function;

public class OpGetLivingEntityData implements ConstMediaAction {
    private final Function<LivingEntity, List<Iota>> processor;

    public OpGetLivingEntityData(Function<LivingEntity, List<Iota>> processor) {
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
            throw MishapInvalidIota.ofType(arg, 0, "lenient_living");
        }
        var entity = ((EntityIota) arg).getEntity(env.getWorld());
        if (entity == null) {
            throw MishapInvalidIota.ofType(arg, 0, "lenient_living");
        }
        env.assertEntityInRange(entity);
        if (!(entity instanceof LivingEntity)) {
            throw MishapInvalidIota.ofType(arg, 0, "lenient_living");
        }
        return processor.apply((LivingEntity) entity);
    }
}