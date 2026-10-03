package org.blacky_moon.hexverse.common.item;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.PatternIota;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.common.lib.HexDataComponents;
import at.petrak.hexcasting.common.lib.hex.HexActions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record HexHolderDelegate(long maxMedia) implements HexHolder {

    @Override
    public boolean hasHex(ItemStack stack) {
        return stack.has(HexDataComponents.HEX_HOLDER_PATTERNS);
    }

    @Nullable
    @Override
    public List<Iota> getHex(ItemStack stack, ServerLevel level) {
        if (stack.has(HexDataComponents.HEX_HOLDER_PATTERNS)) {
            return stack.get(HexDataComponents.HEX_HOLDER_PATTERNS);
        }
        return null;
    }

    @Override
    public void writeHex(ItemStack stack, List<Iota> patterns, @Nullable FrozenPigment pigment, long media) {
        if (patterns != null && !patterns.isEmpty()) {
            stack.set(HexDataComponents.HEX_HOLDER_PATTERNS, patterns);
        }
        if (pigment != null) {
            stack.set(HexDataComponents.PIGMENT, pigment);
        }
        if (media > 0) {
            stack.set(HexDataComponents.MEDIA, Math.min(media, maxMedia));
        } else {
            stack.set(HexDataComponents.MEDIA, maxMedia);
        }
    }

    @Override
    public void clearHex(ItemStack stack) {
        stack.remove(HexDataComponents.HEX_HOLDER_PATTERNS);
        stack.remove(HexDataComponents.PIGMENT);
        stack.remove(HexDataComponents.MEDIA);
    }

    @Nullable
    @Override
    public FrozenPigment getPigment(ItemStack stack) {
        if (stack.has(HexDataComponents.PIGMENT)) {
            return stack.get(HexDataComponents.PIGMENT);
        }
        return FrozenPigment.DEFAULT.get();
    }

    @Override
    public long getMedia(ItemStack stack) {
        return stack.getOrDefault(HexDataComponents.MEDIA, 0L);
    }

    @Override
    public long getMaxMedia(ItemStack stack) {
        return maxMedia;
    }

    @Override
    public void setMedia(ItemStack stack, long media) {
        if (media > 0) {
            stack.set(HexDataComponents.MEDIA, Math.min(media, maxMedia));
        } else {
            stack.remove(HexDataComponents.MEDIA);
        }
    }

    public void appendHoverText(ItemStack stack, List<Component> tooltip) {
        if (hasHex(stack)) {
            Component evalRune = PatternIota.display(HexActions.EVAL.prototype());
            Component coloredRune = evalRune.copy()
                    .withStyle(style -> style.withColor(0x9B59B6));
            tooltip.add(coloredRune);
        }

    }
}