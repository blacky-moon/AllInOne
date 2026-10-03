package org.arcticquests.dev.oneironaut.oneironautt.mixin;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.phys.Vec3;
import org.arcticquests.dev.oneironaut.oneironautt.OneironautConfig;
import org.arcticquests.dev.oneironaut.oneironautt.network.FireballUpdatePacket;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautTags;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("ConstantConditions")
@Mixin(targets = "at.petrak.hexcasting.common.casting.actions.spells.OpAddMotion$Spell")
public abstract class ImpulseRedirectFireballMixin {

    @Final
    @Shadow
    private Vec3 motion;
    @Final
    @Shadow
    private Entity target;

    @Unique
    private static final boolean oneironaut$redirectionEnabled = OneironautConfig.getServer().getImpulseRedirectsFireball();

    @Inject(method = "cast(Lat/petrak/hexcasting/api/casting/eval/CastingEnvironment;)V", at = @At(value = "RETURN", remap = false), remap = false)
    public void redirectFireball(CastingEnvironment env, CallbackInfo ci) {
        if (target instanceof AbstractHurtingProjectile explosive && oneironaut$redirectionEnabled) {
            boolean immune = false;
            if (explosive instanceof WitherSkull skull) {
                if (skull.isDangerous()) {
                    immune = true;
                }
            }
            if (target.getType().is(OneironautTags.Entities.impulseRedirectBlacklist)) {
                immune = true;
            }

            if (!immune) {
                explosive.setOwner(env.getCastingEntity());
            }

            Vec3 oldDelta = explosive.getDeltaMovement();
            Vec3 newDelta = oldDelta.add(motion);

            // Применяем новое движение снаряда
            explosive.setDeltaMovement(newDelta);

            if (!newDelta.equals(oldDelta)) {
                IXplatAbstractions.INSTANCE.sendPacketNear(
                        explosive.position(),
                        128,
                        env.getWorld(),
                        new FireballUpdatePacket(newDelta, explosive.getId())
                );
            }
        }
    }
}