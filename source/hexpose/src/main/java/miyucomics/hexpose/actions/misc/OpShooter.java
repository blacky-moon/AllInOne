package miyucomics.hexpose.actions.misc;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.EntityIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.NullIota;
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;

import java.util.List;

public class OpShooter implements ConstMediaAction {
    public static final OpShooter INSTANCE = new OpShooter();

    private OpShooter() {}

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

        if (!(entity instanceof Projectile)) {
            return List.of(new NullIota());
        }
        Entity shooter = ((Projectile) entity).getOwner();
        if (shooter == null) {
            return List.of(new NullIota());
        }
        if (env.isEntityInRange(shooter)) {
            return List.of(new EntityIota(shooter));
        }
        return List.of(new NullIota());
    }
}