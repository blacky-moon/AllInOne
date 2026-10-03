package miyucomics.hexical.features.grok

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import at.petrak.hexcasting.api.casting.mishaps.MishapOthersName
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand

object OpGrokGetStack : ConstMediaAction {
    override val argc = 0
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        if (env !is PlayerBasedCastEnv)
            throw MishapBadCaster()
        val player = env.castingEntity as ServerPlayer
        return IXplatAbstractions.INSTANCE.getStaffcastVM(player, InteractionHand.MAIN_HAND).image.stack.map { if (MishapOthersName.getTrueNameFromDatum(env.world,it, player) == null) it else NullIota() }.asActionResult
    }
}