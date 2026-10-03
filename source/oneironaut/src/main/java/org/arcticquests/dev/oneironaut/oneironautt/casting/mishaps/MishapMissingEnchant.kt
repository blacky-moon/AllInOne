package org.arcticquests.dev.oneironaut.oneironautt.casting.mishaps

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.GarbageIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.pigment.FrozenPigment
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.ItemStack
import net.minecraft.server.level.ServerPlayer
import net.minecraft.network.chat.Component
import net.minecraft.world.item.DyeColor

class MishapMissingEnchant(val stack: ItemStack, val enchant: Enchantment) : Mishap() {
    override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment = dyeColor(DyeColor.PURPLE)

    override fun particleSpray(ctx: CastingEnvironment) =
        ParticleSpray.burst(stack.entityRepresentation?.position()!!, 1.0)

    override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context): Component =
        error("oneironaut:missingenchant", stack.hoverName, enchant.description)

    override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
        stack.add(GarbageIota())
        if (env.castingEntity != null && env.castingEntity is ServerPlayer){
            (env.castingEntity as ServerPlayer).setExperienceLevels(((env.castingEntity as ServerPlayer).experienceLevel - 3).coerceAtLeast(0))
        }
    }

    companion object {
        @JvmStatic
        fun of(stack: ItemStack, enchant: Enchantment): MishapMissingEnchant {
            return MishapMissingEnchant(stack, enchant)
        }
    }

}