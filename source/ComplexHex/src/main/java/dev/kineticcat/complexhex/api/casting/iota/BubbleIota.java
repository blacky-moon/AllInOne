package dev.kineticcat.complexhex.api.casting.iota;

import at.petrak.hexcasting.api.casting.eval.CastResult;
import at.petrak.hexcasting.api.casting.eval.ResolvedPatternType;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BubbleIota extends Iota {
    private final Iota payload;

    public BubbleIota(Iota iota) {
        super(() -> ComplexHexIotaTypes.BUBBLE);
        this.payload = iota;
    }

    @SuppressWarnings("unchecked")
    public <T extends Iota> T getContainedIota() {
        return (T) this.payload;
    }

    @Override
    public boolean isTruthy() {
        return true;
    }

    @Override
    protected boolean toleratesOther(Iota that) {
        return typesMatch(this, that)
                && that instanceof BubbleIota iota
                && tolerates(this.getContainedIota(), iota.getContainedIota());
    }

    public static boolean tolerates(Iota A, Iota B) {
        return Iota.typesMatch(A, B) && Iota.tolerates(A, B);
    }

    @Override
    public int hashCode() {
        return payload.hashCode();
    }

    @Override
    public Component display() {
        return Component.translatable("complexhex.tooltip.bubble_contents", payload.display())
                .withStyle(ChatFormatting.AQUA);
    }

    @Override
    public boolean executable() { return true; }

    @Override
    public Iterable<Iota> subIotas() { return List.of(this.getContainedIota()); }

    // what the fuck lmao
    @Override
    public @NotNull CastResult execute(CastingVM vm, ServerLevel world, SpellContinuation continuation) {
        Iota push = this.getContainedIota();
        List<Iota> stack = new ArrayList<>(vm.getImage().getStack());
        stack.add(push);

        CastingImage image2 = vm.getImage().copy(
                stack,
                vm.getImage().getParenCount(),
                vm.getImage().getParenthesized(),
                vm.getImage().getEscapeNext(),
                vm.getImage().getSimulateNext(),
                vm.getImage().getOpsConsumed() + 1,
                vm.getImage().getUserData()
        );

        return new CastResult(
                this,
                continuation,
                image2,
                List.of(),
                ResolvedPatternType.EVALUATED,
                HexEvalSounds.MUTE
        );
    }

    public static final MapCodec<BubbleIota> CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    IotaType.TYPED_CODEC.fieldOf("content").forGetter(i -> i.payload)
            ).apply(instance, BubbleIota::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BubbleIota> STREAM_CODEC =
            IotaType.TYPED_STREAM_CODEC.map(BubbleIota::new, i -> i.payload);

    public static final IotaType<BubbleIota> TYPE = new IotaType<BubbleIota>() {
        @Override
        public MapCodec<BubbleIota> codec() { return CODEC; }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BubbleIota> streamCodec() { return STREAM_CODEC; }

        @Override
        public int color() { return 0xff_55ffff; }
    };
}