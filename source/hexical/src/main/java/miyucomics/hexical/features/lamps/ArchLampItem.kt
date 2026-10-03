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
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.level.GameType
import net.minecraft.world.level.Level

object ArchLampItem : ItemPackagedHex(Properties().stacksTo(1).rarity(Rarity.EPIC)) {
    override fun use(level: Level, user: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {
        val stack = user.getItemInHand(hand)
        if (!hasHex(stack))
            return InteractionResultHolder.fail(stack)

        val active = stack.get(DataComponents.CUSTOM_DATA)?.copyTag()?.getBoolean("active") ?: false

        if (level.isClientSide) {
            level.playLocalSound(user.x, user.y, user.z,
                (if (active) HexicalSounds.LAMP_DEACTIVATE else HexicalSounds.LAMP_ACTIVATE).get(), SoundSource.MASTER, 1f, 1f, true)
            return InteractionResultHolder.success(stack)
        }

        if (active) {
            val vm = CastingVM(CastingImage(), LampCastEnv(user as ServerPlayer, hand, true, stack))
            vm.queueExecuteAndWrapIotas((stack.item as ArchLampItem).getHex(stack, level as ServerLevel)!!, level)
            CustomData.update(DataComponents.CUSTOM_DATA, stack) { tag -> tag.putBoolean("active", false) }
            return InteractionResultHolder.success(stack)
        }

        CustomData.update(DataComponents.CUSTOM_DATA, stack) { tag ->
            tag.putBoolean("active", true)
            tag.putCompound("position", user.eyePosition.serializeToNBT())
            tag.putCompound("rotation", user.lookAngle.serializeToNBT())
            tag.putCompound("velocity", user.deltaMovement.serializeToNBT())
            tag.put("storage", IotaType.TYPED_CODEC.encodeStart(RegistryOps.create(NbtOps.INSTANCE, level.registryAccess()), NullIota()).getOrThrow())
            tag.putLong("start_time", level.gameTime)
        }

        return InteractionResultHolder.success(stack)
    }

    override fun inventoryTick(stack: ItemStack, level: Level, user: Entity, slot: Int, selected: Boolean) {
        if (level.isClientSide) return
        if (getMedia(stack) == 0L) return
        if (user !is ServerPlayer) return
        val active = stack.get(DataComponents.CUSTOM_DATA)?.copyTag()?.getBoolean("active") ?: false
        if (!active) return
        if (user.gameMode.gameModeForPlayer == GameType.SPECTATOR) return
        val vm = CastingVM(CastingImage(), LampCastEnv(user, InteractionHand.MAIN_HAND, false, stack))
        vm.queueExecuteAndWrapIotas((stack.item as ArchLampItem).getHex(stack, level as ServerLevel)!!, level)
    }

    override fun canDrawMediaFromInventory(stack: ItemStack) = false
    override fun canRecharge(stack: ItemStack) = false
    override fun breakAfterDepletion() = false
    override fun cooldown() = 0
}