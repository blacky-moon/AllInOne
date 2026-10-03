package dev.kineticcat.complexhex.api.util

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.BlockState
import kotlin.jvm.javaClass

object BITHandlerRegistry {
    private val blockDisplayHandlerRegistry = mutableMapOf<Class<*>, (Iota, CastingEnvironment?, Int) -> BlockState>()
    private val itemDisplayHandlerRegistry = mutableMapOf<Class<*>, (Iota, CastingEnvironment?, Int) -> ItemStack>()
    private val textDisplayHandlerRegistry = mutableMapOf<Class<*>, (Iota, CastingEnvironment?, Int) -> Component>()

    fun registerBlockDisplayHandler(handler: (Iota, CastingEnvironment?, Int) -> BlockState, iotaClass: Class<*>) {
        blockDisplayHandlerRegistry[iotaClass] = handler
    }
    fun registerItemDisplayHandler(handler: (Iota, CastingEnvironment?, Int) -> ItemStack, iotaClass: Class<*>) {
        itemDisplayHandlerRegistry[iotaClass] = handler
    }

    fun registerTextDisplayHandler(handler: (Iota, CastingEnvironment?, Int) -> Component, iotaClass: Class<*>) {
        textDisplayHandlerRegistry[iotaClass] = handler
    }

    fun matchBlockDisplayIota(iota: Iota, level : CastingEnvironment?, idx: Int) : BlockState {
        if (!blockDisplayHandlerRegistry.containsKey(iota.javaClass)) {
            throw BITHandlers.blockMishap(iota, idx)
        }
        val handler = blockDisplayHandlerRegistry[iota.javaClass]!!
        if (level != null) {
            return handler(iota, level, idx)
        }
        return handler(iota, null, idx)
    }

    fun matchItemDisplayIota(iota: Iota, level : CastingEnvironment?, idx : Int) : ItemStack {
        if (!itemDisplayHandlerRegistry.containsKey(iota.javaClass)) {
            throw BITHandlers.itemMishap(iota, idx)
        }
        val handler = itemDisplayHandlerRegistry[iota.javaClass]!!
        if (level != null) {
            return handler(iota, level, idx)
        }
        return handler(iota, null, idx)
    }

    fun matchTextDisplayIota(iota: Iota, level : CastingEnvironment?, idx: Int) : Component {
        if (!textDisplayHandlerRegistry.containsKey(iota.javaClass)) {
            throw BITHandlers.textMishap(iota, idx)
        }
        val handler = textDisplayHandlerRegistry[iota.javaClass]!!
        if (level != null) {
            return handler(iota, level, idx)
        }
        return handler(iota, null, idx)
    }
}