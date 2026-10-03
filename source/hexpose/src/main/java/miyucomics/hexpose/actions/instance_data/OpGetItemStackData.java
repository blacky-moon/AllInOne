package miyucomics.hexpose.actions.instance_data;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota;
import miyucomics.hexpose.iotas.ItemStackIota;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.BiFunction;

public class OpGetItemStackData implements ConstMediaAction {
    private final BiFunction<ItemStack, CastingEnvironment, List<Iota>> processor;

    public OpGetItemStackData(BiFunction<ItemStack, CastingEnvironment, List<Iota>> processor) {
        this.processor = processor;
    }

    @Override
    public int getArgc() {
        return 1;
    }

    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment env) {
        Iota arg = args.get(0);
        if (!(arg instanceof ItemStackIota)) {
            throw MishapInvalidIota.ofType(arg, 0, "item_stack");
        }
        return processor.apply(((ItemStackIota) arg).getStack(), env);
    }
}