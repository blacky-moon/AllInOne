package io.yukkuric.hexparse.parsers.str2nbt.unsafe.hexal;

import at.petrak.hexcasting.api.casting.iota.Iota; // Не забудь импортировать Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapOthersName;
import com.mojang.datafixers.util.Either;
import io.yukkuric.hexparse.HexParse;
import io.yukkuric.hexparse.parsers.IPlayerBinder;
import io.yukkuric.hexparse.parsers.str2nbt.BaseConstParser;
import kotlin.Pair;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import ram.talia.hexal.api.casting.iota.GateIota; // Импорт GateIota
import ram.talia.hexal.api.config.HexalConfig;

import java.util.*;

public class ToGate extends BaseConstParser.Regex implements IPlayerBinder {
    public static ToGate INSTANCE = new ToGate();

    private ToGate() {
        super("^gate(_?)");
    }

    ServerPlayer self;
    int orderId;
    Map<Entity, Map<Vec3, Integer>> usedGates = new HashMap<>();
    boolean costPardonedThisTurn = false;

    int getGateOrder(Entity target, Vec3 pos) {
        if (target != null && pos == null) pos = Vec3.ZERO;
        if (!usedGates.containsKey(target)) usedGates.put(target, new HashMap<>());
        var innerMap = usedGates.get(target);
        costPardonedThisTurn = innerMap.containsKey(pos);
        if (!costPardonedThisTurn) innerMap.put(pos, orderId--);
        return innerMap.get(pos);
    }

    @Override
    public int getCost() {
        var res = super.getCost();
        if (!costPardonedThisTurn) res += HexalConfig.Server.MAKE_GATE_COST.get().intValue();
        return res;
    }

    @Override
    public void BindPlayer(ServerPlayer p) {
        self = p;
        orderId = -1;
        usedGates.clear();
    }

    @Override
    public Iota parse(String node) {
        // Если строка короткая (просто "gate"), создаем дрейфующий гейт (target = null)
        if (node.length() <= 5) {
            return new GateIota(getGateOrder(null, null), null);
        }

        var frags = node.substring(5).split("_");
        double[] vecRaw = new double[]{0, 0, 0};
        byte ptr = 0;
        Entity entity = null;

        for (var f : frags) {
            try {
                var tmp = Double.parseDouble(f);
                if (ptr < 3) vecRaw[ptr++] = tmp;
                continue;
            } catch (NumberFormatException e) {
                // Игнорируем, это не число
            }

            var ff = f.toLowerCase();
            if (ff.equals("self") || ff.equals("myself")) {
                entity = self;
                continue;
            }

            try {
                var uuid = UUID.fromString(f);
                entity = ((ServerLevel) self.level()).getEntity(uuid);
                if (entity instanceof Player p && !self.equals(p)) {
                    throw new MishapOthersName(p);
                }
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(HexParse.doTranslate("hexparse.msg.error.unknown_symbol", f));
            } catch (MishapOthersName e) {
                var msg = HexParse.doTranslate("hexcasting.mishap.others_name", entity.getName());
                throw new RuntimeException(msg);
            }
        }

        Vec3 pos = null;
        if (ptr > 0) pos = new Vec3(vecRaw[0], vecRaw[1], vecRaw[2]);

        // Проверка дистанции
        if (entity != null && pos != null) {
            double maxDist = HexalConfig.Server.MAX_GATE_OFFSET.get();
            if (pos.length() > maxDist) {
                var expected = HexParse.doTranslate("hexcasting.mishap.invalid_value.gate.offset", maxDist);
                throw new IllegalArgumentException(HexParse.doTranslate("hexcasting.mishap.invalid_value", "", expected, 0, pos));
            }
        }

        // Формируем target в формате Either<Vec3, Pair<Entity, Vec3>>
        Either<Vec3, Pair<Entity, Vec3>> target = getVec3PairEither(entity, pos);

        return new GateIota(getGateOrder(entity, pos), target);
    }

    private static @Nullable Either<Vec3, Pair<Entity, Vec3>> getVec3PairEither(Entity entity, Vec3 pos) {
        Either<Vec3, Pair<Entity, Vec3>> target;

        if (entity != null) {
            // Если есть сущность, используем Right (Pair)
            // Важно: если pos == null, используем нулевой вектор или позицию сущности
            Vec3 offset = pos != null ? pos : Vec3.ZERO;
            target = Either.right(new Pair<>(entity, offset));
        } else if (pos != null) {
            // Если только позиция, используем Left (Vec3)
            target = Either.left(pos);
        } else {
            // Если ничего нет, это дрейфующий гейт
            target = null;
        }
        return target;
    }
}