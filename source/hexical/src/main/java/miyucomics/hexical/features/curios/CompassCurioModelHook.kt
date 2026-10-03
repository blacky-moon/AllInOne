package miyucomics.hexical.features.curios

import miyucomics.hexical.inits.HexicalItems
import miyucomics.hexical.misc.InitHook
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.core.BlockPos
import net.minecraft.core.component.DataComponents
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import kotlin.math.atan2

object CompassCurioModelHook : InitHook() {
    override fun init() {
        ItemProperties.register(
            HexicalItems.CURIO_COMPASS.get(),
            ResourceLocation.withDefaultNamespace("angle")
        ) { stack, level, entity, seed ->
            if (entity == null || level == null) return@register 0f

            // Читаем данные из стандартного CUSTOM_DATA
            val customData = stack.get(DataComponents.CUSTOM_DATA) ?: return@register 0f
            val tag = customData.copyTag()

            if (!tag.contains("needle")) return@register 0f

            val needle = tag.getIntArray("needle")
            if (needle.size != 3) return@register 0f

            val targetPos = BlockPos(needle[0], needle[1], needle[2])

            // Вычисляем угол до цели
            val entityPos = entity.position()
            val dx = targetPos.x + 0.5 - entityPos.x
            val dz = targetPos.z + 0.5 - entityPos.z

            val yawRad = Math.toRadians(entity.yRot.toDouble())
            val angleToTarget = atan2(dz, dx)

            // Разница между углом к цели и углом поворота игрока
            var angle = angleToTarget - yawRad

            // Нормализуем угол в диапазон [0.0, 1.0] для предиката модели
            angle = Mth.wrapDegrees(Math.toDegrees(angle)) / 360.0 + 0.5

            return@register angle.toFloat()
        }
    }
}