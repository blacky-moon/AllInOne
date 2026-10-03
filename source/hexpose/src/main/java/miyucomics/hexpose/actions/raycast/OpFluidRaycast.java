package miyucomics.hexpose.actions.raycast;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.NullIota;
import at.petrak.hexcasting.api.casting.iota.Vec3Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota;
import miyucomics.hexpose.utils.DDAUtils;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class OpFluidRaycast implements ConstMediaAction {
    public static final OpFluidRaycast INSTANCE = new OpFluidRaycast();

    private OpFluidRaycast() {}

    @Override
    public int getArgc() {
        return 2;
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
        return DDAUtils.raycastBlock(
                start,
                direction,
                pos -> env.getWorld().getBlockState(pos).getBlock() instanceof LiquidBlock,
                pos -> !env.isVecInRange(Vec3.atCenterOf(pos))
        );
    }
}