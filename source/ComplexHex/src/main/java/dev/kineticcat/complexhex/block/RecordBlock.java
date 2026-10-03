package dev.kineticcat.complexhex.block;

import com.mojang.serialization.MapCodec;
import dev.kineticcat.complexhex.item.ComplexHexItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class RecordBlock extends HorizontalDirectionalBlock {

    // Простой конструктор (Properties) -> simpleCodec подходит.
    public static final MapCodec<RecordBlock> CODEC = simpleCodec(RecordBlock::new);

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    public RecordBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return Block.box(1,0,1,15,1,15);
    }
    @Override
    public VoxelShape getOcclusionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
        return Shapes.empty();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return this.defaultBlockState().setValue(FACING, blockPlaceContext.getHorizontalDirection().getOpposite());
    }

    public static class Quenched extends RecordBlock {
        // Отдельный codec для подкласса — свой конструктор (Quenched::new),
        // иначе datafixer/registry попытается десериализовать через родительский codec
        // и создаст неправильный тип блока.
        public static final MapCodec<Quenched> CODEC = simpleCodec(Quenched::new);

        @Override
        protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
            return CODEC;
        }

        public Quenched(Properties properties) {
            super(properties);
        }
        @Override
        public void onPlace(@NotNull BlockState newState, Level level, @NotNull BlockPos position, @NotNull BlockState OldState, boolean movedByPiston) {
            if (!level.isClientSide) {
                ItemStack record = new ItemStack(ComplexHexItems.QUENCHED_RECORD);
                ItemEntity recordEntity = new ItemEntity(level, position.getX(), position.getY(), position.getZ(), record);
                level.addFreshEntity(recordEntity);
                level.setBlockAndUpdate(position, Blocks.AIR.defaultBlockState());
            }
        }
    }
    public static class Inert extends RecordBlock {
        public static final MapCodec<Inert> CODEC = simpleCodec(Inert::new);

        @Override
        protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
            return CODEC;
        }

        public Inert(Properties properties) {
            super(properties);
        }
    }
}