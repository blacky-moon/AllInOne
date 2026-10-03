package at.petrak.hexcasting.forge.interop.emi;

import at.petrak.hexcasting.api.mod.HexTags;
import at.petrak.hexcasting.common.casting.actions.spells.OpMakeBattery;
import at.petrak.hexcasting.interop.utils.PhialRecipeStackBuilder;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static at.petrak.hexcasting.api.HexAPI.modLoc;

public class PhialEmiRecipe implements EmiRecipe {
    private final ResourceLocation id;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;

    public PhialEmiRecipe(ResourceLocation id, OpMakeBattery recipe) {
        this.id = id;
        var stacks = PhialRecipeStackBuilder.createStacks();

        EmiIngredient inputMedia = EmiIngredient.of(stacks.getFirst().stream().map(EmiStack::of).toList());
        EmiIngredient basePhial = EmiIngredient.of(Ingredient.of(HexTags.Items.PHIAL_BASE));

        this.inputs = List.of(inputMedia, basePhial);
        this.outputs = stacks.getSecond().stream().map(EmiStack::of).toList();
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return HexEmiPlugin.PHIAL_CATEGORY;
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
        return 113;
    }

    @Override
    public int getDisplayHeight() {
        return 40;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(modLoc("textures/gui/phial_jei.png"), 0, 0, 113, 40, 0, 0);

        widgets.addSlot(inputs.get(0), 12, 12);
        widgets.addSlot(inputs.get(1), 47, 12);

        if (!outputs.isEmpty()) {
            widgets.addSlot(outputs.getFirst(), 85, 12).recipeContext(this);
        }
    }
}