package miyucomics.hexical.features.flora

import miyucomics.hexical.HexicalMain
import miyucomics.hexical.misc.InitHook
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.block.Block
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

object ConjureFloraHook : InitHook() {
    private val RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, HexicalMain.MOD_ID)
    private val RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, HexicalMain.MOD_ID)

    @JvmField
    val CONJURE_FLORA_RECIPE: DeferredHolder<RecipeType<*>, RecipeType<ConjureFloraRecipe>> =
        // ИСПРАВЛЕНО: Явно используем Supplier для устранения Overload resolution ambiguity
        RECIPE_TYPES.register("conjure_flora", Supplier {
            RecipeType.simple(ResourceLocation.fromNamespaceAndPath(HexicalMain.MOD_ID, "conjure_flora"))
        })

    init {
        RECIPE_SERIALIZERS.register("conjure_flora", Supplier { ConjureFloraSerializer.INSTANCE })
    }

    fun getRecipe(world: ServerLevel, target: Block): ConjureFloraRecipe? {
        return world.recipeManager.getAllRecipesFor(CONJURE_FLORA_RECIPE.get())
            .map { it.value }
            .firstOrNull { it.state.block == target }
    }

    override fun init() {}

    fun register(bus: IEventBus) {
        RECIPE_TYPES.register(bus)
        RECIPE_SERIALIZERS.register(bus)
    }
}