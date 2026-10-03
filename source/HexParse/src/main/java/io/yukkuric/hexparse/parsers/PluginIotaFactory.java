package io.yukkuric.hexparse.parsers;

import io.yukkuric.hexparse.HexParse;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;

/**
 * Вспомогательные методы для сборки составных структур, используемых при парсинге
 * плагин-специфичных литералов (matrix и т.п.), которые сами по себе не являются Iota,
 * а представляют собой промежуточные NBT-контейнеры для передачи данных.
 *
 * ВАЖНО: этот класс больше не строит CompoundTag-обёртки поверх Iota (makeType/…) —
 * начиная с текущей версии hexcasting все Iota создаются напрямую через конструкторы
 * соответствующих классов. Здесь остаются только вспомогательные билдеры структур,
 * которые сами по себе не являются Iota (например, разбор матрицы в строку/список).
 */
public class PluginIotaFactory {
    public static CompoundTag makeMatrixTag(String[] raw) {
        var body = new CompoundTag();
        int ptr = 0, targetSize = 2;

        if (raw.length < targetSize)
            throw new IllegalArgumentException(HexParse.doTranslate("hexparse.msg.error.matrix.data_amount", raw.length, targetSize));

        int nrow, ncol;
        nrow = Integer.parseInt(raw[ptr]);
        if (nrow < 0)
            throw new IllegalArgumentException(HexParse.doTranslate("hexparse.msg.error.matrix.size", nrow));
        ptr++;
        body.putInt("rows", nrow);
        ncol = Integer.parseInt(raw[ptr]);
        if (ncol < 0)
            throw new IllegalArgumentException(HexParse.doTranslate("hexparse.msg.error.matrix.size", ncol));
        ptr++;
        body.putInt("cols", ncol);

        targetSize = nrow * ncol;
        if (raw.length - 2 < targetSize)
            throw new IllegalArgumentException(HexParse.doTranslate("hexparse.msg.error.matrix.data_amount", raw.length - 2, targetSize));

        var data = new ListTag();
        try {
            for (var i = 0; i < nrow; i++) {
                var row = new ListTag();
                for (var j = 0; j < ncol; j++) {
                    row.add(DoubleTag.valueOf(Double.parseDouble(raw[ptr])));
                    ptr++;
                }
                data.add(row);
            }
        } catch (Throwable e) {
            throw new IllegalArgumentException(HexParse.doTranslate("hexparse.msg.error.matrix.value", raw[ptr]));
        }
        body.put("mat", data);

        return body;
    }
}