package miyucomics.hexical.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import miyucomics.hexical.features.amber_seal.DanglingState;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LevelChunk.class)
public class WorldChunkMixin {
    @WrapOperation(
            method = "setBlockState",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/EntityBlock;newBlockEntity(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/level/block/entity/BlockEntity;")
    )
    private net.minecraft.world.level.block.entity.BlockEntity insertQueueBlockEntity(
            EntityBlock instance,
            BlockPos blockPos,
            BlockState state,
            Operation<net.minecraft.world.level.block.entity.BlockEntity> original
    ) {
        if (DanglingState.queuedBlockEntity != null) {
            var toReturn = DanglingState.queuedBlockEntity;
            DanglingState.queuedBlockEntity = null;
            return toReturn;
        }
        return original.call(instance, blockPos, state);
    }
}