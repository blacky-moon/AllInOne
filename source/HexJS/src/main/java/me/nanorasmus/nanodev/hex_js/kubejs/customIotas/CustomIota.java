package me.nanorasmus.nanodev.hex_js.kubejs.customIotas;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import com.mojang.serialization.MapCodec;
import dev.latvian.mods.rhino.util.HideFromJS;
import me.nanorasmus.nanodev.hex_js.HexJS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import static me.nanorasmus.nanodev.hex_js.kubejs.customIotas.CustomIotaRegistry.customIotas;

public class CustomIota extends Iota {
    public final String type;

    @HideFromJS
    private CustomIotaEntry entry;

    public HashMap<String, Object> data = new HashMap<>();
    public CompoundTag persistentData = new CompoundTag();
    public Object[] initParams;

    @HideFromJS
    public CustomIota(@NotNull CompoundTag content) {
        super(() -> TYPE);

        type = content.getString("hex_js:type");
        this.persistentData = content;

        if (customIotas.containsKey(type)) {
            entry = customIotas.get(type);
        } else {
            HexJS.LOGGER.warn("Tried initializing an iota whose type had not been registered!");
            entry = new CustomIotaEntry();
        }
    }

    public CustomIota(@NotNull String type) {
        super(() -> TYPE);

        this.type = type;

        if (customIotas.containsKey(type)) {
            entry = customIotas.get(type);
        } else {
            HexJS.LOGGER.warn("Tried initializing an iota whose type had not been registered!");
            entry = new CustomIotaEntry();
        }

        persistentData.putString("hex_js:type", type);
        initParams = new Object[]{};
        entry.init.accept(this);
    }

    public CustomIota(@NotNull String type, @NotNull Object... params) {
        super(() -> TYPE);

        this.type = type;

        if (customIotas.containsKey(type)) {
            entry = customIotas.get(type);
        } else {
            HexJS.LOGGER.warn("Tried initializing an iota whose type had not been registered!");
            entry = new CustomIotaEntry();
        }

        persistentData.putString("hex_js:type", type);
        initParams = params;
        entry.init.accept(this);
    }

    @Override
    public boolean isTruthy() {
        return entry.isTruthy.apply(this);
    }

    @Override
    protected boolean toleratesOther(Iota that) {
        // BiFunction вместо Function2 (kotlin), поэтому .apply, а не .invoke
        return entry.toleratesOther.apply(this, that);
    }

    @Override
    public Component display() {
        if (this.persistentData.contains("display")) {
            return Component.literal(this.persistentData.getString("display"));
        }
        return Component.literal(this.type);
    }

    @Override
    public int hashCode() {
        return 31 * type.hashCode() + persistentData.hashCode();
    }

    @HideFromJS
    public CompoundTag toTag() {
        var tag = entry.serialize.apply(this);
        return tag instanceof CompoundTag c ? c : persistentData;
    }

    private static final MapCodec<CustomIota> CODEC =
            CompoundTag.CODEC.fieldOf("data").xmap(CustomIota::new, CustomIota::toTag);

    private static final StreamCodec<RegistryFriendlyByteBuf, CustomIota> STREAM_CODEC =
            StreamCodec.of(
                    (buf, iota) -> ByteBufCodecs.COMPOUND_TAG.encode(buf, iota.toTag()),
                    buf -> new CustomIota(ByteBufCodecs.COMPOUND_TAG.decode(buf))
            );

    public static IotaType<CustomIota> TYPE = new IotaType<>() {
        @Override
        public MapCodec<CustomIota> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CustomIota> streamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public int color() {
            return 0xff_ffffff;
        }
    };
}