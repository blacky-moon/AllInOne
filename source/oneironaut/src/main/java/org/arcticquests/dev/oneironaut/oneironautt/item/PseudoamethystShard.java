package org.arcticquests.dev.oneironaut.oneironautt.item;

import at.petrak.hexcasting.api.misc.MediaConstants;
import net.minecraft.world.item.Item;

public class PseudoamethystShard extends Item {

    public static final int PSEUDOSHARD_UNIT = (int) (MediaConstants.SHARD_UNIT * 1.5);

    public PseudoamethystShard(Properties settings) {
        super(settings);
    }
}