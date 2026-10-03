package miyucomics.hexpose.actions.types;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota;
import miyucomics.hexpose.iotas.IdentifierIota;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.function.Function;

public class OpGetBlockTypeData implements ConstMediaAction {
    private final Function<Block, List<Iota>> processor;

    public OpGetBlockTypeData(Function<Block, List<Iota>> processor) {
        this.processor = processor;
    }

    @Override
    public int getArgc() {
        return 1;
    }

    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment env) {
        ResourceLocation id = IdentifierIota.getIdentifier(args, 0, getArgc());
        if (!BuiltInRegistries.BLOCK.containsKey(id)) {
            throw MishapInvalidIota.of(args.get(0), 0, "block_id");
        }
        Block block = BuiltInRegistries.BLOCK.get(id);
        return processor.apply(block);
    }
}