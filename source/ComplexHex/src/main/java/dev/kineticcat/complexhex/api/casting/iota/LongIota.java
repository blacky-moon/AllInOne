package dev.kineticcat.complexhex.api.casting.iota;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Objects;

public class LongIota extends Iota {
    private final long payload;

    public LongIota(long num) {
        super(() -> ComplexHexIotaTypes.LONG);
        this.payload = num;
    }

    public Long getLong() { return payload; }

    @Override
    public boolean isTruthy() {
        return getLong() != 0L;
    }

    @Override
    protected boolean toleratesOther(Iota that) {
        return typesMatch(this, that)
                && that instanceof LongIota iota
                && Objects.equals(getLong(), iota.getLong());
    }

    @Override
    public int hashCode() {
        return Long.hashCode(payload);
    }

    @Override
    public Component display() {
        return display(payload);
    }

    public static Component display(long l) {
        return Component.literal(String.format("%s", l)).withStyle(ChatFormatting.DARK_GREEN);
    }

    public static final MapCodec<LongIota> CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    com.mojang.serialization.Codec.LONG.fieldOf("value").forGetter(i -> i.payload)
            ).apply(instance, LongIota::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, LongIota> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_LONG, i -> i.payload,
                    LongIota::new
            );

    public static final IotaType<LongIota> TYPE = new IotaType<LongIota>() {
        @Override
        public MapCodec<LongIota> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, LongIota> streamCodec() {
            return STREAM_CODEC;
        }

        @Override
        @SuppressWarnings("ConstantConditions")
        public int color() {
            return ChatFormatting.DARK_GREEN.getColor();
        }
    };
}