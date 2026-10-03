package miyucomics.hexpose.iotas;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota;
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

import java.util.List;

public class IdentifierIota extends Iota {
    private final ResourceLocation identifier;

    public IdentifierIota(ResourceLocation identifier) {
        super(() -> TYPE);
        this.identifier = identifier;
    }

    public ResourceLocation getIdentifier() {
        return identifier;
    }

    @Override
    public boolean isTruthy() {
        return true;
    }

    @Override
    protected boolean toleratesOther(Iota that) {
        return typesMatch(this, that) && that instanceof IdentifierIota && this.identifier.equals(((IdentifierIota) that).identifier);
    }

    public Tag serialize() {
        CompoundTag compound = new CompoundTag();
        compound.putString("namespace", identifier.getNamespace());
        compound.putString("path", identifier.getPath());
        return compound;
    }

    @Override
    public Component display() {
        return Component.literal(identifier.getNamespace() + ":" + identifier.getPath())
                .withStyle(ChatFormatting.GOLD);
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    public static final IotaType<IdentifierIota> TYPE = new IotaType<IdentifierIota>() {
        private static final MapCodec<IdentifierIota> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        ResourceLocation.CODEC.fieldOf("identifier").forGetter(IdentifierIota::getIdentifier)
                ).apply(instance, IdentifierIota::new)
        );

        private static final StreamCodec<RegistryFriendlyByteBuf, IdentifierIota> STREAM_CODEC =
                StreamCodec.composite(
                        ResourceLocation.STREAM_CODEC,
                        IdentifierIota::getIdentifier,
                        IdentifierIota::new
                );

        @Override
        public MapCodec<IdentifierIota> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, IdentifierIota> streamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public int color() {
            return 0xff_e6c24c;
        }

        public Component display(Tag tag) {
            CompoundTag compound = (CompoundTag) tag;
            return Component.literal(compound.getString("namespace") + ":" + compound.getString("path"))
                    .withStyle(ChatFormatting.GOLD);
        }

        public IdentifierIota deserialize(Tag tag, ServerLevel world) {
            CompoundTag compound = (CompoundTag) tag;
            String namespace = compound.getString("namespace");
            String path = compound.getString("path");
            return new IdentifierIota(ResourceLocation.fromNamespaceAndPath(namespace, path));
        }
    };

    public static List<Iota> asActionResult(ResourceLocation identifier) {
        return List.of(new IdentifierIota(identifier));
    }

    public static ResourceLocation getIdentifier(List<? extends Iota> list, int idx, int argc) {
        if (idx >= list.size()) {
            throw new MishapNotEnoughArgs(idx + 1, list.size());
        }
        Iota x = list.get(idx);
        if (x instanceof IdentifierIota) {
            return ((IdentifierIota) x).getIdentifier();
        }
        throw MishapInvalidIota.ofType(x, argc == 0 ? idx : argc - (idx + 1), "identifier");
    }

    public static ResourceLocation getIdentifier(List<? extends Iota> list, int idx) {
        return getIdentifier(list, idx, 0);
    }
}