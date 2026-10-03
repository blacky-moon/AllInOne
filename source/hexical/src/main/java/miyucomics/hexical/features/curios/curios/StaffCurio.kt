package miyucomics.hexical.features.curios.curios

import at.petrak.hexcasting.api.casting.iota.Iota
import miyucomics.hexical.features.curios.CurioItem
import miyucomics.hexical.inits.HexicalSounds
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.item.ItemStack

object StaffCurio : CurioItem() {
    override fun postCharmCast(user: ServerPlayer, item: ItemStack, hand: InteractionHand, world: ServerLevel, stack: List<Iota>) {
        world.playSound(null, user.x, user.y, user.z, HexicalSounds.MAGIC_WOOSHES.get(), SoundSource.PLAYERS, 1f, 1f)
    }
}