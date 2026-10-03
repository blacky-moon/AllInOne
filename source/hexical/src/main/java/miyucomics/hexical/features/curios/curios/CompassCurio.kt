package miyucomics.hexical.features.curios.curios

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.Vec3Iota
import miyucomics.hexical.features.curios.CurioItem
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData
import net.minecraft.server.level.ServerPlayer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand

object CompassCurio : CurioItem() {
    override fun postCharmCast(user: ServerPlayer, item: ItemStack, hand: InteractionHand, world: ServerLevel, stack: List<Iota>) {
        val location = stack.lastOrNull()
        if (location !is Vec3Iota) {
            val customData = item.get(DataComponents.CUSTOM_DATA)
            if (customData != null) {
                val tag = customData.copyTag()
                tag.remove("needle")
                if (tag.isEmpty) {
                    item.remove(DataComponents.CUSTOM_DATA)
                } else {
                    item.set(DataComponents.CUSTOM_DATA, CustomData.of(tag))
                }
            }
            return
        }
        val customData = item.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
        val tag = customData.copyTag()
        tag.putIntArray("needle", intArrayOf(location.vec3.x.toInt(), location.vec3.y.toInt(), location.vec3.z.toInt()))
        item.set(DataComponents.CUSTOM_DATA, CustomData.of(tag))
    }
}