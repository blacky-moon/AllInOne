package miyucomics.hexical.features.lamps


import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag

class OpGetLampData(private val process: (CastingEnvironment, CompoundTag) -> List<Iota>) : ConstMediaAction {
    override val argc = 0
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        if (env !is LampCastEnv)
            throw NeedsLampMishap()
        val tag = env.castingEntity!!.useItem.get(DataComponents.CUSTOM_DATA)?.copyTag() ?: return listOf(NullIota())
        return process(env, tag)
    }
}