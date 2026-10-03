package at.petrak.hexcasting.forge.interop.emi;

import at.petrak.hexcasting.common.casting.actions.spells.OpEdifySapling;
import at.petrak.hexcasting.common.lib.HexBlocks;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static at.petrak.hexcasting.api.HexAPI.modLoc;

public class EdifyEmiRecipe implements EmiRecipe {
    private final ResourceLocation id;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;

    public EdifyEmiRecipe(ResourceLocation id, OpEdifySapling recipe) {
        this.id = id;
        this.inputs = List.of(EmiIngredient.of(Ingredient.of(ItemTags.SAPLINGS)));
        this.outputs = List.of(
                EmiStack.of(HexBlocks.AMETHYST_EDIFIED_LEAVES),
                EmiStack.of(HexBlocks.AVENTURINE_EDIFIED_LEAVES),
                EmiStack.of(HexBlocks.CITRINE_EDIFIED_LEAVES),
                EmiStack.of(HexBlocks.EDIFIED_LOG),
                EmiStack.of(HexBlocks.EDIFIED_LOG_AMETHYST),
                EmiStack.of(HexBlocks.EDIFIED_LOG_AVENTURINE),
                EmiStack.of(HexBlocks.EDIFIED_LOG_CITRINE)
        );
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return HexEmiPlugin.EDIFY_CATEGORY;
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return inputs;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return outputs;
    }

    @Override
    public int getDisplayWidth() {
        return 79;
    }

    @Override
    public int getDisplayHeight() {
        return 61;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(modLoc("textures/gui/edify_jei.png"), 0, 0, 79, 61, 0, 0);

        widgets.addSlot(inputs.getFirst(), 12, 22);

        widgets.addSlot(EmiIngredient.of(List.of(
                outputs.get(0), outputs.get(1), outputs.get(2)
        )), 51, 10).recipeContext(this);

        widgets.addSlot(EmiIngredient.of(List.of(
                outputs.get(3), outputs.get(4), outputs.get(5), outputs.get(6)
        )), 51, 35).recipeContext(this);
    }
}