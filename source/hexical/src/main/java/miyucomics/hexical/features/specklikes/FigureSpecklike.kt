package miyucomics.hexical.features.specklikes

import at.petrak.hexcasting.api.pigment.FrozenPigment
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level

abstract class FigureSpecklike(
    entityType: EntityType<out FigureSpecklike>,
    level: Level
) : BaseSpecklike(entityType, level) {

    var clientPigment: FrozenPigment = FrozenPigment.DEFAULT.get()
    var clientThickness = 1f

    fun setThickness(thickness: Float) = entityData.set(THICKNESS_DATA_TRACKER, thickness)

    fun setPigment(pigment: FrozenPigment) {
        val ops = registryAccess().createSerializationContext(NbtOps.INSTANCE)
        val result = FrozenPigment.CODEC.encodeStart(ops, pigment)
            .resultOrPartial { error ->
                // Опционально: логирование ошибки сериализации
            }
            .orElse(null)
        val tag = result as? CompoundTag ?: CompoundTag()

        entityData.set(PIGMENT_DATA_TRACKER, tag)
    }
    override fun readAdditionalSaveData(nbt: CompoundTag) {
        super.readAdditionalSaveData(nbt)
        if (nbt.contains("shape")) {
            entityData.set(SHAPE_DATA_TRACKER, nbt.getCompound("shape"))
        }
        if (nbt.contains("pigment")) {
            entityData.set(PIGMENT_DATA_TRACKER, nbt.getCompound("pigment"))
        }
        if (nbt.contains("thickness")) {
            entityData.set(THICKNESS_DATA_TRACKER, nbt.getFloat("thickness"))
        }
    }

    override fun addAdditionalSaveData(nbt: CompoundTag) {
        super.addAdditionalSaveData(nbt)
        nbt.put("shape", entityData.get(SHAPE_DATA_TRACKER))
        nbt.put("pigment", entityData.get(PIGMENT_DATA_TRACKER))
        nbt.putFloat("thickness", entityData.get(THICKNESS_DATA_TRACKER))
    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        super.defineSynchedData(builder)
        builder.define(SHAPE_DATA_TRACKER, CompoundTag())
        builder.define(PIGMENT_DATA_TRACKER, CompoundTag())
        builder.define(THICKNESS_DATA_TRACKER, 1f)
    }

    override fun onSyncedDataUpdated(key: EntityDataAccessor<*>) {
        when (key) {
            SHAPE_DATA_TRACKER -> processShape(entityData.get(SHAPE_DATA_TRACKER))
            PIGMENT_DATA_TRACKER -> {
                // Десериализуем через CODEC вместо удалённого fromNBT()
                this.clientPigment = FrozenPigment.CODEC.parse(
                    registryAccess().createSerializationContext(NbtOps.INSTANCE),
                    entityData.get(PIGMENT_DATA_TRACKER)
                ).resultOrPartial { /* log error */ }
                    .orElse(FrozenPigment.DEFAULT.get())
            }
            THICKNESS_DATA_TRACKER -> this.clientThickness = entityData.get(THICKNESS_DATA_TRACKER)
            else -> super.onSyncedDataUpdated(key)
        }
    }

    abstract fun processShape(shape: CompoundTag)

    companion object {
        val SHAPE_DATA_TRACKER: EntityDataAccessor<CompoundTag> =
            SynchedEntityData.defineId(FigureSpecklike::class.java, EntityDataSerializers.COMPOUND_TAG)

        private val PIGMENT_DATA_TRACKER: EntityDataAccessor<CompoundTag> =
            SynchedEntityData.defineId(FigureSpecklike::class.java, EntityDataSerializers.COMPOUND_TAG)

        private val THICKNESS_DATA_TRACKER: EntityDataAccessor<Float> =
            SynchedEntityData.defineId(FigureSpecklike::class.java, EntityDataSerializers.FLOAT)
    }
}