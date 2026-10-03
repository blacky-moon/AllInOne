package miyucomics.hexpose.actions.blockstates;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.DoubleIota;
import at.petrak.hexcasting.api.casting.iota.GarbageIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.NullIota;
import at.petrak.hexcasting.api.casting.iota.Vec3Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota;
import miyucomics.hexpose.iotas.IdentifierIota;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.ArrayList;

public class OpQueryBlockProperty implements ConstMediaAction {
    public static final OpQueryBlockProperty INSTANCE = new OpQueryBlockProperty();

    private OpQueryBlockProperty() {}

    @Override
    public int getArgc() {
        return 2;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment env) {
        Iota arg0 = args.get(0);
        if (!(arg0 instanceof Vec3Iota)) {
            throw MishapInvalidIota.ofType(arg0, 0, "vec3");
        }
        Vec3 vec = ((Vec3Iota) arg0).getVec3();
        BlockPos pos = BlockPos.containing(vec);
        env.assertPosInRange(pos);

        Iota arg1 = args.get(1);
        if (!(arg1 instanceof IdentifierIota)) {
            throw MishapInvalidIota.ofType(arg1, 1, "identifier");
        }
        String target = ((IdentifierIota) arg1).getIdentifier().getPath();

        var state = env.getWorld().getBlockState(pos);

        // Ищем свойство по имени
        Property<?> property = state.getProperties().stream()
                .filter(p -> p.getName().equals(target))
                .findFirst()
                .orElse(null);

        if (property == null) {
            return List.of(new NullIota());
        }

        // Обрабатываем разные типы свойств
        if (property instanceof BooleanProperty boolProp) {
            return List.of(new DoubleIota(state.getValue(boolProp) ? 1.0 : 0.0));
        } else if (property instanceof DirectionProperty dirProp) {
            Direction dir = state.getValue(dirProp);
            Vec3 normal = new Vec3(dir.getNormal().getX(), dir.getNormal().getY(), dir.getNormal().getZ());
            return List.of(new Vec3Iota(normal));
        } else if (property instanceof EnumProperty enumProp) {
            Comparable<?> value = state.getValue(enumProp);
            // Получаем индекс через список возможных значений
            List<?> values = new ArrayList<>(enumProp.getPossibleValues());
            int index = values.indexOf(value);
            return List.of(new DoubleIota(index));
        } else if (property instanceof IntegerProperty intProp) {
            return List.of(new DoubleIota(state.getValue(intProp)));
        } else {
            return List.of(new GarbageIota());
        }
    }
}