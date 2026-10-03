package miyucomics.hexpose.actions.blockstates;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.ListIota; // Импортируем ListIota
import at.petrak.hexcasting.api.casting.iota.Vec3Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota;
import miyucomics.hexpose.iotas.IdentifierIota;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class OpGetBlockProperties implements ConstMediaAction {
    public static final OpGetBlockProperties INSTANCE = new OpGetBlockProperties();

    private OpGetBlockProperties() {}

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

        var state = env.getWorld().getBlockState(pos);

        List<Iota> propertyIotas = new ArrayList<>();

        for (var property : state.getProperties()) {
            propertyIotas.add(new IdentifierIota(
                    ResourceLocation.fromNamespaceAndPath("facet", property.getName())
            ));
        }

        return List.of(new ListIota(propertyIotas));
    }
}