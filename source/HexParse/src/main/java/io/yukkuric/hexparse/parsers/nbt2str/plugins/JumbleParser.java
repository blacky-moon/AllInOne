package io.yukkuric.hexparse.parsers.nbt2str.plugins;

import at.petrak.hexcasting.api.casting.iota.Iota;
import blacky_moon.miyu.overevaluate.actions.jumble.JumbleIota;
import io.yukkuric.hexparse.parsers.nbt2str.INbt2Str;

/**
 * Обратный парсер: JumbleIota -> строка вида "jumble_<args>:<i0>:<i1>:...".
 * Симметричен ToJumble. См. комментарий в ToJumble про ограничения токенизатора.
 */
public class JumbleParser implements INbt2Str<JumbleIota> {
    public static final JumbleParser INSTANCE = new JumbleParser();

    @Override
    public Class<JumbleIota> getType() {
        return JumbleIota.class;
    }

    @Override
    public String parse(JumbleIota node) {
        var jumble = node.getJumble();
        var sb = new StringBuilder();
        sb.append("jumble_").append(jumble.getArgs());
        for (int idx : jumble.getJumble()) {
            sb.append(':').append(idx);
        }
        return sb.toString();
    }
}