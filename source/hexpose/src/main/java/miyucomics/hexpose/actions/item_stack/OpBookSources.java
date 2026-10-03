package miyucomics.hexpose.actions.item_stack;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.DoubleIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.NullIota;
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota;
import miyucomics.hexpose.iotas.ItemStackIota;
import miyucomics.hexpose.iotas.TextIota;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.item.component.WrittenBookContent;

import java.util.List;

public class OpBookSources implements ConstMediaAction {
    public static final OpBookSources INSTANCE = new OpBookSources();

    private OpBookSources() {}

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
        ItemStack book = ((ItemStackIota) arg).getStack();

        if (!(book.getItem() instanceof WrittenBookItem)) {
            return List.of(new NullIota());
        }

        WrittenBookContent content = book.get(DataComponents.WRITTEN_BOOK_CONTENT);
        if (content == null) {
            return List.of(new NullIota());
        }


        String title = content.title().raw();
        String author = content.author();
        int generation = content.generation();

        return List.of(
                new TextIota(Component.literal(title)),
                new TextIota(Component.literal(author)),
                new DoubleIota(generation)
        );
    }
}