package org.arcticquests.dev.oneironaut.oneironautt.item;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.item.IotaHolderItem;
import at.petrak.hexcasting.common.lib.HexDataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.arcticquests.dev.oneironaut.oneironautt.casting.iotatypes.DimIota;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemLibraryCard extends Item implements IotaHolderItem {
    public ItemLibraryCard(Properties settings) {
        super(settings);
    }

    @Nullable
    public ResourceKey<Level> getDimension(ItemStack stack) {
        Iota iota = this.readIota(stack);
        if (iota instanceof DimIota dimIota) {
            return dimIota.getWorldKey();
        }
        return null;
    }

    @Override
    public @Nullable Iota readIota(ItemStack stack) {
        return stack.get(HexDataComponents.IOTA_HOLDER_IOTA);
    }

    @Override
    public boolean writeable(ItemStack stack) {
        return true;
    }

    @Override
    public boolean canWrite(ItemStack stack, @Nullable Iota iota) {
        return iota instanceof DimIota || iota == null;
    }

    @Override
    public void writeDatum(ItemStack stack, @Nullable Iota iota) {
        if (iota == null) {
            stack.remove(HexDataComponents.IOTA_HOLDER_IOTA);
        } else {
            stack.set(HexDataComponents.IOTA_HOLDER_IOTA, iota);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        IotaHolderItem.appendHoverText(this, stack, tooltipComponents, tooltipFlag);
    }
}