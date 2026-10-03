package org.arcticquests.dev.oneironaut.oneironautt.block;

import com.mojang.serialization.MapCodec;
import at.petrak.hexcasting.common.blocks.circles.BlockSlate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.arcticquests.dev.oneironaut.oneironautt.block.blockentity.SpaceBombBlockEntity;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautBlockRegistry;
import org.jetbrains.annotations.Nullable;

public class SpaceBombBlock extends BaseEntityBlock {
    public static final MapCodec<SpaceBombBlock> CODEC = simpleCodec(SpaceBombBlock::new);

    public SpaceBombBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.defaultBlockState().setValue(BlockSlate.ENERGIZED, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SpaceBombBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockSlate.ENERGIZED);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, OneironautBlockRegistry.SPACE_BOMB_ENTITY.get(),
                (_world, _pos, _state, _be) -> _be.tick(_world, _pos, _state));
    }
}