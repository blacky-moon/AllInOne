package org.blacky_moon.hexverse.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.pigment.FrozenPigment;

import java.util.List;

public class HexPickaxeItem extends PickaxeItem implements HexHolder {

    private final HexHolderDelegate delegate;

    public HexPickaxeItem(Tier tier, Properties properties) {
        super(tier, properties);
        this.delegate = new HexHolderDelegate(0);
    }

    @Override
    public boolean hasHex(ItemStack stack) {
        return delegate.hasHex(stack);
    }

    @Nullable
    @Override
    public List<Iota> getHex(ItemStack stack, ServerLevel level) {
        return delegate.getHex(stack, level);
    }

    @Override
    public void writeHex(ItemStack stack, List<Iota> patterns, @Nullable FrozenPigment pigment, long media) {
        delegate.writeHex(stack, patterns, pigment, media);
    }

    @Override
    public void clearHex(ItemStack stack) {
        delegate.clearHex(stack);
    }

    @Nullable
    @Override
    public FrozenPigment getPigment(ItemStack stack) {
        return delegate.getPigment(stack);
    }

    @Override
    public long getMedia(ItemStack stack) {
        return delegate.getMedia(stack);
    }

    @Override
    public long getMaxMedia(ItemStack stack) {
        return delegate.getMaxMedia(stack);
    }

    @Override
    public void setMedia(ItemStack stack, long media) {
        delegate.setMedia(stack, media);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        delegate.appendHoverText(stack, tooltip);
    }
}