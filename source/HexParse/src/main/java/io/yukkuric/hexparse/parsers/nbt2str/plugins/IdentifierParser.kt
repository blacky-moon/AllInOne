package io.yukkuric.hexparse.parsers.nbt2str.plugins

import io.yukkuric.hexparse.parsers.nbt2str.INbt2Str
import miyucomics.hexpose.iotas.IdentifierIota

object IdentifierParser : INbt2Str<IdentifierIota> {

    override fun getType(): Class<IdentifierIota> = IdentifierIota::class.java


    override fun parse(node: IdentifierIota): String {
        val id = node.identifier
        return "id_" + if (id.namespace == "minecraft") {
            id.path
        } else {
            "${id.namespace}:${id.path}"
        }
    }
}