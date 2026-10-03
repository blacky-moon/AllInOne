package dev.tizu.hexcessible.mixin;

import java.util.List;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import at.petrak.hexcasting.client.render.RenderLib;
import dev.tizu.hexcessible.Hexcessible;
import net.minecraft.world.phys.Vec2;

@Mixin(RenderLib.class)
public class RenderLibMixin {

    @WrapMethod(method = "makeZappy", remap = false)
    private static List<Vec2> prefersReducedZappiness(List<Vec2> barePoints,
                                                      Set<Integer> dupIndices, int hops, float variance, float speed,
                                                      float flowIrregular, float readabilityOffset, float lastSegLenProp,
                                                      double seed, Operation<List<Vec2>> original) {
        if (Hexcessible.cfg().prefersReducedMotion)
            return barePoints;
        return original.call(barePoints, dupIndices, hops, variance, speed,
                flowIrregular, readabilityOffset, lastSegLenProp, seed);
    }

}