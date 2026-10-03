package miyucomics.hexical.features.transmuting

import at.petrak.hexcasting.api.utils.isMediaItem
import at.petrak.hexcasting.common.lib.HexItems
import at.petrak.hexcasting.xplat.IXplatAbstractions
import miyucomics.hexical.features.media_jar.MediaJarBlock
import miyucomics.hexical.inits.HexicalComponents
import miyucomics.hexical.inits.HexicalItems
import miyucomics.hexical.inits.HexicalRecipes
import miyucomics.hexical.misc.InitHook
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.item.crafting.SingleRecipeInput
import net.minecraft.world.level.Level
import kotlin.math.min

@Suppress("OverrideOnly")
object TransmutingHelper : InitHook() {

    // ✅ ИСПРАВЛЕНО: Явно приводим тип, чтобы убрать Return type mismatch
    @Suppress("UNCHECKED_CAST")
    val TRANSMUTING_RECIPE: RecipeType<TransmutingRecipe>
        get() = HexicalRecipes.TRANSMUTING_TYPE.get() as RecipeType<TransmutingRecipe>

    override fun init() {
        // Пусто – регистрация происходит через DeferredRegister
    }

    fun transmuteItem(
        world: Level,
        stack: ItemStack,
        media: Long,
        insertMedia: (Long) -> Long,
        withdrawMedia: (Long) -> Boolean
    ): TransmutationResult {
        if (stack.`is`(HexItems.BATTERY)) {
            val mediaHolder = IXplatAbstractions.INSTANCE.findMediaHolder(stack)!!
            val given = min(mediaHolder.getMaxMedia() - mediaHolder.getMedia(), media)
            mediaHolder.insertMedia(given, false)
            withdrawMedia(given)
            return TransmutationResult.RefilledHolder
        }

        if (stack.`is`(HexicalItems.MEDIA_JAR_ITEM)) {
            val current = stack.get(HexicalComponents.MEDIA_HOLDER.get()) ?: 0L
            val given = min(MediaJarBlock.MAX_CAPACITY - current, media)
            if (given > 0)
                stack.set(HexicalComponents.MEDIA_HOLDER.get(), current + given)
            withdrawMedia(given)
            return TransmutationResult.RefilledHolder
        }

        if (isMediaItem(stack) && media < MediaJarBlock.MAX_CAPACITY) {
            val mediaHolder = IXplatAbstractions.INSTANCE.findMediaHolder(stack)!!
            val consumed = insertMedia(mediaHolder.getMedia())
            mediaHolder.withdrawMedia(consumed, false)
            return TransmutationResult.AbsorbedMedia
        }

        val recipe = getRecipe(stack, world)
        if (recipe != null && media >= recipe.cost) {
            stack.shrink(1)
            withdrawMedia(recipe.cost)
            return TransmutationResult.TransmutedItems(recipe.output.map { it.copy() })
        }

        return TransmutationResult.Pass
    }

    // ✅ ИСПРАВЛЕНО: Оставлен только ОДИН метод getRecipe (дубликат удален)
    private fun getRecipe(input: ItemStack, world: Level): TransmutingRecipe? {
        val singleInput = SingleRecipeInput(input)
        // Явно указываем дженерики, чтобы Kotlin не ругался на вывод типов
        for (recipe in world.recipeManager.getAllRecipesFor<SingleRecipeInput, TransmutingRecipe>(TRANSMUTING_RECIPE)) {
            if (recipe.value().matches(singleInput, world))
                return recipe.value()
        }
        return null
    }
}

sealed class TransmutationResult {
    object AbsorbedMedia : TransmutationResult()
    object Pass : TransmutationResult()
    object RefilledHolder : TransmutationResult()
    data class TransmutedItems(val output: List<ItemStack>) : TransmutationResult()
}