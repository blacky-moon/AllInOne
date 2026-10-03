package miyucomics.hexpose.actions.instance_data;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.Vec3Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.BiFunction;

public class OpGetPositionData implements ConstMediaAction {
    private final BiFunction<ServerLevel, BlockPos, List<Iota>> processor;

    public OpGetPositionData(BiFunction<ServerLevel, BlockPos, List<Iota>> processor) {
        this.processor = processor;
    }

    @Override
    public int getArgc() {
        return 1;
    }

    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment env) {
        Iota arg = args.get(0);
        if (!(arg instanceof Vec3Iota)) {
            throw MishapInvalidIota.ofType(arg, 0, "vec3");
        }
        Vec3 vec = ((Vec3Iota) arg).getVec3();
        BlockPos pos = BlockPos.containing(vec);
        env.assertPosInRange(pos);
        return processor.apply(env.getWorld(), pos);
    }
}