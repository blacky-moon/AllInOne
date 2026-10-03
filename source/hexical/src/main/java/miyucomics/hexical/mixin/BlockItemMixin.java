package miyucomics.hexical.mixin;

import miyucomics.hexical.features.amber_seal.AmberSealBlockEntity;
import miyucomics.hexical.inits.HexicalBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.component.CustomData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class BlockItemMixin {
    @Inject(method = "setBlockEntityData", at = @At("RETURN"))
    private static void stripPedestalUUID(ItemStack stack, BlockEntityType<?> blockEntityType, CompoundTag tag, CallbackInfo ci) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            CompoundTag beTag = customData.copyTag();
            if (beTag.contains("BlockEntityTag")) {
                CompoundTag blockEntityTag = beTag.getCompound("BlockEntityTag");
                if (blockEntityTag.contains("persistent_uuid")) {
                    blockEntityTag.remove("persistent_uuid");
                    beTag.put("BlockEntityTag", blockEntityTag);
                    stack.set(DataComponents.CUSTOM_DATA, CustomData.of(beTag));
                }
            }
        }
    }

    @Inject(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;setPlacedBy(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;)V"))
    private void rotateAmberSeal(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> cir) {
        BlockItem blockItem = (BlockItem) (Object) this;
        if (blockItem.getBlock() != HexicalBlocks.AMBER_SEAL_BLOCK.get()) return;
        if (context.getLevel().getBlockEntity(context.getClickedPos()) instanceof AmberSealBlockEntity seal)
            seal.onPlaced(context);
    }
}