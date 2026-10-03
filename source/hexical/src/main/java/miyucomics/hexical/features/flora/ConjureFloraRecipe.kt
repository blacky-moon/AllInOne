package miyucomics.hexical.features.flora

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import miyucomics.hexical.HexicalMain
import net.minecraft.core.HolderLookup
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.item.crafting.SingleRecipeInput
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

class ConjureFloraRecipe(
    val input: Ingredient,
    val state: BlockState,
    val cost: Long
) : Recipe<SingleRecipeInput> {

    override fun matches(recipeInput: SingleRecipeInput, level: Level): Boolean =
        input.test(recipeInput.getItem(0))

    override fun assemble(recipeInput: SingleRecipeInput, registries: HolderLookup.Provider): ItemStack =
        input.items[0].copy()

    override fun getResultItem(registries: HolderLookup.Provider): ItemStack =
        input.items[0].copy()

    override fun canCraftInDimensions(width: Int, height: Int) = true

    override fun getType(): RecipeType<*> = TYPE
    override fun getSerializer(): RecipeSerializer<*> = SERIALIZER

    companion object {
        val TYPE: RecipeType<ConjureFloraRecipe> = RecipeType.simple(HexicalMain.id("conjure_flora"))
        val SERIALIZER: RecipeSerializer<ConjureFloraRecipe> = ConjureFloraSerializer.INSTANCE

        val CODEC: MapCodec<ConjureFloraRecipe> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                Ingredient.CODEC.fieldOf("input").forGetter { it.input },
                BlockState.CODEC.fieldOf("state").forGetter { it.state },
                Codec.LONG.fieldOf("cost").forGetter { it.cost }
            ).apply(instance, ::ConjureFloraRecipe)
        }

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ConjureFloraRecipe> = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, ConjureFloraRecipe::input,
            ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY), ConjureFloraRecipe::state,
            ByteBufCodecs.VAR_LONG, ConjureFloraRecipe::cost,
            ::ConjureFloraRecipe
        )
    }
}
