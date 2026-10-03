package miyucomics.hexical.mixin;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.common.blocks.akashic.BlockAkashicBookshelf;
import at.petrak.hexcasting.common.blocks.akashic.BlockEntityAkashicBookshelf;
import miyucomics.hexical.inits.HexicalSounds;
import miyucomics.hexical.misc.CastingUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockAkashicBookshelf.class)
public class BlockAkashicBookshelfMixin {
    @Inject(method = "useWithoutItem", at = @At("TAIL"))
    private void copyIota(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHit, CallbackInfoReturnable<InteractionResult> cir) {
        if (pLevel.isClientSide || pPlayer.isShiftKeyDown() || !pPlayer.getItemInHand(InteractionHand.OFF_HAND).isEmpty())
            return;

        BlockEntity shelf = pLevel.getBlockEntity(pPos);
        if (!(shelf instanceof BlockEntityAkashicBookshelf akashic))
            return;

        Iota iota = akashic.getIota();
        if (iota == null)
            return;

        if (pPlayer instanceof ServerPlayer serverPlayer) {
            CastingUtils.giveIota(serverPlayer, iota);
            pLevel.playSound(null, pPos, HexicalSounds.INSTANCE.getSUDDEN_REALIZATION().get(), SoundSource.BLOCKS, 1f, 1f);
            pPlayer.swing(InteractionHand.MAIN_HAND, true);
        }
    }
}