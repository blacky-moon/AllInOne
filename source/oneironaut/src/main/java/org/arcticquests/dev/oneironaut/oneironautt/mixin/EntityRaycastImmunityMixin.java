package org.arcticquests.dev.oneironaut.oneironautt.mixin;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.common.casting.actions.raycast.OpEntityRaycast;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautMiscRegistry;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = OpEntityRaycast.class)
public abstract class EntityRaycastImmunityMixin {
    @ModifyVariable(method = "execute", at = @At(value = "STORE", remap = false), remap = false)
    private EntityHitResult nullIfBlocker(
            EntityHitResult value,
            @Local CastingEnvironment env,
            @Local(ordinal = 0) Vec3 origin,
            @Local(ordinal = 1) Vec3 look,
            @Local(ordinal = 2) Vec3 end){
        if (value != null){
            int stepResolution = 16;
            double totalSteps = origin.distanceTo(value.getLocation()) * stepResolution;
            Vec3 step = look.scale(1.0 / stepResolution);

            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

            for (int i = 0; i < totalSteps; i++) {
                Vec3 currentVec = origin.add(step.scale(i));
                mutablePos.set(currentVec.x, currentVec.y, currentVec.z);

                if (env.getWorld().getBlockState(mutablePos).is(OneironautTags.Blocks.blocksRaycast)) {
                    return null;
                }
            }
        }
        return value;
    }

    @ModifyReturnValue(method = "execute$lambda$0", at = @At(value = "RETURN", remap = false), remap = false)
    private static boolean skipImmune(boolean original, @Local(argsOnly = true) Entity it){
        if (it instanceof LivingEntity living){
            return !living.hasEffect(OneironautMiscRegistry.DETECTION_RESISTANCE);
        }
        return true;
    }
}