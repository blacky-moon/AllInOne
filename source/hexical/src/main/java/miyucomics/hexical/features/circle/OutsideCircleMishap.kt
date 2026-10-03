package miyucomics.hexical.features.circle

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.pigment.FrozenPigment
import miyucomics.hexical.HexicalMain
import net.minecraft.network.chat.Component
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.item.DyeColor

class OutsideCircleMishap : Mishap() {
    override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment =
        dyeColor(DyeColor.GREEN)

    override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context): Component =
        Component.translatable("${HexicalMain.MOD_ID}:outside_circle")

    override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
        val caster = env.castingEntity ?: return
        caster.addEffect(MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60))
    }
}