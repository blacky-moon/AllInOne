package miyucomics.hexpose.actions.item_stack;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.NullIota; // Импортируем NullIota
import at.petrak.hexcasting.api.casting.iota.EntityIota;
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota;
import miyucomics.hexpose.iotas.ItemStackIota;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack; // Импортируем ItemStack

import java.util.List;

public class OpItemIota implements ConstMediaAction {
    public static final OpItemIota INSTANCE = new OpItemIota();

    private OpItemIota() {}

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

        if (!(entity instanceof ItemEntity)) {
            throw MishapInvalidIota.ofType(arg, 0, "item_entity");
        }

        ItemStack stack = ((ItemEntity) entity).getItem();

        // 1. Защита от пустых сущностей (воздуха)
        if (stack.isEmpty()) {
            return List.of(new NullIota());
        }

        // 2. Костыль-защита против падения реестра чар в дев-версии Hex Casting на 1.21.1
        if (stack.isEnchanted()) {
            return List.of(new NullIota());
        }

        return List.of(new ItemStackIota(stack));
    }
}