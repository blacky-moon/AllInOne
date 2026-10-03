package miyucomics.hexical.features.toast

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getPlayer
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import miyucomics.hexpose.iotas.ItemStackIota
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.network.PacketDistributor

object OpToast : SpellAction {
    override val argc = 4

    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val recipient = args.getPlayer(env.world, 0, argc)
        val title = args[1].display()
        val description = args[2].display()
        val stack = ItemStackIota.getItemStack(args, 3, argc)   // напрямую получаем ItemStack
        val cost = if (recipient == env.castingEntity) 0 else MediaConstants.SHARD_UNIT
        return SpellAction.Result(Spell(recipient, title, description, stack), cost, listOf())
    }

    private data class Spell(
        val recipient: ServerPlayer,
        val title: net.minecraft.network.chat.Component,
        val description: net.minecraft.network.chat.Component,
        val stack: ItemStack
    ) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            PacketDistributor.sendToPlayer(recipient, ToastPayload(title, description, stack))
        }
    }
}