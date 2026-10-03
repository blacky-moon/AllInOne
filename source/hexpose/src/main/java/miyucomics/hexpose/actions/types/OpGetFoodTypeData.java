package miyucomics.hexpose.actions.types;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota;
import miyucomics.hexpose.iotas.IdentifierIota;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Function;

public class OpGetFoodTypeData implements ConstMediaAction {
    private final Function<FoodProperties, List<Iota>> processor;

    public OpGetFoodTypeData(Function<FoodProperties, List<Iota>> processor) {
        this.processor = processor;
    }

    @Override
    public int getArgc() {
        return 1;
    }

    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment env) {
        ResourceLocation id = IdentifierIota.getIdentifier(args, 0, getArgc());
        if (!BuiltInRegistries.ITEM.containsKey(id)) {
            throw MishapInvalidIota.of(args.get(0), 0, "food_id");
        }
        Item item = BuiltInRegistries.ITEM.get(id);

        ItemStack stack = new ItemStack(item);
        FoodProperties food = item.getFoodProperties(stack, null);
        if (food == null) {
            throw MishapInvalidIota.of(args.get(0), 0, "food_id");
        }
        return processor.apply(food);
    }
}