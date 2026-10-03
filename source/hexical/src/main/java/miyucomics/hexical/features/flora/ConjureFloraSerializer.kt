package miyucomics.hexical.features.flora

import com.mojang.serialization.MapCodec
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.crafting.RecipeSerializer

class ConjureFloraSerializer : RecipeSerializer<ConjureFloraRecipe> {
    override fun codec(): MapCodec<ConjureFloraRecipe> = ConjureFloraRecipe.CODEC
    override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, ConjureFloraRecipe> = ConjureFloraRecipe.STREAM_CODEC

    companion object {
        @JvmField
        val INSTANCE: ConjureFloraSerializer = ConjureFloraSerializer()
    }
}