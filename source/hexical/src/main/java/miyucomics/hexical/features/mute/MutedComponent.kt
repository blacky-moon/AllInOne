package miyucomics.hexical.features.mute

import net.minecraft.world.entity.Entity

class MutedComponent(private val entity: Entity) {
    var muted: Boolean
        get() = entity.persistentData.getBoolean("hexical:muted")
        set(value) {
            entity.persistentData.putBoolean("hexical:muted", value)
        }

    fun toggleMute() {
        muted = !muted

    }
}