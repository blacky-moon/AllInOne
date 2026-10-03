package at.petrak.hexcasting.forge.interop.emi;

import at.petrak.hexcasting.common.casting.actions.spells.OpEdifySapling;
import at.petrak.hexcasting.common.casting.actions.spells.OpMakeBattery;
import at.petrak.hexcasting.common.items.ItemStaff;
import at.petrak.hexcasting.common.lib.HexBlocks;
import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.common.recipe.HexRecipeStuffRegistry;
import at.petrak.hexcasting.interop.utils.PhialRecipeStackBuilder;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import java.util.List;

import static at.petrak.hexcasting.api.HexAPI.modLoc;

@EmiEntrypoint
public class HexEmiPlugin implements EmiPlugin {

    public static final EmiRecipeCategory BRAINSWEEP_CATEGORY = new EmiRecipeCategory(
            modLoc("brainsweep"),
            EmiStack.of(HexBlocks.AMETHYST_EDIFIED_LEAVES),
            EmiIngredient.of(List.of())
    ) {
        @Override
        public Component getName() {
            return Component.translatable("hexcasting.action." + modLoc("brainsweep"));
        }
    };

    public static final EmiRecipeCategory PHIAL_CATEGORY = new EmiRecipeCategory(
            modLoc("craft_phial"),
            EmiStack.of(HexItems.BATTERY),
            EmiIngredient.of(List.of())
    ) {
        @Override
        public Component getName() {
            return Component.translatable("hexcasting.action." + modLoc("craft/battery"));
        }
    };

    public static final EmiRecipeCategory EDIFY_CATEGORY = new EmiRecipeCategory(
            modLoc("edify_tree"),
            EmiStack.of(HexBlocks.AMETHYST_EDIFIED_LEAVES),
            EmiIngredient.of(List.of())
    ) {
        @Override
        public Component getName() {
            return Component.translatable("hexcasting.action." + modLoc("edify"));
        }
    };

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(BRAINSWEEP_CATEGORY);
        registry.addCategory(PHIAL_CATEGORY);
        registry.addCategory(EDIFY_CATEGORY);

        for (ItemStaff staff : new ItemStaff[]{
                HexItems.STAFF_OAK,
                HexItems.STAFF_SPRUCE,
                HexItems.STAFF_BIRCH,
                HexItems.STAFF_JUNGLE,
                HexItems.STAFF_ACACIA,
                HexItems.STAFF_DARK_OAK,
                HexItems.STAFF_CRIMSON,
                HexItems.STAFF_WARPED,
                HexItems.STAFF_MANGROVE,
                HexItems.STAFF_CHERRY,
                HexItems.STAFF_BAMBOO,
                HexItems.STAFF_EDIFIED,
        }) {
            EmiStack staffStack = EmiStack.of(staff);
            registry.addWorkstation(BRAINSWEEP_CATEGORY, staffStack);
            registry.addWorkstation(PHIAL_CATEGORY, staffStack);
            registry.addWorkstation(EDIFY_CATEGORY, staffStack);
        }

        Level level = Minecraft.getInstance().level;
        if (level != null) {
            for (var holder : level.getRecipeManager().getAllRecipesFor(HexRecipeStuffRegistry.BRAINSWEEP_TYPE)) {
                registry.addRecipe(new BrainsweepEmiRecipe(holder.id(), holder.value()));
            }
        }

        if (PhialRecipeStackBuilder.shouldAddRecipe()) {
            registry.addRecipe(new PhialEmiRecipe(modLoc("craft_phial/recipe"), OpMakeBattery.INSTANCE));
        }

        registry.addRecipe(new EdifyEmiRecipe(modLoc("edify_tree/recipe"), OpEdifySapling.INSTANCE));
    }
}