package io.yukkuric.hexparse.parsers.nbt2str.plugins

import io.yukkuric.hexparse.misc.StringEscaper
import io.yukkuric.hexparse.parsers.nbt2str.INbt2Str
import ram.talia.moreiotas.api.casting.iota.StringIota

class StringLitParser : INbt2Str<StringIota> {

    override fun getType(): Class<StringIota> = StringIota::class.java

    override fun parse(node: StringIota): String {
        var data = node.string
        data = StringEscaper.escape(data)
        return "\"$data\""
    }
}