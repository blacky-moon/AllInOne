package org.arcticquests.dev.oneironaut.oneironautt.registry;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.common.lib.HexRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut;
import org.arcticquests.dev.oneironaut.oneironautt.casting.iotatypes.DimIota;
import org.arcticquests.dev.oneironaut.oneironautt.casting.iotatypes.SoulprintIota;

import java.util.HashMap;
import java.util.Map;

public class OneironautIotaTypeRegistry {
    public static Map<ResourceLocation, IotaType<?>> TYPES = new HashMap<>();
    public static final IotaType<DimIota> DIM = type("dim", DimIota.TYPE);
    public static final IotaType<SoulprintIota> UUID = type("uuid", SoulprintIota.TYPE);

    public static void init(RegisterEvent event) {
        event.register(HexRegistries.IOTA_TYPE, helper -> {
            for (Map.Entry<ResourceLocation, IotaType<?>> entry : TYPES.entrySet()) {
                helper.register(entry.getKey(), entry.getValue());
            }
        });
    }

    private static <U extends Iota, T extends IotaType<U>> T type(String name, T type) {
        IotaType<?> old = TYPES.put(Oneironaut.id(name), type);
        if (old != null) {
            throw new IllegalArgumentException("Typo? Duplicate id " + name);
        }
        return type;
    }
}