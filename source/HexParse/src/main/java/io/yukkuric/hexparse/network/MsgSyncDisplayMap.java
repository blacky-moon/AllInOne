package io.yukkuric.hexparse.network;

import io.netty.buffer.ByteBuf;
import io.yukkuric.hexparse.HexParse;
import io.yukkuric.hexparse.parsers.hexpattern.DotHexPatternMapper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public record MsgSyncDisplayMap(Map<String, String> map, Map<String, String> prefixMap) implements CustomPacketPayload {
    public static final ResourceLocation ID = HexParse.modLoc("display/sync");

    private static final StreamEncoder<FriendlyByteBuf, String> ENCODE_UTF = FriendlyByteBuf::writeUtf;
    private static final StreamDecoder<FriendlyByteBuf, String> DECODE_UTF = FriendlyByteBuf::readUtf;

    public void serialize(FriendlyByteBuf buf) {
        buf.writeMap(map, ENCODE_UTF, ENCODE_UTF);
        buf.writeMap(prefixMap, ENCODE_UTF, ENCODE_UTF);
    }

    public static MsgSyncDisplayMap deserialize(ByteBuf buffer) {
        var buf = new FriendlyByteBuf(buffer);
        var map = buf.readMap(DECODE_UTF, DECODE_UTF);
        var prefixMap = buf.readMap(DECODE_UTF, DECODE_UTF);
        return new MsgSyncDisplayMap(map, prefixMap);
    }

    public static void handle(MsgSyncDisplayMap self) {
        var MC = Minecraft.getInstance();
        MC.execute(() -> {
            DotHexPatternMapper.receiveRemoteMap(self);
        });
    }

    public static final Type<MsgSyncDisplayMap> TYPE = new Type<>(ID);
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static final StreamCodec<RegistryFriendlyByteBuf, MsgSyncDisplayMap> STREAM_CODEC = new StreamCodec<>() {
        public MsgSyncDisplayMap decode(RegistryFriendlyByteBuf buf) {
            return deserialize(buf);
        }
        public void encode(RegistryFriendlyByteBuf buf, MsgSyncDisplayMap msg) {
            msg.serialize(buf);
        }
    };
}