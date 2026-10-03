package miyucomics.hexpose.iotas;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota;
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;

import java.util.List;
import java.util.function.Function;

public class TextIota extends Iota {
    private final Component text;

    public TextIota(Component text) {
        super(() -> TYPE);
        this.text = text;
    }

    public Component getText() {
        return text;
    }

    @Override
    public boolean isTruthy() {
        return true;
    }

    @Override
    protected boolean toleratesOther(Iota that) {
        return typesMatch(this, that) && that instanceof TextIota && this.text.equals(((TextIota) that).text);
    }

    public Tag serialize() {
        var result = ComponentSerialization.CODEC.encodeStart(NbtOps.INSTANCE, text);
        if (result.isError()) {
            return new CompoundTag();
        }
        Tag serialized = result.getOrThrow();

        if (serialized.toString().length() > 32000) {
            return new CompoundTag();
        }

        CompoundTag tag = new CompoundTag();
        tag.put("text", serialized);
        return tag;
    }

    @Override
    public Component display() {
        return text;
    }

    @Override
    public int hashCode() {
        return text.hashCode();
    }

    public static final IotaType<TextIota> TYPE = new IotaType<TextIota>() {
        private static final MapCodec<TextIota> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        ComponentSerialization.CODEC.fieldOf("text").forGetter(TextIota::getText)
                ).apply(instance, TextIota::new)
        );

        private static final StreamCodec<RegistryFriendlyByteBuf, TextIota> STREAM_CODEC =
                StreamCodec.composite(
                        ComponentSerialization.STREAM_CODEC,
                        TextIota::getText,
                        TextIota::new
                );

        @Override
        public MapCodec<TextIota> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TextIota> streamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public int color() {
            return 0xff_db3f30;
        }

        public Component display(Tag tag) {
            if (!(tag instanceof CompoundTag compound) || !compound.contains("text")) {
                return Component.literal("arimfexendrapuse")
                        .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.OBFUSCATED);
            }

            var result = ComponentSerialization.CODEC.parse(NbtOps.INSTANCE, compound.get("text"));
            if (result.isError()) {
                return Component.literal("arimfexendrapuse")
                        .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.OBFUSCATED);
            }
            return result.getOrThrow();
        }

        public TextIota deserialize(Tag tag, ServerLevel world) {
            if (!(tag instanceof CompoundTag compound) || !compound.contains("text")) {
                return null;
            }

            var result = ComponentSerialization.CODEC.parse(NbtOps.INSTANCE, compound.get("text"));
            if (result.isError()) {
                return null;
            }
            return new TextIota(result.getOrThrow());
        }
    };

    public static List<Iota> asActionResult(Component text) {
        return List.of(new TextIota(text));
    }

    public static Component getText(List<? extends Iota> list, int idx, int argc) {
        if (idx >= list.size()) {
            throw new MishapNotEnoughArgs(idx + 1, list.size());
        }
        Iota x = list.get(idx);
        if (x instanceof TextIota) {
            return ((TextIota) x).getText().copy();
        }
        throw MishapInvalidIota.ofType(x, argc == 0 ? idx : argc - (idx + 1), "text");
    }

    public static Component getText(List<? extends Iota> list, int idx) {
        return getText(list, idx, 0);
    }

    public static Component mapTextStyle(Component text, Function<Style, Style> transformer) {
        Style newStyle = transformer.apply(text.getStyle());
        return text.copy().withStyle(newStyle);
    }
}