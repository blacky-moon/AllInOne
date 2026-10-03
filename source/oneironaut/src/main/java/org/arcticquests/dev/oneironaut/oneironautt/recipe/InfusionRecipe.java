package org.arcticquests.dev.oneironaut.oneironautt.recipe;

import at.petrak.hexcasting.common.lib.HexStateIngredients;
import at.petrak.hexcasting.common.recipe.ingredient.state.StateIngredient;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public record InfusionRecipe(
        StateIngredient blockIn,
        BlockState blockOut,
        long mediaCost
) implements Recipe<EmptyRecipeInput> {

    public boolean matches(BlockState state) {
        return this.blockIn.test(state);
    }

    @Override
    public boolean matches(EmptyRecipeInput input, Level level) {
        return false;
    }

    @Override
    public ItemStack assemble(EmptyRecipeInput input, HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return OneironautRecipeSerializer.INFUSE;
    }

    @Override
    public RecipeType<?> getType() {
        return OneironautRecipeTypes.INFUSION_TYPE;
    }
    public static class Serializer implements RecipeSerializer<InfusionRecipe> {
        public static final MapCodec<InfusionRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        HexStateIngredients.TYPED_CODEC.fieldOf("blockIn").forGetter(InfusionRecipe::blockIn),
                        BlockState.CODEC.fieldOf("resultType").forGetter(InfusionRecipe::blockOut),
                        Codec.LONG.fieldOf("mediaCost").forGetter(InfusionRecipe::mediaCost)
                ).apply(instance, InfusionRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, InfusionRecipe> STREAM_CODEC = StreamCodec.composite(
                HexStateIngredients.TYPED_STREAM_CODEC, InfusionRecipe::blockIn,
                ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY), InfusionRecipe::blockOut,
                ByteBufCodecs.VAR_LONG, InfusionRecipe::mediaCost,
                InfusionRecipe::new
        );

        @Override
        public MapCodec<InfusionRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, InfusionRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}