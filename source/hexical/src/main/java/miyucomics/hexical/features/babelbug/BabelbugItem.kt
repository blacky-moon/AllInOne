package miyucomics.hexical.features.babelbug

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.item.IotaHolderItem
import at.petrak.hexcasting.common.lib.HexDataComponents
import miyucomics.hexical.inits.HexicalSounds
import net.minecraft.core.component.DataComponents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.level.Level

object BabelbugItem : Item(Properties().stacksTo(1).rarity(Rarity.UNCOMMON)), IotaHolderItem {

    override fun use(world: Level, user: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {
        val stack = user.getItemInHand(hand)

        // "active" остаётся нашим собственным флагом-тумблером — это не то, что хранит hex-casting,
        // так что CUSTOM_DATA тут уместен и не мешает записи иоты через IOTA_HOLDER_IOTA.
        val customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
        val tag = customData.copyTag()

        tag.putBoolean("active", !tag.getBoolean("active"))

        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag))

        if (world.isClientSide) {
            world.playSound(
                user, user.x, user.y, user.z,
                HexicalSounds.BABELBUG_CHIRPS.get(), SoundSource.MASTER, 1f, 1f
            )
        }
        return InteractionResultHolder.success(stack)
    }

    override fun writeable(stack: ItemStack): Boolean = true

    override fun canWrite(stack: ItemStack, iota: Iota?): Boolean = iota == null || iota is ListIota

    override fun writeDatum(stack: ItemStack, iota: Iota?) {

        if (iota == null) {
            stack.remove(HexDataComponents.IOTA_HOLDER_IOTA)
        } else {
            stack.set(HexDataComponents.IOTA_HOLDER_IOTA, iota)
        }
    }
}