package miyucomics.hexpose.actions.instance_data;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.EntityIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.NullIota;
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota;
import miyucomics.hexpose.iotas.IdentifierIota;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.function.Function;

public class OpGetStatusEffectInstanceData implements ConstMediaAction {
    private final Function<MobEffectInstance, List<Iota>> processor;

    public OpGetStatusEffectInstanceData(Function<MobEffectInstance, List<Iota>> processor) {
        this.processor = processor;
    }

    @Override
    public int getArgc() {
        return 2;
    }

    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment env) {
        Iota arg0 = args.get(0);
        if (!(arg0 instanceof EntityIota)) {
            throw MishapInvalidIota.ofType(arg0, 0, "entity");
        }
        var entity = ((EntityIota) arg0).getEntity(env.getWorld());
        if (entity == null) {
            throw MishapInvalidIota.ofType(arg0, 0, "entity");
        }
        env.assertEntityInRange(entity);
        if (!(entity instanceof LivingEntity)) {
            throw MishapInvalidIota.ofType(arg0, 0, "living_entity");
        }

        Iota arg1 = args.get(1);
        if (!(arg1 instanceof IdentifierIota)) {
            throw MishapInvalidIota.ofType(arg1, 1, "identifier");
        }
        ResourceLocation id = ((IdentifierIota) arg1).getIdentifier();

        if (!BuiltInRegistries.MOB_EFFECT.containsKey(id)) {
            throw MishapInvalidIota.of(args.get(1), 0, "status_effect");
        }

        // Получаем Holder<MobEffect>
        var holder = BuiltInRegistries.MOB_EFFECT.getHolder(id);
        if (holder.isEmpty()) {
            throw MishapInvalidIota.of(args.get(1), 0, "status_effect");
        }

        MobEffectInstance instance = ((LivingEntity) entity).getEffect(holder.get());
        if (instance == null) {
            return List.of(new NullIota());
        }
        return processor.apply(instance);
    }
}