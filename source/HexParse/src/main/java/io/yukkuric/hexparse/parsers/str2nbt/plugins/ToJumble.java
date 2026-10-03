package io.yukkuric.hexparse.parsers.str2nbt.plugins;

import at.petrak.hexcasting.api.casting.iota.Iota;
import blacky_moon.miyu.overevaluate.actions.jumble.Jumble;
import blacky_moon.miyu.overevaluate.actions.jumble.JumbleIota;

import java.util.ArrayList;
import java.util.List;

import static io.yukkuric.hexparse.parsers.str2nbt.BaseConstParser.Regex;

/**
 * Парсер строк вида "jumble_<args>:<i0>:<i1>:..." в JumbleIota.
 * Пример: jumble_2:1:0  =>  Jumble(args = 2, jumble = [1, 0])
 * (Это тот же эффект, что у "swap" — берёт 2 аргумента и переставляет их местами.)
 * Пустой список индексов: jumble_2 (без хвоста) => Jumble(args = 2, jumble = [])
 *
 * ВАЖНО: HexParse's CodeCutter токенизирует по regex, который вырезает
 * '[', ']', ',' как отдельные/недопустимые в токене символы (см. CodeCutter.pTokens:
 * разрешён только класс [\w./\-:#...]). Поэтому формат "jumble_2[1,0]" НЕ работает —
 * скобки и запятая всегда режутся до отдельного/сломанного токена. ':' входит
 * в разрешённый набор символов токена, поэтому используем его как разделитель.
 */
public class ToJumble extends Regex {
    public ToJumble() {
        super("^jumble_\\d+(:\\d+)*$");
    }

    @Override
    public Iota parse(String node) {
        // формат: jumble_<args>:<i0>:<i1>:...
        var parts = node.split(":");
        // parts[0] == "jumble_<args>"
        var argsPart = parts[0].substring("jumble_".length());
        int argCount = Integer.parseInt(argsPart);

        List<Integer> jumbleList = new ArrayList<>();
        for (int i = 1; i < parts.length; i++) {
            int idx = Integer.parseInt(parts[i]);
            if (idx >= argCount)
                throw new IllegalArgumentException("Jumble index " + idx + " out of range for args=" + argCount);
            jumbleList.add(idx);
        }

        return new JumbleIota(new Jumble(argCount, jumbleList));
    }
}