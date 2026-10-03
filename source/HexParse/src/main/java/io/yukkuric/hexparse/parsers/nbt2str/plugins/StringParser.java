package io.yukkuric.hexparse.parsers.nbt2str.plugins;

import io.yukkuric.hexparse.misc.StringProcessors.F;
import io.yukkuric.hexparse.parsers.nbt2str.INbt2Str;
import ram.talia.moreiotas.api.casting.iota.StringIota;

import static io.yukkuric.hexparse.misc.StringProcessors.*;

public class StringParser implements INbt2Str<StringIota> {
    private final String prefix;
    private final F[] processors;

    StringParser(String prefix, F... processors) {
        this.prefix = prefix;
        this.processors = processors;
    }

    @Override
    public Class<StringIota> getType() {
        return StringIota.class;
    }

    @Override
    public String parse(StringIota iota) {
        var raw = iota.getString();
        for (var f : processors) {
            raw = f.apply(raw);
        }
        return this.prefix + raw;
    }

    // Статические экземпляры для удобства использования
    public static final StringParser IOTA = new StringParser("type_", OMIT_HEX);
    public static final StringParser ENTITY = new StringParser("type/entity_", OMIT_MC);
    public static final StringParser STRING = new StringParser("str_");
}