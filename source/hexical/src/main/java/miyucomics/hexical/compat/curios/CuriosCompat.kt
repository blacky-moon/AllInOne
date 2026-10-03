package miyucomics.hexical.compat.curios

import miyucomics.hexical.inits.HexicalItems
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import top.theillusivec4.curios.api.CuriosApi

object CuriosCompat {

    fun getGauntletStaff(entity: LivingEntity): ItemStack? {
        val inventory = CuriosApi.getCuriosInventory(entity).orElse(null) ?: return null
        val handler = inventory.getStacksHandler("hands").orElse(null) ?: return null

        for (i in 0 until handler.stacks.slots) {
            val stack = handler.stacks.getStackInSlot(i)
            if (stack.item === HexicalItems.GAUNTLET_STAFF.get()) return stack
        }
        return null
    }

    fun isEquipped(entity: LivingEntity, item: Item): Boolean {
        val inventory = CuriosApi.getCuriosInventory(entity).orElse(null) ?: return false
        return inventory.curios.values.any { handler ->
            (0 until handler.stacks.slots).any { i -> handler.stacks.getStackInSlot(i).item === item }
        }
    }

    fun isGauntletStaffEquipped(entity: LivingEntity): Boolean {
        return isEquipped(entity, HexicalItems.GAUNTLET_STAFF.get())
    }
}