package miyucomics.hexical.inits

import miyucomics.hexical.HexicalMain
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

object HexicalSounds {
    private val SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, HexicalMain.MOD_ID)

    val AMETHYST_MELT: Supplier<SoundEvent> = register("amethyst_melt")
    val ITEM_DUNKS: Supplier<SoundEvent> = register("item_dunks")
    val EVOKING_MURMUR: Supplier<SoundEvent> = register("evoking_murmur")
    val LAMP_ACTIVATE: Supplier<SoundEvent> = register("lamp_activate")
    val LAMP_DEACTIVATE: Supplier<SoundEvent> = register("lamp_deactivate")
    val MAGIC_WOOSHES: Supplier<SoundEvent> = register("magic_wooshes")
    val SUDDEN_REALIZATION: Supplier<SoundEvent> = register("sudden_realization")
    val REPLENISH_AIR: Supplier<SoundEvent> = register("replenish_air")
    val BABELBUG_CHIRPS: Supplier<SoundEvent> = register("babelbug_chirps")
    val HANDBELL_CHIMES: Supplier<SoundEvent> = register("handbell_chimes")

    private fun register(name: String): Supplier<SoundEvent> {
        val id = HexicalMain.id(name)
        return SOUNDS.register(name) { _: ResourceLocation ->
            SoundEvent.createVariableRangeEvent(id)
        }
    }

    fun init(modEventBus: IEventBus) {
        SOUNDS.register(modEventBus)
    }
}