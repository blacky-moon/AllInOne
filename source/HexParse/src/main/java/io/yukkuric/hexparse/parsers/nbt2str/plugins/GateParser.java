package io.yukkuric.hexparse.parsers.nbt2str.plugins;

import io.yukkuric.hexparse.parsers.IPlayerBinder;
import io.yukkuric.hexparse.parsers.nbt2str.INbt2Str;
import net.minecraft.server.level.ServerPlayer;
import  ram.talia.hexal.api.casting.iota.GateIota;

public class GateParser implements IPlayerBinder, INbt2Str<GateIota> {
    ServerPlayer self;

    @Override
    public void BindPlayer(ServerPlayer p) {
        self = p;
    }

    @Override
    public String parse(GateIota iota) {
        if (iota.isDrifting()) return "gate";

        var target = iota.getTarget();

        if (iota.isLocationAnchored()) {
            var pos = target.getA();
            return "gate_%s_%s_%s".formatted(
                    displayMinimal(pos.x),
                    displayMinimal(pos.y),
                    displayMinimal(pos.z)
            );
        }

        // isEntityAnchored()
        var anchor = target.getB();
        var res = "gate_%s_%s_%s".formatted(
                displayMinimal(anchor.offset().x),
                displayMinimal(anchor.offset().y),
                displayMinimal(anchor.offset().z)
        );
        if (self != null && self.getUUID().equals(anchor.uuid())) res += "_self";
        else res += "_" + anchor.uuid();
        return res;
    }

    @Override
    public Class<GateIota> getType() {
        return GateIota.class;
    }
}