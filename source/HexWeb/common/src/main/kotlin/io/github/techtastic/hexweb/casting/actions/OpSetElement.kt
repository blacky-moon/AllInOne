package io.github.techtastic.hexweb.casting.actions

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.GarbageIota
import at.petrak.hexcasting.api.casting.iota.Iota
import com.google.gson.JsonNull
import io.github.techtastic.hexweb.casting.iota.JsonIota
import io.github.techtastic.hexweb.utils.HexWebOperatorUtils.getJsonObject
import io.github.techtastic.hexweb.utils.HexWebOperatorUtils.toJson
import ram.talia.moreiotas.api.getString

object OpSetElement: ConstMediaAction {
    override val argc: Int
        get() = 3

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val json = args.getJsonObject(0, argc)
        val key = args.getString(1, argc)
        val any = args.getOrNull(2)

        if (any is GarbageIota) {
            json.remove(key)
            return listOf(JsonIota(json))
        }

        json.add(key, any?.toJson() ?: JsonNull.INSTANCE)
        return listOf(JsonIota(json))
    }
}