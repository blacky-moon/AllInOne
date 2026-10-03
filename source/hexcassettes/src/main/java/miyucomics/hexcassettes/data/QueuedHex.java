package miyucomics.hexcassettes.data;

import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.casting.iota.ListIota;
import com.mojang.serialization.DataResult;
import miyucomics.hexcassettes.CassetteCastEnv;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;

import java.util.ArrayList;
import java.util.List;

public class QueuedHex {
    public final CompoundTag hex;
    public int delay;
    public final int depth;

    public QueuedHex(CompoundTag hex, int delay, int depth) {
        this.hex = hex;
        this.delay = delay;
        this.depth = depth;
    }

    public CompoundTag serialize() {
        CompoundTag compound = new CompoundTag();
        compound.put("hex", hex);
        compound.putInt("delay", delay);
        compound.putInt("depth", depth);
        return compound;
    }

    public void cast(ServerPlayer player, String key) {
        InteractionHand hand = (!player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() && player.getItemInHand(InteractionHand.OFF_HAND).isEmpty())
                ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        CastingVM harness = CastingVM.empty(new CassetteCastEnv(player, hand, key, depth));

        DataResult<Iota> result = IotaType.TYPED_CODEC.parse(NbtOps.INSTANCE, hex);
        Iota hexIota = result.getOrThrow();

        if (hexIota instanceof ListIota listIota) {
            List<Iota> iotas = new ArrayList<>();
            for (Iota iota : listIota.getList()) {
                iotas.add(iota);
            }
            harness.queueExecuteAndWrapIotas(iotas, (ServerLevel) player.level());
        }
    }

    public static QueuedHex deserialize(CompoundTag compound) {
        return new QueuedHex(compound.getCompound("hex"), compound.getInt("delay"), compound.getInt("depth"));
    }
}