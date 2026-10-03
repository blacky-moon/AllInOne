package org.arcticquests.dev.oneironaut.oneironautt.block;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut;

import java.util.function.Supplier;

public class ThoughtSlurryBlock extends LiquidBlock {
    public static final ResourceLocation ID =
            ResourceLocation.fromNamespaceAndPath(Oneironaut.MODID, "thought_slurry");

    public static final Properties SETTINGS =
            Properties.ofFullCopy(Blocks.WATER).noOcclusion().mapColor(MapColor.COLOR_PURPLE);

    public ThoughtSlurryBlock(Supplier<? extends FlowingFluid> fluidSupplier, Properties properties) {
        super(fluidSupplier.get(), SETTINGS);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
        return true;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }
}