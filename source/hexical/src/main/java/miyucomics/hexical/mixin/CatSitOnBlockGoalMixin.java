package miyucomics.hexical.mixin;

import miyucomics.hexical.inits.HexicalBlocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.entity.ai.goal.CatSitOnBlockGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CatSitOnBlockGoal.class)
public class CatSitOnBlockGoalMixin {
    @Inject(
            method = "isValidTarget",
            at = @At("RETURN"),
            cancellable = true
    )
    private void sits(LevelReader world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) return;
        if (!world.isEmptyBlock(pos.above())) {
            cir.setReturnValue(false);
            return;
        }
        BlockState state = world.getBlockState(pos);
        if (state.is(HexicalBlocks.SENTINEL_BED_BLOCK.get()) &&
                state.getValue(BlockStateProperties.FACING) == Direction.UP) {
            cir.setReturnValue(true);
        }
    }
}