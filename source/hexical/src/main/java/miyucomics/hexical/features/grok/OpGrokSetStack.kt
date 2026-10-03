package miyucomics.hexical.features.grok

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.getList
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster
import at.petrak.hexcasting.api.casting.mishaps.MishapOthersName
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand

object OpGrokSetParenthesized : SpellAction {
    override val argc = 1
    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        if (env.castingEntity !is ServerPlayer)
            throw MishapBadCaster()
        val new = args.getList(0, argc).map { if (MishapOthersName.getTrueNameFromDatum(env.world,it, env.castingEntity as ServerPlayer) == null) it else NullIota() }
        return SpellAction.Result(Spell { image -> image.copy(parenthesized = new.map { CastingImage.ParenthesizedIota(it, true) }) }, 0, listOf())
    }

    data class Spell(val modify: (CastingImage) -> CastingImage) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            IXplatAbstractions.INSTANCE.setStaffcastImage(
                env.castingEntity as ServerPlayer,
                modify(IXplatAbstractions.INSTANCE.getStaffcastVM(env.castingEntity as ServerPlayer, InteractionHand.MAIN_HAND).image)
            )
        }
    }
}