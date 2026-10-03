package miyucomics.hexical.features.babelbug

import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM
import miyucomics.hexical.features.item_cache.itemCache
import miyucomics.hexical.misc.InitHook
import miyucomics.hexpose.iotas.TextIota
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.ServerChatEvent

object BabelbugListener : InitHook() {
    override fun init() {
        NeoForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun onServerChat(event: ServerChatEvent) {
        val sender = event.player
        val message = event.message

        val babelbugProgram = sender.itemCache().babelbugProgram ?: return

        val contents = message.string
        val vm = CastingVM(
            CastingImage().copy(stack = TextIota.asActionResult(Component.literal(contents))),
            BabelbugCastEnv(sender, InteractionHand.MAIN_HAND)
        )
        vm.queueExecuteAndWrapIotas(babelbugProgram, sender.serverLevel())

        val remainingStack = vm.image.stack
        if (remainingStack.isNotEmpty()) {
            event.message = remainingStack[remainingStack.lastIndex].display()
            return
        }

        event.isCanceled = true
    }
}
