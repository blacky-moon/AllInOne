package miyucomics.hexical.features.lamps

import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.utils.putCompound
import at.petrak.hexcasting.api.utils.serializeToNBT
import at.petrak.hexcasting.common.items.magic.ItemPackagedHex
import miyucomics.hexical.inits.HexicalSounds
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.NbtOps
import net.minecraft.resources.RegistryOps
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.UseAnim
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.level.Level

object HandLampItem : ItemPackagedHex(Properties().stacksTo(1).rarity(Rarity.RARE)) {
    override fun use(level: Level, user: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {
        val stack = user.getItemInHand(hand)
        if (!hasHex(stack)) return InteractionResultHolder.fail(stack)
        level.playLocalSound(user.x, user.y, user.z,
            HexicalSounds.LAMP_ACTIVATE.get(), SoundSource.MASTER, 1f, 1f, true)
        if (!level.isClientSide) {
            CustomData.update(DataComponents.CUSTOM_DATA, stack) { tag ->
                tag.putCompound("position", user.eyePosition.serializeToNBT())
                tag.putCompound("rotation", user.lookAngle.serializeToNBT())
                tag.putCompound("velocity", user.deltaMovement.serializeToNBT())
                tag.put("storage", IotaType.TYPED_CODEC.encodeStart(RegistryOps.create(NbtOps.INSTANCE, level.registryAccess()), NullIota()).getOrThrow())
                tag.putLong("start_time", level.gameTime)
            }
        }
        user.startUsingItem(hand)
        return InteractionResultHolder.success(stack)
    }

    override fun onUseTick(level: Level, user: LivingEntity, stack: ItemStack, remainingUseDuration: Int) {
        if (level.isClientSide) return
        if (getMedia(stack) == 0L) return
        val vm = CastingVM(CastingImage(), LampCastEnv(user as ServerPlayer, user.usedItemHand, false, stack))
        vm.queueExecuteAndWrapIotas((stack.item as HandLampItem).getHex(stack, level as ServerLevel)!!, level)
    }

    override fun releaseUsing(stack: ItemStack, level: Level, user: LivingEntity, remainingUseTicks: Int) {
        if (!level.isClientSide) {
            val vm = CastingVM(CastingImage(), LampCastEnv(user as ServerPlayer, user.usedItemHand, true, stack))
            vm.queueExecuteAndWrapIotas((stack.item as HandLampItem).getHex(stack, level as ServerLevel)!!, level)
        }
        level.playLocalSound(user.x, user.y, user.z,
            HexicalSounds.LAMP_DEACTIVATE.get(), SoundSource.MASTER, 1f, 1f, true)
    }

    override fun getUseDuration(stack: ItemStack, entity: LivingEntity) = Int.MAX_VALUE
    override fun canDrawMediaFromInventory(stack: ItemStack) = false
    override fun getUseAnimation(stack: ItemStack) = UseAnim.BOW
    override fun canRecharge(stack: ItemStack) = false
    override fun breakAfterDepletion() = false
    override fun cooldown() = 0
}