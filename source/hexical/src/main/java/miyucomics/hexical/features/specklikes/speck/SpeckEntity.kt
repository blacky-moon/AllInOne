package miyucomics.hexical.features.specklikes.speck

import miyucomics.hexical.features.specklikes.BaseSpecklike
import miyucomics.hexical.inits.HexicalEntities
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level

class SpeckEntity(entityType: EntityType<out SpeckEntity>, level: Level) : BaseSpecklike(entityType, level) {

    constructor(level: Level) : this(HexicalEntities.SPECK_ENTITY.get(), level)

    // В 1.21.1 HolderLookup.Provider больше не передаётся как параметр.
    // Используем this.registryAccess() внутри метода.
    override fun readAdditionalSaveData(nbt: CompoundTag) {
        super.readAdditionalSaveData(nbt)
        val text = if (nbt.contains("text")) {
            Component.Serializer.fromJson(nbt.getString("text"), registryAccess()) ?: Component.empty()
        } else {
            Component.empty()
        }
        entityData.set(TEXT_DATA_TRACKER, text)
    }

    override fun addAdditionalSaveData(nbt: CompoundTag) {
        super.addAdditionalSaveData(nbt)
        nbt.putString("text", Component.Serializer.toJson(entityData.get(TEXT_DATA_TRACKER), registryAccess()))
    }

    fun setText(text: Component) {
        entityData.set(TEXT_DATA_TRACKER, text)
    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        super.defineSynchedData(builder)
        builder.define(TEXT_DATA_TRACKER, Component.empty())
    }

    companion object {
        val TEXT_DATA_TRACKER: EntityDataAccessor<Component> =
            SynchedEntityData.defineId(SpeckEntity::class.java, EntityDataSerializers.COMPONENT)
    }
}