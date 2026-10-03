package me.nanorasmus.nanodev.hex_js.kubejs.customIotas;

import at.petrak.hexcasting.api.casting.iota.Iota;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class CustomIotaEntry {
    // Basic
    public String identifier;

    // Constructor
    public Consumer<CustomIota> init;

    // Comparisons
    public Function<CustomIota, Boolean> isTruthy = (CustomIota iota) -> true;
    public BiFunction<CustomIota, Iota, Boolean> toleratesOther = (CustomIota me, Iota other) -> false;

    // Serialization
    public Function<CustomIota, Tag> serialize = (CustomIota iota) -> iota.persistentData;
    public BiFunction<CustomIota, ServerLevel, CustomIota> deserialize = (CustomIota iota, ServerLevel level) -> iota;

    public CustomIotaEntry(
            @NotNull String identifier,
            @NotNull Consumer<CustomIota> init,
            @Nullable Function<CustomIota, Boolean> isTruthy,
            @Nullable BiFunction<CustomIota, Iota, Boolean> toleratesOther,
            @Nullable Function<CustomIota, Tag> serialize,
            @Nullable BiFunction<CustomIota, ServerLevel, CustomIota> deserialize
    ) {
        this.identifier = identifier;
        this.init = init;

        if (isTruthy != null) this.isTruthy = isTruthy;
        if (toleratesOther != null) this.toleratesOther = toleratesOther;
        if (serialize != null) this.serialize = serialize;
        if (deserialize != null) this.deserialize = deserialize;
    }

    // Boilerplate entry that should only be used if an error has occurred to prevent a crash
    public CustomIotaEntry() {
        identifier = "ERR";
        init = (CustomIota iota) -> {};
    }
}