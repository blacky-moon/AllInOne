package miyucomics.hexpose.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.PlainTextContents;

import java.util.ArrayList;
import java.util.List;

public class TextUtils {
    public static List<Component> split(Component text) {
        List<Component> chars = new ArrayList<>();
        collectStyledCharacters(text, Style.EMPTY, chars);
        return chars;
    }

    private static void collectStyledCharacters(Component text, Style parentStyle, List<Component> out) {
        Style effectiveStyle = text.getStyle().applyTo(parentStyle);
        var content = text.getContents();
        if (content instanceof PlainTextContents.LiteralContents literal) {
            String string = literal.text();
            for (char c : string.toCharArray()) {
                out.add(Component.literal(String.valueOf(c)).setStyle(effectiveStyle));
            }
        }
        for (Component child : text.getSiblings()) {
            collectStyledCharacters(child, effectiveStyle, out);
        }
    }

    public static MutableComponent mergeText(List<Component> texts) {
        MutableComponent result = Component.empty();
        for (Component text : texts) {
            result.append(text);
        }
        return result;
    }
}