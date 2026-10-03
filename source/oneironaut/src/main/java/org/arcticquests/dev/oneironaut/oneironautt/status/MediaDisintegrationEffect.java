package org.arcticquests.dev.oneironaut.oneironautt.status;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut;
import org.arcticquests.dev.oneironaut.oneironautt.event.OvercastEnchantmentEvents;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautMiscRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MediaDisintegrationEffect extends MobEffect {
    public static final ResourceLocation ATTRIBUTE_ID = ResourceLocation.fromNamespaceAndPath(Oneironaut.MODID, "disintegration");

    public MediaDisintegrationEffect() {
        super(MobEffectCategory.HARMFUL, 0x8f6b94);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 30 == 0;
    }

    public final Map<LivingEntity, UUID> modifierData = new HashMap<>();

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity instanceof Player player && (player.isCreative() || player.isSpectator())) {
            return true;
        }
        entity.hurt(entity.damageSources().genericKill(), 0.00001f);
        entity.heal(0.00001f);
        AttributeInstance instance = entity.getAttribute(Attributes.MAX_HEALTH);

        if (instance != null) {
            AttributeModifier currentMod = instance.getModifier(ATTRIBUTE_ID);
            if (currentMod != null) {
                double oldValue = instance.getValue();
                instance.removeModifier(ATTRIBUTE_ID);
                double oldModValue = Math.abs(currentMod.amount());
                double newModValue = oldModValue + (amplifier + 1);

                AttributeModifier newModifier = new AttributeModifier(
                        ATTRIBUTE_ID,
                        -newModValue,
                        AttributeModifier.Operation.ADD_VALUE
                );
                instance.addPermanentModifier(newModifier);
                double newValue = instance.getValue();

                if (newValue != oldValue - (newModValue - oldModValue)) {
                    OvercastEnchantmentEvents.applyMindDamage(null, entity, 9001, false);
                } else if (entity.getHealth() > entity.getMaxHealth()) {
                    entity.setHealth(entity.getMaxHealth());
                }
            }
        }
        return true;
    }

    @Override
    public void removeAttributeModifiers(AttributeMap attributes) {
        super.removeAttributeModifiers(attributes);
        // Дополнительную логику очистки при необходимости можно перенести в место снятия эффекта
    }
}