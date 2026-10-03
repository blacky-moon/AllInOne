package org.arcticquests.dev.oneironaut.oneironautt.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.arcticquests.dev.oneironaut.oneironautt.block.blockentity.NoosphereGateEntity;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautBlockRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import ram.talia.hexal.common.entities.BaseCastingWisp;

import java.util.Iterator;
import java.util.Map;

@SuppressWarnings("ConstantConditions")
@Mixin(value = BaseCastingWisp.class)
public abstract class MiscWispMixin {

    @Unique
    private final BaseCastingWisp oneironaut$wisp = (BaseCastingWisp) (Object) this;

    @Unique
    private static final Map<ResourceKey<Level>, Map<BlockPos, Vec3>> oneironaut$gateMap = NoosphereGateEntity.gateLocationMap;

    @WrapOperation(method = "deductMedia",
            at = @At(value = "INVOKE",
                    target="Lram/talia/hexal/common/entities/BaseCastingWisp;getNormalCostPerTick()J",
                    remap = false),
            remap = false)
    public long freeIfNoosphereNormal(BaseCastingWisp wisp, Operation<Long> original){
        if (oneironaut$free(wisp)){
            return 0;
        }
        return original.call(wisp);
    }

    @WrapOperation(method = "deductMedia",
            at = @At(value = "INVOKE",
                    target="Lram/talia/hexal/common/entities/BaseCastingWisp;getUntriggeredCostPerTick()J",
                    remap = false),
            remap = false)
    public long freeIfNoosphereSleepy(BaseCastingWisp wisp, Operation<Long> original){
        if (oneironaut$free(wisp)){
            return 0;
        }
        return original.call(wisp);
    }

    @Unique
    private static boolean oneironaut$free(BaseCastingWisp wisp){
        boolean foundGate = false;
        Level world = ((Entity)wisp).getCommandSenderWorld();
        ResourceKey<Level> worldKey = world.dimension();
        String worldName = worldKey.location().toString();

        if (oneironaut$gateMap.containsKey(worldKey) && !(worldName.equals("oneironaut:noosphere"))) {
            Map<BlockPos, Vec3> gatePosMap = oneironaut$gateMap.get(worldKey);
            Iterator<Map.Entry<BlockPos, Vec3>> iterator = gatePosMap.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<BlockPos, Vec3> entry = iterator.next();
                if (((Entity)wisp).position().closerThan(entry.getValue(), 8.0)) {
                    if (world.getBlockState(entry.getKey()).getBlock().equals(OneironautBlockRegistry.NOOSPHERE_GATE.get().defaultBlockState().getBlock())) {
                        foundGate = true;
                    } else {
                        iterator.remove();
                    }
                }
            }
        }

        if (worldName.equals("oneironaut:noosphere")) {
            foundGate = true;
        }

        if (wisp.wispNumContainedPlayers() > 0) {
            foundGate = false;
        }

        return foundGate;
    }
}