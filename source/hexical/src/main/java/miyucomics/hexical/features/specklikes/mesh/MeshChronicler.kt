package miyucomics.hexical.features.specklikes.mesh

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.iota.Vec3Iota
import miyucomics.hexical.misc.SimpleEntityIotaHolder
import net.minecraft.world.phys.Vec3
import org.joml.Vector3f

class MeshChronicler(val mesh: MeshEntity) : SimpleEntityIotaHolder() {

    override fun writeable(): Boolean = true

    // В 1.21.1 ADIotaHolder требует readIota(), а не readIotaTag()
    override fun readIota(): Iota = ListIota(mesh.getShape())

    override fun writeIota(design: Iota?, simulate: Boolean): Boolean {
        if (design !is ListIota) return false
        if (design.list.size() > 32) return false

        val points = design.list.map {
            if (it !is Vec3Iota) return false
            val vector = it.vec3
            if (vector.lengthSqr() > 100.0) return false
            Vector3f(vector.x.toFloat(), vector.y.toFloat(), vector.z.toFloat())
        }

        if (!simulate) {
            mesh.setShape(points)
        }
        return true
    }
}