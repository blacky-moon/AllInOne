package miyucomics.hexical.features.specklikes.strand

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.iota.Vec3Iota
import miyucomics.hexical.misc.SimpleEntityIotaHolder
import net.minecraft.world.phys.Vec2
import kotlin.math.pow

class StrandChronicler(val strand: StrandEntity) : SimpleEntityIotaHolder() {

    override fun writeable(): Boolean = true

    // В 1.21.1 ADIotaHolder требует readIota(), а не readIotaTag()
    override fun readIota(): Iota = ListIota(strand.getShape())

    override fun writeIota(design: Iota?, simulate: Boolean): Boolean {
        if (design !is ListIota) return false
        if (design.list.size() > 32) return false

        val points = design.list.map {
            if (it !is Vec3Iota) return false
            val vector = it.vec3
            if (vector.x.pow(2) + vector.y.pow(2) > 100) return false
            Vec2(vector.x.toFloat(), vector.y.toFloat())
        }

        if (!simulate) {
            strand.setShape(points)
        }
        return true
    }
}