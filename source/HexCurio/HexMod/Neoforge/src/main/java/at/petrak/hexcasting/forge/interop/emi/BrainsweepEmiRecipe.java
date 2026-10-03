package at.petrak.hexcasting.forge.interop.emi;

import at.petrak.hexcasting.client.ClientTickCounter;
import at.petrak.hexcasting.common.recipe.BrainsweepRecipe;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static at.petrak.hexcasting.api.HexAPI.modLoc;
import static at.petrak.hexcasting.client.render.RenderLib.renderEntity;

public class BrainsweepEmiRecipe implements EmiRecipe {
    private final ResourceLocation id;
    private final BrainsweepRecipe recipe;
    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;

    public BrainsweepEmiRecipe(ResourceLocation id, BrainsweepRecipe recipe) {
        this.id = id;
        this.recipe = recipe;
        this.inputs = List.of(EmiIngredient.of(recipe.blockIn().getDisplayedStacks().stream().map(EmiStack::of).toList()));
        this.outputs = List.of(EmiStack.of(recipe.result().getBlock()));
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return HexEmiPlugin.BRAINSWEEP_CATEGORY;
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
        return 118;
    }

    @Override
    public int getDisplayHeight() {
        return 86;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(modLoc("textures/gui/brainsweep_jei.png"), 0, 0, 118, 86, 0, 0);

        widgets.addSlot(inputs.getFirst(), 12, 35);
        widgets.addSlot(outputs.getFirst(), 87, 35).recipeContext(this);

        widgets.addDrawable(0, 0, 118, 86, (graphics, mouseX, mouseY, delta) -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level != null) {
                var examples = recipe.entityIn().exampleEntities(level);
                Entity example = null;
                if (!examples.isEmpty()) {
                    long seconds = System.currentTimeMillis() / 1000;
                    example = examples.get((int) (seconds % examples.size()));
                }
                if (example == null) return;

                RenderSystem.enableBlend();
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                renderEntity(graphics, example, level, 50, 62.5f, ClientTickCounter.getTotal(), 20, 0);
            }
        });
    }
}