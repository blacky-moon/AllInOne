package miyucomics.hexpose.actions.item_stack;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.ListIota; // Импортируем ListIota
import at.petrak.hexcasting.api.casting.iota.NullIota;
import at.petrak.hexcasting.api.casting.iota.Vec3Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota;
import miyucomics.hexpose.iotas.ItemStackIota;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class OpGetContainer implements ConstMediaAction {
    public static final OpGetContainer INSTANCE = new OpGetContainer();

    private OpGetContainer() {}

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

        BlockEntity blockEntity = env.getWorld().getBlockEntity(pos);
        if (!(blockEntity instanceof Container)) {
            return List.of(new NullIota());
        }

        Container inventory = (Container) blockEntity;
        List<Iota> containerItems = new ArrayList<>();

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            // Защита: полностью игнорируем пустые слоты (микро-воздух с нулевым стаком)
            if (!stack.isEmpty()) {
                containerItems.add(new ItemStackIota(stack));
            }
        }

        // Оборачиваем собранную коллекцию в одну ListIota, чтобы она вернулась списком
        return List.of(new ListIota(containerItems));
    }
}