package miyucomics.hexical.features.evocation

import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM
import at.petrak.hexcasting.xplat.IXplatAbstractions
import miyucomics.hexical.features.player.types.PlayerTicker
import miyucomics.hexical.inits.HexicalAdvancements
import miyucomics.hexical.inits.HexicalSounds
import miyucomics.hexical.misc.CastingUtils
import miyucomics.hexical.misc.HexSerialization
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundSource
import net.minecraft.util.Mth
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.core.particles.ColorParticleOption

class EvocationTicker : PlayerTicker {
    override fun tick(player: Player) {
        if (player.level().isClientSide && player.evocationActive) {
            val rot = player.yBodyRot * (Mth.PI / 180f) + Mth.cos(player.tickCount.toFloat() * 0.6662f) * 0.25f
            val cos = Mth.cos(rot)
            val sin = Mth.sin(rot)

            val color = IXplatAbstractions.INSTANCE.getPigment(player).colorProvider.getColor((player.level().gameTime * 10).toFloat(), player.position())
            val r = (color shr 16 and 0xFF) / 255f
            val g = (color shr 8 and 0xFF) / 255f
            val b = (color and 0xFF) / 255f
            val particleOption = ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, r, g, b)

            player.level().addParticle(particleOption, player.x + cos * 0.6, player.y + 1.8, player.z + sin * 0.6, 0.0, 0.0, 0.0)
            player.level().addParticle(particleOption, player.x - cos * 0.6, player.y + 1.8, player.z - sin * 0.6, 0.0, 0.0, 0.0)
        }

        if (player.level().isClientSide) return

        if (player.evocationActive) player.evocationDuration -= 1

        if (player.evocationActive && player.evocationDuration == 0 && CastingUtils.isEnlightened(player as ServerPlayer)) {
            player.awardStat(HexicalAdvancements.EVOCATION_STATISTIC)
            player.evocationDuration = ServerEvocationManager.EVOKE_DURATION

            val hand = if (!player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty && player.getItemInHand(InteractionHand.OFF_HAND).isEmpty) {
                InteractionHand.OFF_HAND
            } else {
                InteractionHand.MAIN_HAND
            }

            val vm = CastingVM(CastingImage(), EvocationCastEnv(player, hand))
            vm.queueExecuteAndWrapIotas(
                HexSerialization.deserializeHex(player.evocation, player.level().registryAccess()),
                player.level() as ServerLevel
            )

            player.level().playSound(null, player.x, player.y, player.z, HexicalSounds.MAGIC_WOOSHES.get(), SoundSource.PLAYERS, 1f, 1f)
        }
    }
}