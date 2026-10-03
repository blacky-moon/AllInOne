package miyucomics.hexcassettes.data;

import miyucomics.hexcassettes.HexcassettesMain;
import miyucomics.hexcassettes.inits.HexcassettesNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.server.level.ServerPlayer;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CassetteState {
    public int owned = 0;
    public final Map<String, QueuedHex> hexes = new ConcurrentHashMap<>();
    private Set<String> previouslyActiveSlots = Set.of();

    public void sync(ServerPlayer player) {
        HexcassettesNetworking.SyncCassettesPayload payload = new HexcassettesNetworking.SyncCassettesPayload(
                owned, hexes.keySet().stream().toList()
        );
        player.connection.send(new ClientboundCustomPayloadPacket(payload));
    }

    public void tick(ServerPlayer player) {
        for (QueuedHex hex : hexes.values()) {
            hex.delay -= 1;
        }

        Iterator<Map.Entry<String, QueuedHex>> iterator = hexes.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, QueuedHex> entry = iterator.next();
            if (entry.getValue().delay <= 0) {
                String pattern = entry.getKey();
                QueuedHex hex = entry.getValue();
                iterator.remove();
                hex.cast(player, pattern);
            }
        }

        Set<String> activeIndices = hexes.keySet();
        if (!previouslyActiveSlots.equals(activeIndices)) {
            sync(player);
        }
        previouslyActiveSlots = Set.copyOf(activeIndices);
    }

    public CompoundTag serialize() {
        CompoundTag compound = new CompoundTag();
        compound.putInt("owned", owned);
        CompoundTag serialized = new CompoundTag();
        for (Map.Entry<String, QueuedHex> entry : hexes.entrySet()) {
            serialized.put(entry.getKey(), entry.getValue().serialize());
        }
        compound.put("hexes", serialized);
        return compound;
    }

    public static CassetteState deserialize(CompoundTag compound) {
        CassetteState state = new CassetteState();
        state.owned = compound.getInt("owned");
        CompoundTag hexesTag = compound.getCompound("hexes");
        for (String key : hexesTag.getAllKeys()) {
            state.hexes.put(key, QueuedHex.deserialize(hexesTag.getCompound(key)));
        }
        return state;
    }
}