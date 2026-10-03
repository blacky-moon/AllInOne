package org.arcticquests.dev.oneironaut.oneironautt.mixin;

import at.petrak.hexcasting.api.casting.iota.EntityIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.common.casting.actions.selectors.OpGetEntitiesBy;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautMiscRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ram.talia.moreiotas.common.casting.actions.types.OpGetEntitiesByDyn;

import java.util.Collection;

@Mixin(value = {OpGetEntitiesBy.class, OpGetEntitiesByDyn.class})
public abstract class ZoneDistImmunityMixin {
    @WrapOperation(method = "execute", at = @At(value = "INVOKE", target = "Ljava/util/Collection;add(Ljava/lang/Object;)Z", remap = false), remap = false)
    private boolean ignoreImmune(Collection<Iota> instance, Object o, Operation<Boolean> original){
        if (o instanceof EntityIota e) {
            ServerLevel level = Oneironaut.getCachedServer() != null ? Oneironaut.getCachedServer().overworld() : null;
            if (level != null && e.getEntity(level) instanceof LivingEntity le) {

                if (le.hasEffect(OneironautMiscRegistry.DETECTION_RESISTANCE)) {
                    return false;
                }
            }
        }
        return original.call(instance, o);
    }
}