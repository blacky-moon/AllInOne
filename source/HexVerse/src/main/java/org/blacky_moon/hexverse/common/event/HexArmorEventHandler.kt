package org.blacky_moon.hexverse.common.event

import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM
import at.petrak.hexcasting.api.casting.iota.*
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.xplat.IXplatAbstractions
import miyucomics.hexpose.iotas.IdentifierIota
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.Vec3
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent
import net.neoforged.neoforge.event.entity.living.LivingFallEvent
import net.neoforged.neoforge.event.level.BlockEvent
import org.blacky_moon.hexverse.HexVerseMain
import org.blacky_moon.hexverse.common.item.HexArmorItem
import org.blacky_moon.hexverse.common.item.HexPickaxeItem
import org.blacky_moon.hexverse.common.item.HexSwordItem

@EventBusSubscriber(modid = HexVerseMain.MOD_ID)
class HexArmorEventHandler {
    companion object {


        private const val LEGGINGS_MAX_MEDIA = 70L * MediaConstants.DUST_UNIT
        @SubscribeEvent
        @JvmStatic
        fun onPlayerDamage(event: LivingDamageEvent.Post) {
            val player = event.entity as? ServerPlayer ?: return
            if (player.level().isClientSide) return

            val chestplate = player.getItemBySlot(EquipmentSlot.CHEST)
                .takeIf { it.item is HexArmorItem } ?: return

            castFromItem(chestplate, player) {
                buildDamageStack(event.newDamage, event.source)
            }
        }
        @SubscribeEvent
        @JvmStatic
        fun onSwordHit(event: LivingDamageEvent.Post) {
            val attacker = event.source.entity as? ServerPlayer ?: return
            if (attacker.level().isClientSide) return

            val weapon = attacker.mainHandItem
                .takeIf { it.item is HexSwordItem } ?: return

            val victim = event.entity

            castFromItem(weapon, attacker) {
                listOf(EntityIota(victim))
            }
        }

        @SubscribeEvent
        @JvmStatic
        fun onPickaxeBreak(event: BlockEvent.BreakEvent) {
            val player = event.player as? ServerPlayer ?: return
            if (player.level().isClientSide) return
            val tool = player.mainHandItem
                .takeIf { it.item is HexPickaxeItem } ?: return
            val pos = event.pos
            val blockState = event.state
            val blockId = BuiltInRegistries.BLOCK.getKey(blockState.block)

            castFromItem(tool, player) {
                listOf(
                    Vec3Iota(Vec3(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())),
                    IdentifierIota(blockId)
                )
            }
        }




        @SubscribeEvent
        @JvmStatic
        fun onPlayerDeath(event: LivingDeathEvent) {
            val player = event.entity as? ServerPlayer ?: return
            if (player.level().isClientSide) return

            val leggings = player.getItemBySlot(EquipmentSlot.LEGS)
                .takeIf { it.item is HexArmorItem } ?: return

            castFromItem(leggings, player, mediaCap = LEGGINGS_MAX_MEDIA) {
                listOf()
            }
        }


        @SubscribeEvent
        @JvmStatic
        fun onPlayerFall(event: LivingFallEvent) {
            val player = event.entity as? ServerPlayer ?: return
            if (player.level().isClientSide) return

            val boots = player.getItemBySlot(EquipmentSlot.FEET)
                .takeIf { it.item is HexArmorItem } ?: return

            val distance = event.distance
            if(distance < 1.8) return
            castFromItem(boots, player) {
                listOf(DoubleIota(distance.toDouble()))
            }
        }


        private inline fun castFromItem(
            stack: ItemStack,
            caster: ServerPlayer,
            mediaCap: Long? = null,
            stackBuilder: () -> List<Iota>
        ) {
            val hexHolder = IXplatAbstractions.INSTANCE.findHexHolder(stack) ?: return
            if (!hexHolder.hasHex()) return

            val spell = hexHolder.getHex(caster.serverLevel()) ?: return
            if (spell.isEmpty()) return

            try {
                val initialStack = stackBuilder()
                val hand = caster.determineCastingHand()
                val env = caster.createCastingEnv(hand, hexHolder.pigment, mediaCap)

                CastingVM(
                    CastingImage(
                        initialStack, 0, ArrayList(),
                        escapeNext = false,
                        simulateNext = false,
                        opsConsumed = 0L,
                        userData = net.minecraft.nbt.CompoundTag()
                    ),
                    env
                ).queueExecuteAndWrapIotas(spell, caster.serverLevel())

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        private fun buildDamageStack(damage: Float, source: DamageSource): List<Iota> {
            return listOf(
                DoubleIota(damage.toDouble()),
                (source.directEntity as? LivingEntity)?.let { EntityIota(it) } ?: NullIota(),
                (source.entity as? LivingEntity)?.let { EntityIota(it) }
                    ?: (source.directEntity as? LivingEntity)?.let { EntityIota(it) }
                    ?: NullIota()
            )
        }
        private fun ServerPlayer.determineCastingHand() = when {
            !getItemInHand(InteractionHand.MAIN_HAND).isEmpty &&
                    getItemInHand(InteractionHand.OFF_HAND).isEmpty -> InteractionHand.OFF_HAND
            else -> InteractionHand.MAIN_HAND
        }

        private fun ServerPlayer.createCastingEnv(
            hand: InteractionHand,
            pigment: FrozenPigment?,
            mediaCap: Long?
        ) = object : PlayerBasedCastEnv(this, hand) {
            override fun getCastingHand() = hand
            override fun getPigment() = pigment ?: IXplatAbstractions.INSTANCE.getPigment(this@createCastingEnv)
            override fun produceParticles(p: at.petrak.hexcasting.api.casting.ParticleSpray, pg: FrozenPigment) = Unit
            override fun extractMediaEnvironment(cost: Long, simulate: Boolean): Long {
                if (mediaCap != null && cost > mediaCap) {

                    return cost
                }
                return if (isCreative) 0 else extractMediaFromInventory(cost, true, simulate)
            }
            override fun printMessage(msg: Component) = sendSystemMessage(msg)
            override fun mishapSprayPos() = position()
            override fun isVecInRangeEnvironment(vec: Vec3) = vec.distanceToSqr(position()) <= 256.0
            override fun hasEditPermissionsAtEnvironment(pos: BlockPos) = true
        }
    }
}