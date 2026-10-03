package miyucomics.hexical.features.autographs

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getPlayer
import at.petrak.hexcasting.api.casting.iota.Iota
import miyucomics.hexpose.iotas.ItemStackIota
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.world.item.component.CustomData

object OpHasAutograph : ConstMediaAction {
    override val argc = 2
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val stack = ItemStackIota.getItemStack(args, 0, argc)

        val customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
        if (customData === CustomData.EMPTY)
            return false.asActionResult
        val tag = customData.copyTag()
        if (!tag.contains("autographs"))
            return false.asActionResult

        val player = args.getPlayer(env.world, argc)
        val list = tag.getList("autographs", Tag.TAG_COMPOUND.toInt())
        return (list.count { (it as CompoundTag).getString("name") == player.scoreboardName } > 0).asActionResult
    }
}