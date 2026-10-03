package dev.kineticcat.complexhex.api.casting.iota;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.kineticcat.complexhex.util.ExprDeSer;
import dev.kineticcat.complexhex.api.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class ExprIota extends Iota {
    private final Expr payload;

    public ExprIota(@NotNull Expr expr) {
        super(() -> ComplexHexIotaTypes.EXPR);
        this.payload = expr;
    }

    public Expr expr() { return payload; }

    @Override
    public boolean isTruthy() {
        return true;
    }

    @Override
    protected boolean toleratesOther(Iota that) {
        return that instanceof ExprIota exprIota && exprIota.expr().equals(expr());
    }

    @Override
    public int hashCode() {
        return payload.hashCode();
    }

    @Override
    public Component display() {
        return Component.literal(expr().toString()).withStyle(ChatFormatting.GREEN);
    }

    public static final MapCodec<ExprIota> CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    CompoundTag.CODEC.fieldOf("expr").forGetter(i -> ExprDeSer.serialise(i.payload))
            ).apply(instance, tag -> new ExprIota(ExprDeSer.deserialise(tag).simp())));

    public static final StreamCodec<RegistryFriendlyByteBuf, ExprIota> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.COMPOUND_TAG, i -> ExprDeSer.serialise(i.payload),
                    tag -> new ExprIota(ExprDeSer.deserialise(tag).simp())
            );

    public static final IotaType<ExprIota> TYPE = new IotaType<ExprIota>() {
        @Override
        public MapCodec<ExprIota> codec() { return CODEC; }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ExprIota> streamCodec() { return STREAM_CODEC; }

        @Override
        public int color() { return 0xff_00ff00; }
    };
}