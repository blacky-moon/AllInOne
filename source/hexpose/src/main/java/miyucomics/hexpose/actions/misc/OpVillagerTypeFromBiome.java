package miyucomics.hexpose.actions.misc;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.NullIota;
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota;
import miyucomics.hexpose.iotas.IdentifierIota;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.level.biome.Biome;

import java.util.List;

public class OpVillagerTypeFromBiome implements ConstMediaAction {
    public static final OpVillagerTypeFromBiome INSTANCE = new OpVillagerTypeFromBiome();

    private OpVillagerTypeFromBiome() {}

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
        ResourceLocation identifier = ((IdentifierIota) arg).getIdentifier();

        // Получаем Holder<Biome> из реестра
        var biomeRegistry = env.getWorld().registryAccess().registry(Registries.BIOME);
        if (biomeRegistry.isEmpty()) {
            return List.of(new NullIota());
        }

        ResourceKey<Biome> biomeKey = ResourceKey.create(Registries.BIOME, identifier);
        Holder<Biome> biomeHolder = biomeRegistry.get().getHolder(biomeKey).orElse(null);
        if (biomeHolder == null) {
            return List.of(new NullIota());
        }

        VillagerType type = VillagerType.byBiome(biomeHolder);
        ResourceLocation id = BuiltInRegistries.VILLAGER_TYPE.getKey(type);
        return List.of(new IdentifierIota(id));
    }
}