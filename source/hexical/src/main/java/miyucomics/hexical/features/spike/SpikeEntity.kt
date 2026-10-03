package miyucomics.hexical.features.spike

import miyucomics.hexical.HexicalMain
import miyucomics.hexical.inits.HexicalDamageTypes
import miyucomics.hexical.inits.HexicalEntities
import net.minecraft.core.Direction
import net.minecraft.core.particles.ItemParticleOption
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import java.util.*
import kotlin.math.pow

@OptIn(ExperimentalStdlibApi::class)
class SpikeEntity(
    entityType: EntityType<out SpikeEntity>,
    level: Level
) : Entity(entityType, level) {

    private var timer = 0
    private var conjurerUUID: UUID? = null
    private var conjurer: Player? = null

    constructor(level: Level, x: Double, y: Double, z: Double, direction: Direction, delay: Int) : this(
        HexicalEntities.SPIKE_ENTITY.value(),
        level
    ) {
        this.setDirection(direction)
        this.setPos(x, y, z)
        this.timer = -delay
        // Доступ к компаньону через имя класса или прямое обращение, если они в одной области видимости
        this.entityData.set(TIMER_DATA_TRACKER, this.timer)
    }

    override fun tick() {
        this.timer += 1
        if (this.timer == EMERGE_LENGTH) {
            this.playSound(SoundEvents.GLASS_BREAK, 1f, 1f)
            val entities = level().getEntitiesOfClass(
                LivingEntity::class.java,
                this.boundingBox
            ) { it != this }

            for (livingEntity in entities) {
                this.damageTarget(livingEntity)
            }
        }

        if (this.timer > EMERGE_LENGTH + STAY_LENGTH + DISAPPEAR_LENGTH && !level().isClientSide) {
            if (level() is ServerLevel) {
                val serverLevel = level() as ServerLevel
                serverLevel.sendParticles(
                    ItemParticleOption(ParticleTypes.ITEM, ItemStack(Items.AMETHYST_BLOCK, 1)),
                    this.x, this.y, this.z,
                    8,
                    HexicalMain.RANDOM.nextGaussian() / 20.0,
                    HexicalMain.RANDOM.nextGaussian() / 20.0,
                    HexicalMain.RANDOM.nextGaussian() / 20.0,
                    HexicalMain.RANDOM.nextGaussian() / 10.0
                )
            }

            level().playSound(
                null,
                this.blockPosition(),
                SoundEvents.AMETHYST_BLOCK_BREAK,
                SoundSource.NEUTRAL,
                0.25f,
                1.5f
            )
            this.discard()
        }
    }

    private fun damageTarget(target: LivingEntity) {
        val dirId = this.entityData.get(DIRECTION_DATA_TRACKER)
        val direction = Direction.entries[dirId]

        val dirVector = direction.normal
        target.push(dirVector.x.toDouble() * 0.5, 0.5, dirVector.z.toDouble() * 0.5)

        if (!target.isAlive || target.isInvulnerableTo(level().damageSources().magic())) return

        val owner = this.getConjurer()

        // В 1.21.1 используется source() вместо create()
        val damageSource = level().damageSources().source(
            HexicalDamageTypes.SPIKE,
            this,
            owner
        )

        target.hurt(damageSource, 6f)
    }

    private fun setDirection(direction: Direction) {
        this.entityData.set(DIRECTION_DATA_TRACKER, direction.ordinal)
        if (direction.axis.isHorizontal) {
            this.xRot = 0.0f
            this.yRot = direction.toYRot()
        } else {
            this.xRot = (-90 * direction.stepY).toFloat()
            this.yRot = 0.0f
        }
        this.xRotO = this.xRot
        this.yRotO = this.yRot
    }

    fun getAnimationProgress(): Float {
        if (this.timer < 0) return 0f
        if (this.timer in 0..<EMERGE_LENGTH) return (this.timer / EMERGE_LENGTH.toFloat()).pow(5f)
        if (this.timer in EMERGE_LENGTH..<EMERGE_LENGTH + STAY_LENGTH) return 1f
        return 1f - (this.timer - EMERGE_LENGTH - STAY_LENGTH) / DISAPPEAR_LENGTH.toFloat()
    }

    override fun readAdditionalSaveData(nbt: CompoundTag) {
        this.timer = nbt.getInt("timer")
        if (nbt.hasUUID("conjurer")) {
            this.conjurerUUID = nbt.getUUID("conjurer")
        } else {
            this.conjurer = null
            this.conjurerUUID = null
        }
        this.entityData.set(TIMER_DATA_TRACKER, this.timer)
        val dirId = nbt.getInt("direction")
        this.setDirection(Direction.entries[dirId])
    }

    override fun addAdditionalSaveData(nbt: CompoundTag) {
        nbt.putInt("timer", this.timer)
        if (this.conjurerUUID != null) {
            nbt.putUUID("conjurer", this.conjurerUUID!!)
        }
        nbt.putInt("direction", this.entityData.get(DIRECTION_DATA_TRACKER))
    }

    fun setConjurer(player: Player) {
        this.conjurer = player
        this.conjurerUUID = player.uuid
    }

    private fun getConjurer(): Player? {
        if (this.conjurer != null) return this.conjurer
        if (this.conjurerUUID == null) return null

        this.conjurer = level().getPlayerByUUID(conjurerUUID!!)
        return this.conjurer
    }

    fun getSpikeDirection(): Direction {
        val id = this.entityData.get(DIRECTION_DATA_TRACKER)
        return Direction.entries[id]
    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        builder.define(DIRECTION_DATA_TRACKER, 0)
        builder.define(TIMER_DATA_TRACKER, 0)
    }

    companion object {
        private const val EMERGE_LENGTH = 10
        private const val STAY_LENGTH = 10
        private const val DISAPPEAR_LENGTH = 20

        private val DIRECTION_DATA_TRACKER: EntityDataAccessor<Int> =
            SynchedEntityData.defineId(SpikeEntity::class.java, EntityDataSerializers.INT)

        private val TIMER_DATA_TRACKER: EntityDataAccessor<Int> =
            SynchedEntityData.defineId(SpikeEntity::class.java, EntityDataSerializers.INT)
    }
}