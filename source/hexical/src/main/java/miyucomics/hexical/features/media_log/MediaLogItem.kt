package miyucomics.hexical.features.media_log

import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.UseAnim
import net.minecraft.world.level.Level

object MediaLogItem : Item(Properties().stacksTo(1)) {
    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResultHolder<ItemStack> {
        val stack = player.getItemInHand(hand)
        if (!level.isClientSide)
            (player as ServerPlayer).syncMediaLog()
        player.startUsingItem(hand)
        return InteractionResultHolder.success(stack)
    }

    override fun getUseAnimation(stack: ItemStack): UseAnim = UseAnim.BOW
    override fun getDescriptionId(): String = "item.hexical.media_log." + ((System.currentTimeMillis() / 2000) % 2).toString()
}