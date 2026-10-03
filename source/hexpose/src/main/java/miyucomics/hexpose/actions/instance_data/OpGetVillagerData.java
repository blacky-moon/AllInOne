package miyucomics.hexpose.actions.instance_data;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.EntityIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity;
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;

import java.util.List;
import java.util.function.Function;

public class OpGetVillagerData implements ConstMediaAction {
    private final Function<Villager, List<Iota>> processor;

    public OpGetVillagerData(Function<Villager, List<Iota>> processor) {
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
        Entity entity = ((EntityIota) arg).getEntity(env.getWorld());
        if (entity == null) {
            throw MishapInvalidIota.ofType(arg, 0, "entity");
        }
        env.assertEntityInRange(entity);
        if (!(entity instanceof Villager)) {
            throw MishapBadEntity.of(entity, "villager");
        }
        return processor.apply((Villager) entity);
    }
}