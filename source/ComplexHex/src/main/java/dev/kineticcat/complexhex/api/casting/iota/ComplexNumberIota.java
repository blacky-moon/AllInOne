package dev.kineticcat.complexhex.api.casting.iota;

import at.petrak.hexcasting.api.casting.iota.DoubleIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.kineticcat.complexhex.stuff.ComplexNumber;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class ComplexNumberIota extends Iota {
    private final ComplexNumber payload;

    public ComplexNumberIota(ComplexNumber comp) {
        super(() -> ComplexHexIotaTypes.COMPLEXNUMBER);
        this.payload = comp;
    }

    public ComplexNumber getComplex() {
        return ComplexNumber.fixNaN(this.payload);
    }

    @Override
    public boolean isTruthy() {
        return !(this.getComplex().real == 0 && this.getComplex().imag == 0);
    }

    @Override
    protected boolean toleratesOther(Iota that) {
        return typesMatch(this, that)
                && that instanceof ComplexNumberIota iota
                && tolerates(this.getComplex(), iota.getComplex());
    }

    public static boolean tolerates(ComplexNumber cnA, ComplexNumber cnB) {
        return DoubleIota.tolerates(cnA.real, cnB.real) && DoubleIota.tolerates(cnA.imag, cnB.imag);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(payload.real, payload.imag);
    }

    @Override
    public Component display() {
        return display(getComplex());
    }

    private static final ChatFormatting ComplexNumberColour = ChatFormatting.DARK_RED;

    public static Component display(ComplexNumber cn) {
        if (cn.real == 0) return Component.literal(String.format("%.2f", cn.imag) + "i").withStyle(ComplexNumberColour);
        else if (cn.imag >= 0) return Component.literal(String.format("%.2f", cn.real) + " + " + String.format("%.2f", cn.imag) + "i").withStyle(ComplexNumberColour);
        else return Component.literal(String.format("%.2f", cn.real) + " - " + String.format("%.2f", Math.abs(cn.imag)) + "i").withStyle(ComplexNumberColour);
    }

    public static final MapCodec<ComplexNumberIota> CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.DOUBLE.fieldOf("real").forGetter(i -> i.payload.real),
                    Codec.DOUBLE.fieldOf("imag").forGetter(i -> i.payload.imag)
            ).apply(instance, (real, imag) -> new ComplexNumberIota(new ComplexNumber(real, imag))));

    public static final StreamCodec<RegistryFriendlyByteBuf, ComplexNumberIota> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.DOUBLE, i -> i.payload.real,
                    ByteBufCodecs.DOUBLE, i -> i.payload.imag,
                    (real, imag) -> new ComplexNumberIota(new ComplexNumber(real, imag))
            );

    public static final IotaType<ComplexNumberIota> TYPE = new IotaType<ComplexNumberIota>() {
        @Override
        public MapCodec<ComplexNumberIota> codec() { return CODEC; }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ComplexNumberIota> streamCodec() { return STREAM_CODEC; }

        @Override
        public int color() { return 0xff_fc0522; }
    };
}