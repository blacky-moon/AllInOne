package io.yukkuric.hexparse.parsers.nbt2str.plugins

import com.mojang.serialization.Codec
import io.yukkuric.hexparse.parsers.nbt2str.INbt2Str
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import ram.talia.moreiotas.api.casting.iota.MatrixIota
import ram.talia.moreiotas.common.lib.hex.MoreIotasIotaTypes

object MatrixParser : INbt2Str<MatrixIota> {

    override fun getType(): Class<MatrixIota> = MatrixIota::class.java

    override fun parse(node: MatrixIota): String {
        val output = ArrayList<String>()
        output.add("mat")

        val mapCodec = MoreIotasIotaTypes.MATRIX.codec()

        val codec: Codec<MatrixIota> = mapCodec.codec()

        val result = codec.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE, node)

        val tag = result.result().orElseThrow { RuntimeException("Failed to serialize MatrixIota") }

        if (tag is CompoundTag && tag.contains("matrix")) {
            val matrixList = tag.getList("matrix", ListTag.TAG_LIST.toInt())

            val rows = matrixList.size
            if (rows == 0) return "mat_0_0"

            val firstRow = matrixList[0] as ListTag
            val cols = firstRow.size

            output.add(rows.toString())
            output.add(cols.toString())

            for (rowTag in matrixList) {
                val rowList = rowTag as ListTag
                for (numTag in rowList) {
                    val value = when (numTag) {
                        is net.minecraft.nbt.DoubleTag -> numTag.asDouble
                        is net.minecraft.nbt.FloatTag -> numTag.asFloat.toDouble()
                        else -> 0.0
                    }
                    output.add(displayMinimal(value))
                }
            }
        }

        return java.lang.String.join("_", output)
    }
}