package miyucomics.hexpose.actions.instance_data;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.Vec3Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Function;

public class OpGetBlockStateData implements ConstMediaAction {
    private final Function<BlockState, List<Iota>> processor;

    public OpGetBlockStateData(Function<BlockState, List<Iota>> processor) {
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
        return processor.apply(env.getWorld().getBlockState(pos));
    }
}