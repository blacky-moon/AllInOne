package io.yukkuric.hexparse.parsers.nbt2str.plugins

import io.yukkuric.hexparse.parsers.nbt2str.INbt2Str
import miyucomics.hexcellular.PropertyIota

object PropertyParser : INbt2Str<PropertyIota> {

    override fun getType(): Class<PropertyIota> = PropertyIota::class.java

    private fun unwrapName(raw: String): String =
        if (raw.startsWith("_")) raw.substring(1) else raw

    override fun parse(node: PropertyIota): String {
        val name = node.name

        return if (name.contains("@")) {
            val sub = name.substring(name.indexOf('@') + 1)
            "myprop_$sub"
        } else {
            "prop_${unwrapName(name)}"
        }
    }
}