package miyucomics.hexical.features.transmuting

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import miyucomics.hexical.features.transmuting.TransmutingRecipe
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.RecipeSerializer

class TransmutingSerializer : RecipeSerializer<TransmutingRecipe> {

    override fun codec(): MapCodec<TransmutingRecipe> {
        return RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter { it.input },
                Codec.LONG.fieldOf("cost").forGetter { it.cost },
                ItemStack.CODEC.listOf().fieldOf("output").forGetter { it.output }
            ).apply(instance) { input, cost, output ->
                TransmutingRecipe(ResourceLocation.parse(""), input, cost, output)
            }
        }
    }

    override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, TransmutingRecipe> {
        return StreamCodec.of(
            { buf, recipe ->
                Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.input)
                buf.writeLong(recipe.cost)
                buf.writeInt(recipe.output.size)
                for (item in recipe.output) {
                    ItemStack.STREAM_CODEC.encode(buf, item)
                }
            },
            { buf ->
                val input = Ingredient.CONTENTS_STREAM_CODEC.decode(buf)
                val cost = buf.readLong()
                val size = buf.readInt()
                val outputs = mutableListOf<ItemStack>()
                for (i in 0 until size) {
                    outputs.add(ItemStack.STREAM_CODEC.decode(buf))
                }
                TransmutingRecipe(ResourceLocation.parse(""), input, cost, outputs)
            }
        )
    }

    companion object {
        val INSTANCE: TransmutingSerializer = TransmutingSerializer()
    }
}