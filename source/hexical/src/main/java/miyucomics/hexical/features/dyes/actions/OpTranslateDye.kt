package miyucomics.hexical.features.dyes.actions

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import miyucomics.hexical.features.dyes.getColoredDye
import net.minecraft.world.phys.Vec3

object OpTranslateDye : ConstMediaAction {
    override val argc = 1

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val dye = args.getColoredDye(0, argc)

        // В 1.21.1 textureDiffuseColors удален, используем getTextureDiffuseColor() и распаковываем Int
        val color = dye.textureDiffuseColor
        val r = (color shr 16 and 0xFF) / 255.0
        val g = (color shr 8 and 0xFF) / 255.0
        val b = (color and 0xFF) / 255.0

        return Vec3(r, g, b).asActionResult
    }
}