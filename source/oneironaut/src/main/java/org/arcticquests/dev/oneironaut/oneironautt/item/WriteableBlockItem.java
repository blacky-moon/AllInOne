package org.arcticquests.dev.oneironaut.oneironautt.item;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.casting.iota.NullIota;
import at.petrak.hexcasting.api.item.IotaHolderItem;
import at.petrak.hexcasting.common.lib.HexDataComponents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiPredicate;

public class WriteableBlockItem extends BlockItem implements IotaHolderItem {

    public final BiPredicate<Iota, ServerLevel> acceptsIota;
    public static final String TAG_IOTA = "iota";

    public WriteableBlockItem(Block block, Properties settings, BiPredicate<Iota, ServerLevel> acceptsIota) {
        super(block, settings);
        this.acceptsIota = acceptsIota;
    }

    @Override
    public @Nullable Iota readIota(ItemStack stack) {
        // First check standard item component
        if (stack.has(HexDataComponents.IOTA_HOLDER_IOTA)) {
            return stack.get(HexDataComponents.IOTA_HOLDER_IOTA);
        }

        // Fallback: Read from BlockEntity NBT inside custom data component
        CustomData customData = stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY);
        if (!customData.isEmpty()) {
            CompoundTag blockEntityTag = customData.copyTag();
            if (blockEntityTag.contains(TAG_IOTA)) {
                Tag iotaTag = blockEntityTag.get(TAG_IOTA);
                return IotaType.TYPED_CODEC.parse(NbtOps.INSTANCE, iotaTag).result().orElse(null);
            }
        }
        return new NullIota();
    }

    @Override
    public boolean writeable(ItemStack stack) {
        return true;
    }

    @Override
    public boolean canWrite(ItemStack stack, @Nullable Iota iota) {
        return false;
    }

    public boolean canWrite(ItemStack stack, @Nullable Iota iota, ServerLevel level) {
        return this.acceptsIota.test(iota, level);
    }

    @Override
    public void writeDatum(ItemStack stack, @Nullable Iota iota) {
        Iota toWrite = iota != null ? iota : new NullIota();

        // 1. Store in Hex Casting's Item Data Component
        stack.set(HexDataComponents.IOTA_HOLDER_IOTA, toWrite);

        // 2. Store inside BlockEntity custom data so it transfers to the placed block
        Tag serializedIota = IotaType.TYPED_CODEC.encodeStart(NbtOps.INSTANCE, toWrite).getOrThrow();
        CustomData customData = stack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY);

        customData = customData.update(tag -> tag.put(TAG_IOTA, serializedIota));
        stack.set(DataComponents.BLOCK_ENTITY_DATA, customData);
    }
}