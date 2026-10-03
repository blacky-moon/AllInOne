package miyucomics.hexcassettes.mixin;

import miyucomics.hexcassettes.PlayerEntityMinterface;
import miyucomics.hexcassettes.data.CassetteState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerEntityMixin implements PlayerEntityMinterface {
    @Unique
    private CassetteState cassetteState = new CassetteState();

    @Inject(method = "tick", at = @At("HEAD"))
    private void runCassettes(CallbackInfo ci) {
        Player player = (Player) (Object) this;
        if (player.level().isClientSide()) return;
        cassetteState.tick((ServerPlayer) player);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    private void readCassetteState(CompoundTag compound, CallbackInfo ci) {
        cassetteState = CassetteState.deserialize(compound.getCompound("cassettes"));
    }

    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    private void writeCassetteState(CompoundTag compound, CallbackInfo ci) {
        compound.put("cassettes", cassetteState.serialize());
    }

    @Override
    public CassetteState getCassetteState() {
        return cassetteState;
    }
}