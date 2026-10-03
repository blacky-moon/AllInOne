package miyucomics.hexical.inits

import miyucomics.hexical.HexicalMain
import miyucomics.hexical.features.flora.ConjureFloraRecipe
import miyucomics.hexical.features.flora.ConjureFloraSerializer
import miyucomics.hexical.features.transmuting.TransmutingRecipe
import miyucomics.hexical.features.transmuting.TransmutingSerializer
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.crafting.RecipeType
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

object HexicalRecipes {
    private val TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, HexicalMain.Companion.MOD_ID)
    private val SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, HexicalMain.Companion.MOD_ID)

    // ✅ ИСПРАВЛЕНО: Используем RecipeType.simple вместо удаленного Type.INSTANCE
    @JvmField
    val TRANSMUTING_TYPE = TYPES.register("transmuting", Supplier {
        RecipeType.simple<TransmutingRecipe>(HexicalMain.Companion.id("transmuting"))
    })

    @JvmField
    val TRANSMUTING_SERIALIZER = SERIALIZERS.register("transmuting",
        Supplier { TransmutingSerializer.Companion.INSTANCE })

    @JvmField
    val FLORA_TYPE = TYPES.register("flora", Supplier {
        RecipeType.simple<ConjureFloraRecipe>(HexicalMain.Companion.id("flora"))
    })

    @JvmField
    val FLORA_SERIALIZER = SERIALIZERS.register("flora", Supplier { ConjureFloraSerializer.Companion.INSTANCE })

    fun init(bus: IEventBus) {
        TYPES.register(bus)
        SERIALIZERS.register(bus)
    }
}