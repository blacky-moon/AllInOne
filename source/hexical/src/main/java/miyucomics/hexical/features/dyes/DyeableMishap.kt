package miyucomics.hexical.features.dyes

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.pigment.FrozenPigment
import miyucomics.hexical.HexicalMain
import net.minecraft.network.chat.Component
import net.minecraft.world.item.DyeColor
import net.minecraft.world.phys.Vec3

class DyeableMishap(val position: Vec3) : Mishap() {
    override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment = dyeColor(DyeColor.WHITE)

    override fun particleSpray(ctx: CastingEnvironment) = ParticleSpray.burst(position, 1.0)

    override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context): Component =
        Component.translatable("${HexicalMain.MOD_ID}:can_not_dye")

    override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {}
}