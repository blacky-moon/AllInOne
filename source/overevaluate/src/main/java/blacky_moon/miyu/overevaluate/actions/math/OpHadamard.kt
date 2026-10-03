package blacky_moon.miyu.overevaluate.actions.math
import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.Iota
import net.minecraft.core.Vec3i
import net.minecraft.world.phys.Vec3
object OpHadamard : ConstMediaAction {
    override val argc = 2
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val a = args.getVec3(0, argc)
        val b = args.getVec3(1, argc)
        return Vec3(a.x * b.x, a.y * b.y, a.z * b.z).asActionResult
    }
}