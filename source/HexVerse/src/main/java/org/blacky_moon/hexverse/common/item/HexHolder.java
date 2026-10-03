package org.blacky_moon.hexverse.common.item;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface HexHolder {

    boolean hasHex(ItemStack stack);

    @Nullable List<Iota> getHex(ItemStack stack, ServerLevel level);

    void writeHex(ItemStack stack, List<Iota> patterns, @Nullable FrozenPigment pigment, long media);

    void clearHex(ItemStack stack);

    @Nullable FrozenPigment getPigment(ItemStack stack);

    long getMedia(ItemStack stack);

    long getMaxMedia(ItemStack stack);

    void setMedia(ItemStack stack, long media);
}