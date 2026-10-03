package miyucomics.hexcassettes.inits;

import miyucomics.hexcassettes.HexcassettesMain;
import miyucomics.hexcassettes.PlayerEntityMinterface;
import miyucomics.hexcassettes.client.ClientStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

import java.util.List;

public class HexcassettesNetworking {

    public static void init(IEventBus modEventBus) {
        modEventBus.addListener(RegisterPayloadHandlersEvent.class, event -> {
            var registrar = event.registrar("1");

            registrar.playToClient(
                    SyncCassettesPayload.TYPE,
                    SyncCassettesPayload.STREAM_CODEC,
                    (payload, context) -> context.enqueueWork(() -> {
                        ClientStorage.ownedCassettes = payload.owned();
                        ClientStorage.activeCassettes.clear();
                        if (Minecraft.getInstance().level != null) {
                            var provider = Minecraft.getInstance().level.registryAccess();
                            for (String key : payload.cassettes()) {
                                ClientStorage.activeCassettes.add(HexcassettesMain.deserializeKey(key, provider));
                            }
                        }
                    })
            );

            registrar.playToServer(
                    CassetteRemovePayload.TYPE,
                    CassetteRemovePayload.STREAM_CODEC,
                    (payload, context) -> context.enqueueWork(() -> {
                        if (context.player() instanceof PlayerEntityMinterface minterface) {
                            minterface.getCassetteState().hexes.remove(payload.pattern());
                        }
                    })
            );
        });
    }

    public record CassetteRemovePayload(String pattern) implements CustomPacketPayload {
        public static final Type<CassetteRemovePayload> TYPE = new Type<>(HexcassettesMain.id("cassette_remove"));
        public static final StreamCodec<RegistryFriendlyByteBuf, CassetteRemovePayload> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, CassetteRemovePayload::pattern,
                CassetteRemovePayload::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record SyncCassettesPayload(int owned, List<String> cassettes) implements CustomPacketPayload {
        public static final Type<SyncCassettesPayload> TYPE = new Type<>(HexcassettesMain.id("sync_cassettes"));
        public static final StreamCodec<RegistryFriendlyByteBuf, SyncCassettesPayload> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, SyncCassettesPayload::owned,
                ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), SyncCassettesPayload::cassettes,
                SyncCassettesPayload::new
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}