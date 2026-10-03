package miyucomics.hexical.features.periwinkle

import miyucomics.hexical.HexicalMain
import miyucomics.hexical.inits.HexicalItems
import miyucomics.hexical.misc.InitHook
import net.minecraft.core.Holder
import net.minecraft.core.registries.Registries
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.item.Items
import net.minecraft.world.item.alchemy.Potion
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

object WooleyedEffectRegister : InitHook() {
    private val MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, HexicalMain.MOD_ID)
    private val POTIONS = DeferredRegister.create(Registries.POTION, HexicalMain.MOD_ID)

    val WOOLEYED_EFFECT: DeferredHolder<net.minecraft.world.effect.MobEffect, WooleyedEffect> =
        MOB_EFFECTS.register("wooleyed", Supplier { WooleyedEffect() })

    @JvmStatic
    val INSTANCE: WooleyedEffect get() = WOOLEYED_EFFECT.value()

    @JvmStatic
    val HOLDER: Holder<net.minecraft.world.effect.MobEffect> get() = WOOLEYED_EFFECT

    val WOOLEYED_POTION: DeferredHolder<Potion, Potion> = POTIONS.register("wooleyed", Supplier {
        Potion(MobEffectInstance(HOLDER, 12000, 0))
    })

    val LONG_WOOLEYED_POTION: DeferredHolder<Potion, Potion> = POTIONS.register("long_wooleyed", Supplier {
        Potion(MobEffectInstance(HOLDER, 48000, 0))
    })

    val STRONG_WOOLEYED_POTION: DeferredHolder<Potion, Potion> = POTIONS.register("strong_wooleyed", Supplier {
        Potion(MobEffectInstance(HOLDER, 6000, 1))
    })

    override fun init() {
        // Подписка на RegisterBrewingRecipesEvent на общей шине
        NeoForge.EVENT_BUS.addListener<RegisterBrewingRecipesEvent> { event ->
            registerBrewingRecipes(event.builder)
        }
    }

    fun init(modEventBus: IEventBus) {
        MOB_EFFECTS.register(modEventBus)
        POTIONS.register(modEventBus)
    }

    private fun registerBrewingRecipes(builder: net.minecraft.world.item.alchemy.PotionBrewing.Builder) {
        val periwinkleItem = HexicalItems.PERIWINKLE_FLOWER_ITEM.get()

        builder.addMix(
            net.minecraft.world.item.alchemy.Potions.AWKWARD,
            periwinkleItem,
            WOOLEYED_POTION
        )
        builder.addMix(
            WOOLEYED_POTION,
            Items.REDSTONE,
            LONG_WOOLEYED_POTION
        )
        builder.addMix(
            WOOLEYED_POTION,
            Items.GLOWSTONE_DUST,
            STRONG_WOOLEYED_POTION
        )
    }
}

class WooleyedEffect : net.minecraft.world.effect.MobEffect(MobEffectCategory.BENEFICIAL, 0xff_a678f1.toInt())
