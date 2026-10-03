package dev.kineticcat.complexhex.api.casting.iota;

import at.petrak.hexcasting.api.casting.iota.DoubleIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.kineticcat.complexhex.stuff.Quaternion;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class QuaternionIota extends Iota {
    private final Quaternion payload;

    public QuaternionIota(Quaternion A) {
        super(() -> ComplexHexIotaTypes.QUATERNION);
        this.payload = A;
    }

    public Quaternion getQuaternion() { return payload.fixNaN(); }

    @Override
    public boolean isTruthy() {
        Quaternion Q = this.getQuaternion();
        return !(Q.w == 0 && Q.x == 0 && Q.y == 0 && Q.z == 0);
    }

    @Override
    protected boolean toleratesOther(Iota that) {
        return typesMatch(this, that)
                && that instanceof QuaternionIota iota
                && tolerates(this.getQuaternion(), iota.getQuaternion());
    }

    private boolean tolerates(Quaternion QA, Quaternion QB) {
        return DoubleIota.tolerates(QA.w, QB.w)
                && DoubleIota.tolerates(QA.x, QB.x)
                && DoubleIota.tolerates(QA.y, QB.y)
                && DoubleIota.tolerates(QA.z, QB.z);
    }

    @Override
    public int hashCode() {
        Quaternion Q = getQuaternion();
        return java.util.Objects.hash(Q.w, Q.x, Q.y, Q.z);
    }

    @Override
    public Component display() {
        return display(getQuaternion());
    }

    public static final ChatFormatting QuaternionColour = ChatFormatting.DARK_BLUE;

    public static Component display(Quaternion Q) {
        String text = "";
        text += Q.w == 0 ? "" : String.format("%.2f", Q.w);
        text += text.equals("") ? (Q.x == 0 ? "" : String.format("%.2f", Q.x) + "i")
                : (Q.x == 0 ? "" : " " + (Q.x < 0 ? "-" : "+") + " " + String.format("%.2f", Math.abs(Q.x)) + "i");
        text += text.equals("") ? (Q.y == 0 ? "" : String.format("%.2f", Q.y) + "j")
                : (Q.y == 0 ? "" : " " + (Q.y < 0 ? "-" : "+") + " " + String.format("%.2f", Math.abs(Q.y)) + "j");
        text += text.equals("") ? (Q.z == 0 ? "" : String.format("%.2f", Q.z) + "k")
                : (Q.z == 0 ? "" : " " + (Q.z < 0 ? "-" : "+") + " " + String.format("%.2f", Math.abs(Q.z)) + "k");
        text = text.equals("") ? String.format("%.2f", 0.0d) : text;
        return Component.literal(text).withStyle(QuaternionColour);
    }

    public static final MapCodec<QuaternionIota> CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.DOUBLE.fieldOf("w").forGetter(i -> i.payload.w),
                    Codec.DOUBLE.fieldOf("x").forGetter(i -> i.payload.x),
                    Codec.DOUBLE.fieldOf("y").forGetter(i -> i.payload.y),
                    Codec.DOUBLE.fieldOf("z").forGetter(i -> i.payload.z)
            ).apply(instance, (w, x, y, z) -> new QuaternionIota(new Quaternion(w, x, y, z))));

    public static final StreamCodec<RegistryFriendlyByteBuf, QuaternionIota> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.DOUBLE, i -> i.payload.w,
                    ByteBufCodecs.DOUBLE, i -> i.payload.x,
                    ByteBufCodecs.DOUBLE, i -> i.payload.y,
                    ByteBufCodecs.DOUBLE, i -> i.payload.z,
                    (w, x, y, z) -> new QuaternionIota(new Quaternion(w, x, y, z))
            );

    public static final IotaType<QuaternionIota> TYPE = new IotaType<QuaternionIota>() {
        @Override
        public MapCodec<QuaternionIota> codec() { return CODEC; }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, QuaternionIota> streamCodec() { return STREAM_CODEC; }

        @Override
        public int color() { return ChatFormatting.DARK_BLUE.getColor(); }
    };
}