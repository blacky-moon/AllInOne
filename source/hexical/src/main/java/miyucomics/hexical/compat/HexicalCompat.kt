package miyucomics.hexical.compat

import miyucomics.hexical.compat.curios.CuriosCompat
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.neoforged.fml.ModList

object HexicalCompat {
    val curiosLoaded: Boolean by lazy {
        ModList.get().isLoaded("curios")
    }

    fun getGauntletStaff(entity: LivingEntity): ItemStack? {
        if (!curiosLoaded) return null
        return CuriosCompat.getGauntletStaff(entity)
    }

    fun isGauntletStaffEquipped(entity: LivingEntity): Boolean {
        if (!curiosLoaded) return false
        return CuriosCompat.isGauntletStaffEquipped(entity)
    }
}