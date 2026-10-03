package miyucomics.hexpose.actions.identifier;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import miyucomics.hexpose.iotas.IdentifierIota;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class OpClassify implements ConstMediaAction {
    public static final OpClassify INSTANCE = new OpClassify();

    private OpClassify() {}

    @Override
    public int getArgc() {
        return 1;
    }

    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment env) {
        Iota arg = args.get(0);
        IotaType<?> type = arg.getType();

        // Ключ реестра IotaType из HexCasting
        ResourceKey<Registry<IotaType<?>>> IOTA_TYPE_KEY =
                ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("hexcasting", "iota_type"));

        var registryAccess = env.getWorld().registryAccess();
        var registry = registryAccess.registry(IOTA_TYPE_KEY);

        if (registry.isEmpty()) {
            return List.of(new IdentifierIota(ResourceLocation.fromNamespaceAndPath("hexcasting", "unknown")));
        }

        // Ищем ключ по типу
        ResourceLocation id = null;
        for (var entry : registry.get().entrySet()) {
            if (entry.getValue().equals(type)) {
                id = entry.getKey().location();
                break;
            }
        }

        if (id == null) {
            id = ResourceLocation.fromNamespaceAndPath("hexcasting", "unknown");
        }

        return List.of(new IdentifierIota(id));
    }
}