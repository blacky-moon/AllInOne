package miyucomics.hexical.features.driver_dots

import at.petrak.hexcasting.api.casting.PatternShapeMatch
import at.petrak.hexcasting.api.casting.SpellList
import at.petrak.hexcasting.api.casting.eval.CastResult
import at.petrak.hexcasting.api.casting.eval.ResolvedPatternType
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM
import at.petrak.hexcasting.api.casting.eval.vm.FrameEvaluate
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.PatternIota
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.common.casting.PatternRegistryManifest
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
import miyucomics.hexical.features.item_cache.itemCache
import net.minecraft.ChatFormatting
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.TooltipFlag

open class DriverDotItem(val hasKey: Boolean) : Item(Properties().stacksTo(1).rarity(Rarity.UNCOMMON)) {

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        val customData = stack.get(DataComponents.CUSTOM_DATA) ?: return
        val nbt = customData.copyTag()

        if (!nbt.contains("program")) return

        val registryAccess = context.registries() ?: return
        val ops = registryAccess.createSerializationContext(NbtOps.INSTANCE)

        if (hasKey && nbt.contains("display")) {
            val patternTag = nbt.getCompound("display")
            val pattern = HexPattern.CODEC.parse(ops, patternTag).result().orElse(null)
            if (pattern != null) {
                tooltipComponents.add(
                    Component.translatable("hexical.driver_dot.pattern", PatternIota.display(pattern))
                        .withStyle(ChatFormatting.GRAY)
                )
            }
        }

        val programList = nbt.getList("program", Tag.TAG_COMPOUND.toInt())
        val programDisplay = programList.fold(Component.empty()) { acc, curr ->
            // В 1.21.1 IotaType.getDisplay удален, десериализуем и вызываем .display()
            val iota = IotaType.TYPED_CODEC.parse(ops, curr).result().orElse(null)
            if (iota != null) acc.append(iota.display()) else acc
        }

        tooltipComponents.add(
            Component.translatable("hexical.driver_dot.program", programDisplay)
                .withStyle(ChatFormatting.GRAY)
        )
    }

    companion object {
        val substitutionRules: List<(CastingVM, PatternIota, SpellContinuation) -> CastResult?> = listOf(
            { vm, iota, continuation ->
                val env = vm.env
                val pattern = iota.pattern
                val iotaList = (env.castingEntity as? ServerPlayer)?.itemCache()?.copperDriverDotMacros?.get(pattern.anglesSignature())
                    ?: return@listOf null
                CastResult(iota, continuation, vm.image.copy(stack = vm.image.stack.plus(iotaList)), listOf(), ResolvedPatternType.EVALUATED, HexEvalSounds.NOTHING)
            },
            { vm, iota, continuation ->
                val env = vm.env
                val pattern = iota.pattern
                val program = (env.castingEntity as? ServerPlayer)?.itemCache()?.ironDriverDotMacros?.get(pattern.anglesSignature())
                    ?: return@listOf null
                constructCastResult(vm, continuation, iota, program)
            },
            { vm, iota, continuation ->
                val env = vm.env
                val pattern = iota.pattern
                val targetSignature = pattern.anglesSignature()
                val prefix = (env.castingEntity as? ServerPlayer)?.itemCache()?.goldDriverDotMacros?.keys?.firstOrNull(targetSignature::startsWith)
                    ?: return@listOf null
                val program = (env.castingEntity as? ServerPlayer)?.itemCache()?.goldDriverDotMacros?.get(prefix)!!
                constructCastResult(vm, continuation, iota, program, vm.image.stack.plus(iota))
            },
            { vm, iota, continuation ->
                val program = (vm.env.castingEntity as? ServerPlayer)?.itemCache()?.netheriteDriverDotProgram
                    ?: return@listOf null
                constructCastResult(vm, continuation, iota, program, vm.image.stack.plus(iota))
            }
        )

        private fun constructCastResult(
            vm: CastingVM,
            continuation: SpellContinuation,
            iota: PatternIota,
            program: List<Iota>,
            newStack: List<Iota>? = null
        ): CastResult {
            return CastResult(
                iota,
                continuation.pushFrame(FrameEvaluate(SpellList.LList(program), false)),
                if (newStack != null) vm.image.copy(stack = newStack) else vm.image,
                listOf(),
                ResolvedPatternType.EVALUATED,
                HexEvalSounds.NOTHING
            )
        }

        @JvmStatic
        fun applySubstitution(vm: CastingVM, iota: PatternIota, continuation: SpellContinuation): CastResult? {
            if (vm.env !is PlayerBasedCastEnv) return null

            val patternTest = PatternRegistryManifest.matchPattern(iota.pattern, vm.env)
            if (patternTest !is PatternShapeMatch.Nothing) return null

            substitutionRules.forEach { handler ->
                val result = handler(vm, iota, continuation)
                if (result != null) return result
            }
            return null
        }
    }
}