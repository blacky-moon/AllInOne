package miyucomics.hexpose.actions.raycast;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.NullIota;
import at.petrak.hexcasting.api.casting.iota.Vec3Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota;
import miyucomics.hexpose.iotas.IdentifierIota;
import miyucomics.hexpose.utils.DDAUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class OpPiercingSurfaceRaycast implements ConstMediaAction {
    public static final OpPiercingSurfaceRaycast INSTANCE = new OpPiercingSurfaceRaycast();

    private OpPiercingSurfaceRaycast() {}

    @Override
    public int getArgc() {
        return 3;
    }

    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment env) {
        Iota arg0 = args.get(0);
        if (!(arg0 instanceof Vec3Iota)) {
            throw MishapInvalidIota.ofType(arg0, 0, "vec3");
        }
        Vec3 start = ((Vec3Iota) arg0).getVec3();
        env.assertVecInRange(start);

        Iota arg1 = args.get(1);
        if (!(arg1 instanceof Vec3Iota)) {
            throw MishapInvalidIota.ofType(arg1, 1, "vec3");
        }
        Vec3 direction = ((Vec3Iota) arg1).getVec3().normalize();

        if (direction.equals(Vec3.ZERO)) {
            return List.of(new NullIota());
        }

        Iota arg2 = args.get(2);
        if (!(arg2 instanceof IdentifierIota)) {
            throw MishapInvalidIota.ofType(arg2, 2, "identifier");
        }
        ResourceLocation id = ((IdentifierIota) arg2).getIdentifier();

        if (!BuiltInRegistries.BLOCK.containsKey(id)) {
            throw MishapInvalidIota.of(args.get(2), 0, "block_id");
        }
        var desired = BuiltInRegistries.BLOCK.get(id);

        return DDAUtils.raycastNormal(
                start,
                direction,
                pos -> env.getWorld().getBlockState(pos).is(desired),
                pos -> !env.isVecInRange(Vec3.atCenterOf(pos))
        );
    }
}