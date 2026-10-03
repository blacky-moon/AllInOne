package miyucomics.hexpose.actions.misc;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.DoubleIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota;
import miyucomics.hexpose.iotas.IdentifierIota;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

import java.util.List;

public class OpGetStatusEffectCategory implements ConstMediaAction {
    public static final OpGetStatusEffectCategory INSTANCE = new OpGetStatusEffectCategory();

    private OpGetStatusEffectCategory() {}

    @Override
    public int getArgc() {
        return 1;
    }

    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment env) {
        Iota arg = args.get(0);
        if (!(arg instanceof IdentifierIota)) {
            throw MishapInvalidIota.ofType(arg, 0, "identifier");
        }
        ResourceLocation id = ((IdentifierIota) arg).getIdentifier();

        if (!BuiltInRegistries.MOB_EFFECT.containsKey(id)) {
            throw MishapInvalidIota.of(args.get(0), 0, "status_effect_id");
        }
        MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(id);

        MobEffectCategory category = effect.getCategory();
        double value = switch (category) {
            case BENEFICIAL -> 1.0;
            case NEUTRAL -> 0.0;
            case HARMFUL -> -1.0;
        };

        return List.of(new DoubleIota(value));
    }
}