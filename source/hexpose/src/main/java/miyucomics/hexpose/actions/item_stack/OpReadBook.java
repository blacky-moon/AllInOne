package miyucomics.hexpose.actions.item_stack;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.ListIota;
import at.petrak.hexcasting.api.casting.iota.NullIota;
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota;
import miyucomics.hexpose.iotas.ItemStackIota;
import miyucomics.hexpose.iotas.TextIota;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WritableBookItem;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.item.component.WritableBookContent;
import net.minecraft.world.item.component.WrittenBookContent;

import java.util.ArrayList;
import java.util.List;

public class OpReadBook implements ConstMediaAction {
    public static final OpReadBook INSTANCE = new OpReadBook();

    private OpReadBook() {}

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

        List<Iota> pages = new ArrayList<>();

        if (book.getItem() instanceof WritableBookItem) {
            WritableBookContent content = book.get(DataComponents.WRITABLE_BOOK_CONTENT);
            if (content == null) {
                return List.of(new NullIota());
            }
            for (var page : content.pages()) {
                pages.add(new TextIota(Component.literal(page.raw())));
            }
        } else if (book.getItem() instanceof WrittenBookItem) {
            WrittenBookContent content = book.get(DataComponents.WRITTEN_BOOK_CONTENT);
            if (content == null) {
                return List.of(new NullIota());
            }
            for (var page : content.pages()) {
                pages.add(new TextIota(page.raw()));
            }
        } else {
            return List.of(new NullIota());
        }

        return List.of(new ListIota(pages));
    }
}