package miyucomics.hexical.features.specklikes

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerEntity
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityDimensions
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Pose
import net.minecraft.world.level.Level

abstract class BaseSpecklike(entityType: EntityType<out BaseSpecklike>, level: Level) : Entity(entityType, level) {
    private var lifespan = -1

    var clientSize = 1f
    var clientRoll = 0f

    override fun tick() {
        if (lifespan != -1) {
            lifespan--
        }
        if (lifespan == 0) {
            discard()
        }
        super.tick()
    }

    override fun readAdditionalSaveData(nbt: CompoundTag) {
        entityData.set(ROLL_DATA_TRACKER, nbt.getFloat("roll"))
        entityData.set(SIZE_DATA_TRACKER, nbt.getFloat("size"))
        this.lifespan = nbt.getInt("lifespan")
    }

    override fun addAdditionalSaveData(nbt: CompoundTag) {
        nbt.putFloat("roll", entityData.get(ROLL_DATA_TRACKER))
        nbt.putFloat("size", entityData.get(SIZE_DATA_TRACKER))
        nbt.putInt("lifespan", lifespan)
    }

    fun setLifespan(lifespan: Int) {
        this.lifespan = lifespan
    }

    fun setSize(size: Float) = entityData.set(SIZE_DATA_TRACKER, size)

    fun setRoll(rotation: Float) = entityData.set(ROLL_DATA_TRACKER, rotation)

    // В 1.21.1 getEyeHeight стал final, поэтому мы переопределяем getDimensions
    override fun getDimensions(pose: Pose): EntityDimensions {
        return super.getDimensions(pose).withEyeHeight(0.25f)
    }

    override fun getAddEntityPacket(serverEntity: ServerEntity): Packet<ClientGamePacketListener> {
        return ClientboundAddEntityPacket(this, serverEntity)
    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        builder.define(ROLL_DATA_TRACKER, 0f)
        builder.define(SIZE_DATA_TRACKER, 1f)
    }

    override fun onSyncedDataUpdated(key: EntityDataAccessor<*>) {
        when (key) {
            SIZE_DATA_TRACKER -> this.clientSize = entityData.get(SIZE_DATA_TRACKER)
            ROLL_DATA_TRACKER -> this.clientRoll = entityData.get(ROLL_DATA_TRACKER)
            else -> super.onSyncedDataUpdated(key)
        }
    }

    companion object {
        private val SIZE_DATA_TRACKER: EntityDataAccessor<Float> = SynchedEntityData.defineId(BaseSpecklike::class.java, EntityDataSerializers.FLOAT)
        private val ROLL_DATA_TRACKER: EntityDataAccessor<Float> = SynchedEntityData.defineId(BaseSpecklike::class.java, EntityDataSerializers.FLOAT)
    }
}