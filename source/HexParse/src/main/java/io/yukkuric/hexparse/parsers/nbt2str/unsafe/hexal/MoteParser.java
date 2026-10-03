package io.yukkuric.hexparse.parsers.nbt2str.unsafe.hexal;

import io.yukkuric.hexparse.parsers.nbt2str.INbt2Str;
import ram.talia.hexal.api.casting.iota.MoteIota;
import ram.talia.hexal.api.mediafieditems.MediafiedItemManager;

public class MoteParser implements INbt2Str<MoteIota> {

    @Override
    public Class<MoteIota> getType() {
        return MoteIota.class;
    }

    @Override
    public String parse(MoteIota iota) {
        // Получаем индекс напрямую из объекта иоты
        MediafiedItemManager.Index index = iota.getItemIndex();

        var uuid = index.getStorage();
        var idx = index.getIndex();

        return "mote_%s_%s".formatted(uuid, idx);
    }

    public static final MoteParser INSTANCE = new MoteParser();
}