package miyucomics.hexical.features.grimoires

import at.petrak.hexcasting.api.casting.eval.ExecutionClientView
import at.petrak.hexcasting.api.casting.eval.env.PackagedItemCastEnv
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv
import at.petrak.hexcasting.api.casting.eval.env.StaffCastEnv
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.PatternIota
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import miyucomics.hexical.features.item_cache.itemCache
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer

object GrimoireHandler {
    @JvmStatic
    fun handleGrimoire(vm: CastingVM, iota: Iota, world: ServerLevel): ExecutionClientView? {
        val env = vm.env
        if (env !is StaffCastEnv && env !is PlayerBasedCastEnv && env !is PackagedItemCastEnv)
            return null
        if (vm.image.escapeNext || iota.type !== HexIotaTypes.PATTERN)
            return null

        val pattern = (iota as PatternIota).pattern
        val expansion = (env.castingEntity!! as ServerPlayer).itemCache().grimoireMacros[pattern.anglesSignature()] ?: return null
        return vm.queueExecuteAndWrapIotas(expansion, world)
    }
}
