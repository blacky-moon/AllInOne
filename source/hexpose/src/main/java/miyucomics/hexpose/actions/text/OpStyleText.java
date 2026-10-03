package miyucomics.hexpose.actions.text;

import at.petrak.hexcasting.api.casting.castables.Action;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota;
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs;
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds;
import miyucomics.hexpose.iotas.TextIota;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class OpStyleText implements Action {
    private final BiFunction<List<? extends Iota>, Style, Style> setOp;
    private final Function<Style, Iota> getOp;

    public OpStyleText(BiFunction<List<? extends Iota>, Style, Style> setOp, Function<Style, Iota> getOp) {
        this.setOp = setOp;
        this.getOp = getOp;
    }

    @Override
    public OperationResult operate(CastingEnvironment env, CastingImage image, SpellContinuation continuation) {
        List<Iota> stack = new ArrayList<>(image.getStack());
        if (stack.isEmpty()) {
            throw new MishapNotEnoughArgs(1, 0);
        }

        Iota last = stack.get(stack.size() - 1);
        if (last instanceof TextIota) {
            stack.remove(stack.size() - 1);
            stack.add(getOp.apply(((TextIota) last).getText().getStyle()));
            CastingImage newImage = image.copy(
                    stack,
                    image.getParenCount(),
                    image.getParenthesized(),
                    image.getEscapeNext(),
                    image.getSimulateNext(),
                    image.getOpsConsumed() + 1,
                    image.getUserData()
            );
            return new OperationResult(newImage, List.of(), continuation, HexEvalSounds.NORMAL_EXECUTE);
        }

        if (stack.size() == 1) {
            throw new MishapNotEnoughArgs(2, 1);
        }

        Iota textIota = stack.get(stack.size() - 2);
        if (!(textIota instanceof TextIota)) {
            throw MishapInvalidIota.ofType(textIota, 1, "text");
        }

        // Берем последние 2 аргумента и текущий стиль текста
        List<? extends Iota> args = stack.subList(stack.size() - 2, stack.size());
        Style currentStyle = ((TextIota) textIota).getText().getStyle();
        Style newStyle = setOp.apply(args, currentStyle);

        Component newText = TextIota.mapTextStyle(((TextIota) textIota).getText(), (style) -> newStyle);

        stack.remove(stack.size() - 1);
        stack.remove(stack.size() - 1);
        stack.add(new TextIota(newText));

        CastingImage newImage = image.copy(
                stack,
                image.getParenCount(),
                image.getParenthesized(),
                image.getEscapeNext(),
                image.getSimulateNext(),
                image.getOpsConsumed() + 1,
                image.getUserData()
        );
        return new OperationResult(newImage, List.of(), continuation, HexEvalSounds.NORMAL_EXECUTE);
    }
}