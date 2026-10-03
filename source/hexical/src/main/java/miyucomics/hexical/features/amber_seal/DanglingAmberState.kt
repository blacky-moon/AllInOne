package miyucomics.hexical.features.amber_seal

import net.minecraft.world.level.block.entity.BlockEntity

// An incredibly gross class, but also an incredibly useful one
// Stores a block entity so that later on, a mixin can fetch the block entity and set it
object DanglingState {
    @JvmField
    var queuedBlockEntity: BlockEntity? = null
}