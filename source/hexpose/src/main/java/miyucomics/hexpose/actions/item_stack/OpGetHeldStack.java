package miyucomics.hexpose.actions.item_stack;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.EntityIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.NullIota; // Импортируем NullIota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota;
import miyucomics.hexpose.iotas.ItemStackIota;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack; // Импортируем ItemStack

import java.util.List;

public class OpGetHeldStack implements ConstMediaAction {
    private final InteractionHand hand;

    public OpGetHeldStack(InteractionHand hand) {
        this.hand = hand;
    }

    @Override
    public int getArgc() {
        return 1;
    }

    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment env) {
        Iota arg = args.get(0);
        if (!(arg instanceof EntityIota)) {
            throw MishapInvalidIota.ofType(arg, 0, "lenient_living");
        }
        var entity = ((EntityIota) arg).getEntity(env.getWorld());
        if (entity == null) {
            throw MishapInvalidIota.ofType(arg, 0, "lenient_living");
        }
        env.assertEntityInRange(entity);

        if (!(entity instanceof LivingEntity)) {
            throw MishapInvalidIota.ofType(arg, 0, "lenient_living");
        }

        ItemStack stack = ((LivingEntity) entity).getItemInHand(hand);

        // Защита: если рука пустая, возвращаем NullIota, чтобы не сломать сериализацию
        if (stack.isEmpty()) {
            return List.of(new NullIota());
        }

        return List.of(new ItemStackIota(stack));
    }
}