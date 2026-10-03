package miyucomics.hexical.features.mage_blocks

import miyucomics.hexical.features.mage_blocks.modifiers.*
import miyucomics.hexical.misc.InitHook
import net.minecraft.resources.ResourceLocation

object MageBlockModifierRegistry : InitHook() {
    private val MODIFIER_MAP = mutableMapOf<ResourceLocation, MageBlockModifierType<*>>()

    fun get(id: ResourceLocation): MageBlockModifierType<*>? = MODIFIER_MAP[id]

    private fun register(type: MageBlockModifierType<*>) {
        MODIFIER_MAP[type.id] = type
    }

    override fun init() {
        register(BouncyModifier.TYPE)
        register(LifespanModifier.TYPE)
        register(RedstoneModifier.TYPE)
        register(VolatileModifier.TYPE)
    }
}