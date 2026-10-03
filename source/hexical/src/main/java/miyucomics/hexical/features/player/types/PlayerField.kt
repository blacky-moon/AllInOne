package miyucomics.hexical.features.player.types

import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.player.Player

interface PlayerField {
    // В 1.20.5+ для работы с NBT (особенно ItemStack) нужен доступ к реестрам
    fun readNbt(compound: CompoundTag, registries: HolderLookup.Provider) {}
    fun writeNbt(compound: CompoundTag, registries: HolderLookup.Provider) {}
    fun handleRespawn(new: Player, old: Player) {}
}