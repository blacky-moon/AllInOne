package miyucomics.hexical.features.wristpocket

import miyucomics.hexical.features.player.getHexicalPlayerManager
import miyucomics.hexical.features.player.types.PlayerField
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

class WristpocketField : PlayerField {
    var wristpocket: ItemStack = ItemStack.EMPTY

    override fun readNbt(compound: CompoundTag, registries: HolderLookup.Provider) {
        if (!compound.contains("wristpocket"))
            return

        wristpocket = ItemStack.parseOptional(registries, compound.getCompound("wristpocket"))
    }

    override fun writeNbt(compound: CompoundTag, registries: HolderLookup.Provider) {
        if (!wristpocket.isEmpty) {
            compound.put("wristpocket", wristpocket.save(registries))
        }
    }

    override fun handleRespawn(new: Player, old: Player) {
        new.wristpocket = old.wristpocket
    }
}

var Player.wristpocket: ItemStack
    get() = this.getHexicalPlayerManager().get(WristpocketField::class).wristpocket
    set(stack) {
        this.getHexicalPlayerManager().get(WristpocketField::class).wristpocket = stack
    }