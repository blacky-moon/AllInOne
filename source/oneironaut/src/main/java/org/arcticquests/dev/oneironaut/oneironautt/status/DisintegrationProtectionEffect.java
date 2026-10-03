package org.arcticquests.dev.oneironaut.oneironautt.status;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautMiscRegistry;

public class DisintegrationProtectionEffect extends MobEffect {

    public DisintegrationProtectionEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x8d6acc);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        entity.removeEffect(OneironautMiscRegistry.DISINTEGRATION.getDelegate());
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }

    @Override
    public void onEffectStarted(LivingEntity entity, int amplifier) {
        super.onEffectStarted(entity, amplifier);
        entity.removeEffect(OneironautMiscRegistry.DISINTEGRATION.getDelegate());
    }
}