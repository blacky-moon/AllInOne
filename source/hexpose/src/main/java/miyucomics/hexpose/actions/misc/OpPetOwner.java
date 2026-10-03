package miyucomics.hexpose.actions.misc;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.EntityIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.NullIota;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity;
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;

import java.util.List;

public class OpPetOwner implements ConstMediaAction {
    public static final OpPetOwner INSTANCE = new OpPetOwner();

    private OpPetOwner() {}

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
            return List.of(new NullIota());
        }
        env.assertEntityInRange(entity);

        if (!(entity instanceof TamableAnimal)) {
            throw MishapBadEntity.of(entity, "tamable");
        }
        Entity owner = ((TamableAnimal) entity).getOwner();
        if (owner != null && env.isEntityInRange(owner)) {
            return List.of(new EntityIota(owner));
        }
        return List.of(new NullIota());
    }
}