package miyucomics.hexpose.actions.item_stack;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.DoubleIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota;
import miyucomics.hexpose.iotas.IdentifierIota;
import miyucomics.hexpose.iotas.ItemStackIota;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class OpCreateStack implements ConstMediaAction {
    public static final OpCreateStack INSTANCE = new OpCreateStack();

    private OpCreateStack() {}

    @Override
    public int getArgc() {
        return 2;
    }

    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment env) {
        Iota arg0 = args.get(0);
        if (!(arg0 instanceof IdentifierIota)) {
            throw MishapInvalidIota.ofType(arg0, 0, "identifier");
        }
        ResourceLocation id = ((IdentifierIota) arg0).getIdentifier();

        if (!BuiltInRegistries.ITEM.containsKey(id)) {
            throw MishapInvalidIota.of(args.get(0), 1, "item_id");
        }

        Iota arg1 = args.get(1);
        if (!(arg1 instanceof DoubleIota)) {
            throw MishapInvalidIota.ofType(arg1, 1, "double");
        }
        int count = (int) Math.round(((DoubleIota) arg1).getDouble());
        if (count < 1) {
            count = 1;
        }

        ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.get(id), count);
        return List.of(new ItemStackIota(stack));
    }
}