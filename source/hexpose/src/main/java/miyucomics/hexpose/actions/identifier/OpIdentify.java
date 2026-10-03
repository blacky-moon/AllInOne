package miyucomics.hexpose.actions.identifier;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.EntityIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.Vec3Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota;
import miyucomics.hexpose.iotas.IdentifierIota;
import miyucomics.hexpose.iotas.ItemStackIota;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class OpIdentify implements ConstMediaAction {
    public static final OpIdentify INSTANCE = new OpIdentify();

    private OpIdentify() {}

    @Override
    public int getArgc() {
        return 1;
    }

    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment env) {
        Iota arg = args.get(0);
        ResourceLocation id;

        if (arg instanceof EntityIota) {
            var entity = ((EntityIota) arg).getEntity(env.getWorld());
            if (entity == null) {
                throw MishapInvalidIota.of(args.get(0), 0, "identifiable");
            }
            id = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
        } else if (arg instanceof ItemStackIota) {
            var stack = ((ItemStackIota) arg).getStack();
            id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        } else if (arg instanceof Vec3Iota) {
            Vec3 vec = ((Vec3Iota) arg).getVec3();
            BlockPos pos = BlockPos.containing(vec);
            env.assertPosInRange(pos);
            id = BuiltInRegistries.BLOCK.getKey(env.getWorld().getBlockState(pos).getBlock());
        } else {
            throw MishapInvalidIota.of(args.get(0), 0, "identifiable");
        }

        if (id == null) {
            throw MishapInvalidIota.of(args.get(0), 0, "identifiable");
        }

        return List.of(new IdentifierIota(id));
    }
}