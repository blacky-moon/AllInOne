package miyucomics.hexical.features.autographs

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.env.StaffCastEnv
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.api.utils.getOrCreateList
import at.petrak.hexcasting.xplat.IXplatAbstractions
import com.mojang.serialization.JsonOps
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData

object OpAutograph : SpellAction {
    override val argc = 0
    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        if (env !is StaffCastEnv)
            throw NeedsStaffMishap()
        val stack = env.getHeldItemToOperateOn { !it.isEmpty } ?: throw MishapBadOffhandItem.of(null, "anything")
        return SpellAction.Result(Spell(stack.stack), 0, listOf())
    }

    private data class Spell(val stack: ItemStack) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            val caster = env.castingEntity as ServerPlayer

            val tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag()
            val list = tag.getOrCreateList("autographs", Tag.TAG_COMPOUND.toInt())
            list.removeIf { compound -> (compound as CompoundTag).getString("name") == caster.scoreboardName }

            val pigment = IXplatAbstractions.INSTANCE.getPigment(caster)
            val pigmentTag = FrozenPigment.CODEC.encodeStart(NbtOps.INSTANCE, pigment)
                .result()
                .orElseGet { CompoundTag() }

            list.add(0, CompoundTag().apply {
                putString("name", caster.scoreboardName)
                put("pigment", pigmentTag)
            })
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag))
        }
    }
}