package miyucomics.hexical.features.evocation

import miyucomics.hexical.features.player.getHexicalPlayerManager
import miyucomics.hexical.features.player.types.PlayerField
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.world.entity.player.Player

class EvocationField : PlayerField {
    var active: Boolean = false
    var duration: Int = -1
    var evocation: ListTag = ListTag()

    // ИСПРАВЛЕНО: Добавлен параметр registries для соответствия интерфейсу PlayerField
    override fun readNbt(compound: CompoundTag, registries: HolderLookup.Provider) {
        if (compound.contains("evocation_hex")) {
            this.evocation = compound.getList("evocation_hex", Tag.TAG_COMPOUND.toInt())
        }
        if (compound.contains("evocation_active")) {
            this.active = compound.getBoolean("evocation_active")
        }
        if (compound.contains("evocation_duration")) {
            this.duration = compound.getInt("evocation_duration")
        }
    }

    // ИСПРАВЛЕНО: Добавлен параметр registries
    override fun writeNbt(compound: CompoundTag, registries: HolderLookup.Provider) {
        compound.put("evocation_hex", evocation)
        compound.putBoolean("evocation_active", active)
        compound.putInt("evocation_duration", duration)
    }

    override fun handleRespawn(new: Player, old: Player) {
        new.evocation = old.evocation
        new.evocationActive = old.evocationActive
        new.evocationDuration = old.evocationDuration
    }
}

var Player.evocationActive: Boolean
    get() = this.getHexicalPlayerManager().get(EvocationField::class).active
    set(active) { this.getHexicalPlayerManager().get(EvocationField::class).active = active }

var Player.evocationDuration: Int
    get() = this.getHexicalPlayerManager().get(EvocationField::class).duration
    set(duration) { this.getHexicalPlayerManager().get(EvocationField::class).duration = duration }

var Player.evocation: ListTag
    get() = this.getHexicalPlayerManager().get(EvocationField::class).evocation
    set(hex) { this.getHexicalPlayerManager().get(EvocationField::class).evocation = hex }