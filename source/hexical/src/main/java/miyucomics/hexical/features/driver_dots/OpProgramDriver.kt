package miyucomics.hexical.features.driver_dots

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getList
import at.petrak.hexcasting.api.casting.getPattern
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem
import miyucomics.hexical.misc.CastingUtils
import miyucomics.hexical.misc.HexSerialization
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData

object OpProgramDriver : SpellAction {
    override val argc = 2

    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val dot = env.getHeldItemToOperateOn { it.item is DriverDotItem }
            ?: throw MishapBadOffhandItem.of(null, "driver_dot")
        val pattern = args.getPattern(0, argc)
        val program = args.getList(1, argc).toList()
        CastingUtils.assertNoTruename(args[0], env)
        return SpellAction.Result(Spell(dot.stack, pattern, program), 0, listOf())
    }

    private data class Spell(val dot: ItemStack, val pattern: HexPattern, val program: List<Iota>) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            val customData = dot.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY)
            val tag = customData.copyTag()
            val lookup = env.world.registryAccess()

            // Сериализуем программу с учетом реестров
            tag.put("program", HexSerialization.serializeHex(program, lookup))

            if ((dot.item as DriverDotItem).hasKey) {
                tag.putString("pattern", pattern.anglesSignature())

                // Сериализуем паттерн. В 1.21.1 используем Codec, если serializeToNBT() был удален
                val patternTag = HexPattern.CODEC.encodeStart(NbtOps.INSTANCE, pattern)
                    .result()
                    .orElse(CompoundTag())
                tag.put("display", patternTag)
            }

            dot.set(DataComponents.CUSTOM_DATA, CustomData.of(tag))
        }
    }
}