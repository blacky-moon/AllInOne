package org.arcticquests.dev.oneironaut.oneironautt.block;

import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.arcticquests.dev.oneironaut.oneironautt.block.blockentity.WispLanternEntityTinted;

public class WispLanternTinted extends BaseEntityBlock {

    public static final MapCodec<WispLanternTinted> CODEC = simpleCodec(WispLanternTinted::new);

    public WispLanternTinted(Properties settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
    @Override
    protected boolean skipRendering(BlockState state, BlockState adjacentState, Direction side) {
        return adjacentState.is(this) || super.skipRendering(state, adjacentState, side);
    }


    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WispLanternEntityTinted(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext context) {
        VoxelShape glass = Shapes.box(4f / 16, 0f / 16, 4f / 16, 12f / 16, 9f / 16, 12f / 16);
        VoxelShape lid = Shapes.box(5f / 16, 8f / 16, 5f / 16, 11f / 16, 10f / 16, 11f / 16);
        return Shapes.or(glass, lid);
    }

    public void splatPigmentOntoBlock(Level world, BlockPos pos, FrozenPigment pigment) {
        WispLanternEntityTinted be = (WispLanternEntityTinted) (world.getBlockEntity(pos));
        assert be != null;
        be.setColor(pigment.item(), world.getPlayerByUUID(pigment.owner()));
        be.setChanged();
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack clickStack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (IXplatAbstractions.INSTANCE.isPigment(clickStack)) {
            WispLanternEntityTinted be = (WispLanternEntityTinted) world.getBlockEntity(pos);
            if (be != null) {
                be.setColor(clickStack, player);
                be.setChanged();
                return ItemInteractionResult.sidedSuccess(world.isClientSide);
            }
        }
        return ItemInteractionResult.FAIL;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return world.isClientSide ? (_world, _pos, _state, _be) -> {
            if (_be instanceof WispLanternEntityTinted tintedEntity) {
                tintedEntity.tick(_world, _pos, _state);
            }
        } : null;
    }
}