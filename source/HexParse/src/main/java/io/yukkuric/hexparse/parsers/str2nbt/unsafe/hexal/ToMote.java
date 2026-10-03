package io.yukkuric.hexparse.parsers.str2nbt.unsafe.hexal;

import at.petrak.hexcasting.api.casting.iota.Iota;
import io.yukkuric.hexparse.parsers.str2nbt.BaseConstParser;
import ram.talia.hexal.api.casting.iota.MoteIota;
import ram.talia.hexal.api.mediafieditems.MediafiedItemManager;

import java.util.UUID;

public class ToMote {
    public static final BaseConstParser INSTANCE = new BaseConstParser.Prefix("mote_") {
        @Override
        public Iota parse(String node) {
            var raw = node.split("_");

            if (raw.length < 3) {
                throw new IllegalArgumentException("Invalid Mote format: " + node);
            }

            try {
                var uuid = UUID.fromString(raw[1]);
                var idx = Integer.parseInt(raw[2]);

                var index = new MediafiedItemManager.Index(uuid, idx);

                return new MoteIota(index);

            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to parse Mote from: " + node, e);
            }
        }
    };
}