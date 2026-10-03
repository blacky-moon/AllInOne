package miyucomics.hexical.features.prestidigitation.handlers

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import miyucomics.hexical.features.prestidigitation.interfaces.PrestidigitationHandler
import miyucomics.hexical.features.prestidigitation.interfaces.PrestidigitationHandlerEntity
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Shearable
import net.minecraft.world.entity.animal.Fox
import net.minecraft.world.entity.animal.Panda
import net.minecraft.world.entity.animal.Pufferfish
import net.minecraft.world.entity.animal.Squid
import net.minecraft.world.entity.animal.horse.Llama
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.entity.item.PrimedTnt
import net.minecraft.world.entity.monster.Creeper
import net.minecraft.world.entity.monster.EnderMan
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Blocks

object PrestidigitationHandlersEntity {
    // Рефлексия для приватного поля puffState (Pufferfish)
    private val pufferfishPuffStateField by lazy {
        Pufferfish::class.java.getDeclaredField("puffState").apply { isAccessible = true }
    }

    // Рефлексия для приватного метода spawnInk() (Squid)
    private val squidSpawnInkMethod by lazy {
        Squid::class.java.getDeclaredMethod("spawnInk").apply { isAccessible = true }
    }

    // Рефлексия для приватного метода spit(ItemStack) (Fox)
    private val foxSpitMethod by lazy {
        Fox::class.java.getDeclaredMethod("spit", ItemStack::class.java).apply { isAccessible = true }
    }

    // Рефлексия для приватного метода spit(LivingEntity) (Llama)
    private val llamaSpitMethod by lazy {
        Llama::class.java.getDeclaredMethod("spit", LivingEntity::class.java).apply { isAccessible = true }
    }

    fun init(register: (PrestidigitationHandler) -> Unit) {
        // Armor stand toggle arms
        register(PrestidigitationHandlerEntity.simple(ArmorStand::class.java) { stand ->
            stand.isShowArms = !stand.isShowArms
            stand.playSound(SoundEvents.ARMOR_STAND_PLACE, 1f, 1f)
        })

        // TNT entity → TNT block
        register(PrestidigitationHandlerEntity.simple(PrimedTnt::class.java) { tnt ->
            if (tnt.level().getBlockState(tnt.blockPosition()).canBeReplaced()) {
                tnt.level().setBlock(tnt.blockPosition(), Blocks.TNT.defaultBlockState(), 3)
            }
            tnt.discard()
        })

        // Creeper ignite/stop
        register(PrestidigitationHandlerEntity.simple(Creeper::class.java) { creeper ->
            if (creeper.isIgnited) {
                creeper.ignite()
            } else {
                creeper.ignite()
            }
        })

        // Pufferfish inflate (через рефлексию, т.к. puffState теперь val)
        register(PrestidigitationHandlerEntity.simple(Pufferfish::class.java) { pufferfish ->
            if (pufferfish.puffState != 2) {
                pufferfish.playSound(SoundEvents.PUFFER_FISH_BLOW_UP, 1f, 1f)
                pufferfishPuffStateField.setInt(pufferfish, 2)
            }
        })

        // Panda sneeze
        register(PrestidigitationHandlerEntity.simple(Panda::class.java) { panda ->
            panda.sneeze(true)
        })

        // Squid ink (через приватный spawnInk)
        register(PrestidigitationHandlerEntity.simple(Squid::class.java) { squid ->
            squidSpawnInkMethod.invoke(squid)
        })

        // Enderman target
        register(object : PrestidigitationHandlerEntity<EnderMan>(EnderMan::class.java) {
            override fun affect(env: CastingEnvironment, entity: EnderMan) {
                entity.target = env.castingEntity as? LivingEntity
            }
        })

        // Fox spit (приватный метод spit)
        register(object : PrestidigitationHandlerEntity<Fox>(Fox::class.java) {
            override fun affect(env: CastingEnvironment, entity: Fox) {
                val item = entity.getItemBySlot(EquipmentSlot.MAINHAND)
                foxSpitMethod.invoke(entity, item)
                entity.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY)
            }
        })

        // Llama spit (приватный метод spit)
        register(object : PrestidigitationHandlerEntity<Llama>(Llama::class.java) {
            override fun affect(env: CastingEnvironment, entity: Llama) {
                val target = env.castingEntity ?: return
                llamaSpitMethod.invoke(entity, target)
            }
        })

        // Shearable entities
        register(object : PrestidigitationHandlerEntity<Shearable>(Shearable::class.java) {
            override fun canAffectEntity(env: CastingEnvironment, entity: Entity): Boolean {
                return super.canAffectEntity(env, entity) && (entity as Shearable).readyForShearing()
            }
            override fun affect(env: CastingEnvironment, entity: Shearable) {
                entity.shear(SoundSource.MASTER)
            }
        })
    }
}