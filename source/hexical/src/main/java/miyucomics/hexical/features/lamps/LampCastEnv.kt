package miyucomics.hexical.features.lamps

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.eval.CastResult
import at.petrak.hexcasting.api.casting.eval.env.PackagedItemCastEnv
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.api.utils.putCompound
import at.petrak.hexcasting.xplat.IXplatAbstractions
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.NbtOps
import net.minecraft.resources.RegistryOps
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData

class LampCastEnv(caster: ServerPlayer, castingHand: InteractionHand, val finale: Boolean, val stack: ItemStack) : PackagedItemCastEnv(caster, castingHand) {
    override fun produceParticles(particles: ParticleSpray, pigment: FrozenPigment) {}
    override fun postExecution(result: CastResult) = super.postExecution(result.copy(cast = NullIota()))
    override fun getCastingHand(): InteractionHand = this.castingHand
    override fun getPigment(): FrozenPigment = IXplatAbstractions.INSTANCE.getPigment(this.caster)

    fun setInternalIota(iota: Iota) {
        val ops = RegistryOps.create(NbtOps.INSTANCE, this.caster.level().registryAccess())
        CustomData.update(DataComponents.CUSTOM_DATA, stack) { tag -> tag.put("storage", IotaType.TYPED_CODEC.encodeStart(ops, iota).getOrThrow()) }
    }
}