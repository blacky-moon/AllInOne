package miyucomics.hexical.mixin;

import miyucomics.hexical.features.player.PlayerEntityMinterface;
import miyucomics.hexical.features.player.PlayerManager;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
@SuppressWarnings("AddedMixinMembersNamePattern")
public class PlayerEntityMixin implements PlayerEntityMinterface {
    @Unique
    private final PlayerManager hexicalPlayerManager = new PlayerManager();

    @Inject(method = "tick", at = @At("TAIL"))
    private void tick(CallbackInfo ci) {
        hexicalPlayerManager.tick((Player) (Object) this);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    private void readPlayerData(CompoundTag compound, CallbackInfo ci) {
        // Используем RegistryAccess.EMPTY для пустого доступа к реестрам
        hexicalPlayerManager.readNbt(compound, RegistryAccess.EMPTY);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    private void writePlayerData(CompoundTag compound, CallbackInfo ci) {
        hexicalPlayerManager.writeNbt(compound, RegistryAccess.EMPTY);
    }

    @Override
    public @NotNull PlayerManager getPlayerManager() {
        return hexicalPlayerManager;
    }
}