package dev.kineticcat.complexhex.item;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.ListIota;
import at.petrak.hexcasting.api.item.IotaHolderItem;
import at.petrak.hexcasting.common.lib.HexDataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RecordBlockItem extends BlockItem {

    public RecordBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    public static class Inert extends RecordBlockItem {
        public Inert(Block block, Properties properties) {
            super(block, properties);
        }

        @Override
        public @NotNull String getDescriptionId() {
            return "item.complexhex.inert_record";
        }
    }

    // IotaHolderItem в новой версии полностью основан на DataComponents:
    // readIota(stack) уже реализован в интерфейсе по умолчанию через
    // stack.get(HexDataComponents.IOTA_HOLDER_IOTA), так что нам нужно
    // только реализовать writeDatum/writeable/canWrite поверх того же компонента.
    public static class Quenched extends RecordBlockItem implements IotaHolderItem {

        public Quenched(Block block, Properties properties) {
            super(block, properties);
        }

        @Override
        public boolean canPlace(@NotNull BlockPlaceContext blockPlaceContext, @NotNull BlockState blockState) { return false; }

        @Override
        public @NotNull String getDescriptionId() {
            return "item.complexhex.quenched_record";
        }

        @Override
        public boolean writeable(ItemStack stack) { return true; }

        @Override
        public boolean canWrite(ItemStack stack, @Nullable Iota datum) {
            return datum instanceof ListIota;
        }

        @Override
        public void writeDatum(ItemStack stack, @Nullable Iota datum) {
            if (datum == null) {
                stack.remove(HexDataComponents.IOTA_HOLDER_IOTA);
            } else {
                stack.set(HexDataComponents.IOTA_HOLDER_IOTA, datum);
            }
        }

        @Override
        public void appendHoverText(ItemStack pStack, Item.TooltipContext pContext, List<Component> pTooltipComponents,
                                    TooltipFlag pIsAdvanced) {
            IotaHolderItem.appendHoverText(this, pStack, pTooltipComponents, pIsAdvanced);
        }
    }
}