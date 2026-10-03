package miyucomics.hexical.features.magic_missile

import miyucomics.hexical.HexicalMain
import miyucomics.hexical.inits.HexicalDamageTypes
import miyucomics.hexical.inits.HexicalEntities
import net.minecraft.core.particles.ItemParticleOption
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.projectile.AbstractArrow
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.Vec3

class MagicMissileEntity(entityType: EntityType<out MagicMissileEntity>, level: Level) : AbstractArrow(entityType, level) {

    constructor(level: Level) : this(HexicalEntities.MAGIC_MISSILE_ENTITY.get(), level)

    init {
        isNoGravity = true
        pickup = Pickup.DISALLOWED
    }

    companion object {

        const val SHATTER_EVENT_ID: Byte = 64
    }

    override fun handleEntityEvent(status: Byte) {
        if (status == SHATTER_EVENT_ID) {

            level().playSound(null, blockPosition(), SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.NEUTRAL, 0.25f, 1.5f)
            for (i in 0..7) {
                level().addParticle(
                    ItemParticleOption(ParticleTypes.ITEM, ItemStack(Items.AMETHYST_BLOCK)),
                    x, y, z,
                    HexicalMain.RANDOM.nextGaussian() / 20.0,
                    HexicalMain.RANDOM.nextGaussian() / 20.0,
                    HexicalMain.RANDOM.nextGaussian() / 20.0
                )
            }
        } else {
            super.handleEntityEvent(status)
        }
    }

    override fun tick() {
        super.tick()
        if (!level().isClientSide && (inGround || tickCount > 200)) {
            shatter()
        }
    }

    override fun onHitBlock(result: BlockHitResult) {
        super.onHitBlock(result)
        shatter()
    }

    override fun onHitEntity(result: EntityHitResult) {
        val target = result.entity


        val damageSource = level().damageSources().source(HexicalDamageTypes.MAGIC_MISSILE, this, owner)
        target.hurt(damageSource, 2f)

        val motion = deltaMovement
        target.setDeltaMovement(
            Vec3(motion.x, 0.0, motion.z).normalize().scale(0.6).add(0.0, 0.1, 0.0)
        )
        shatter()
    }

    private fun shatter() {
        level().broadcastEntityEvent(this, SHATTER_EVENT_ID)
        discard()
    }

    // В 1.21.1 getPickupItem() больше не абстрактный, он возвращает копию pickupItemStack.
    // Вместо него нужно реализовать getDefaultPickupItem().
    override fun getDefaultPickupItem(): ItemStack = ItemStack.EMPTY

    override fun getWaterInertia(): Float = 1f

    override fun getDefaultHitGroundSoundEvent() = SoundEvents.AMETHYST_BLOCK_BREAK
}