package org.arcticquests.dev.oneironaut.oneironautt.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut;
import org.arcticquests.dev.oneironaut.oneironautt.block.ThoughtSlurry;
// import org.arcticquests.dev.oneironaut.oneironautt.casting.OvercastDamageEnchant; // Зачарования теперь Data-Driven
import org.arcticquests.dev.oneironaut.oneironautt.status.*;

public class OneironautMiscRegistry {
    // В NeoForge вместо ForgeRegistries используются стандартные ключи ванильных реестров из класса Registries
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, Oneironaut.MODID);
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, Oneironaut.MODID);


    public static final DeferredHolder<MobEffect, DetectionResistEffect> DETECTION_RESISTANCE = EFFECTS.register("detection_resistance", DetectionResistEffect::new);
    public static final DeferredHolder<MobEffect, GlowingAmbitEffect> NOT_MISSING = EFFECTS.register("not_missing", GlowingAmbitEffect::new);
    public static final DeferredHolder<MobEffect, MonkfruitDelayEffect> RUMINATION = EFFECTS.register("rumination", MonkfruitDelayEffect::new);
    public static final DeferredHolder<MobEffect, MediaDisintegrationEffect> DISINTEGRATION = EFFECTS.register("disintegration", MediaDisintegrationEffect::new);
    public static final DeferredHolder<MobEffect, DisintegrationProtectionEffect> DISINTEGRATION_PROTECTION = EFFECTS.register("disintegration_protection", DisintegrationProtectionEffect::new);

    public static final DeferredHolder<Fluid, ThoughtSlurry.Still> THOUGHT_SLURRY = FLUIDS.register("thought_slurry", ThoughtSlurry.Still::new);
    public static final DeferredHolder<Fluid, ThoughtSlurry.Flowing> THOUGHT_SLURRY_FLOWING = FLUIDS.register("thought_slurry_flowing", ThoughtSlurry.Flowing::new);

    // Зачарование overcast_damage теперь описано в data/oneironaut/enchantment/overcast_damage.json.
    // В коде храним только ссылку-ключ на него; сам Holder<Enchantment> резолвится
    // через RegistryAccess в месте использования (registryAccess.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(...)).
    public static final ResourceKey<Enchantment> OVERCAST_DAMAGE_ENCHANT = ResourceKey.create(
            Registries.ENCHANTMENT,
            ResourceLocation.fromNamespaceAndPath(Oneironaut.MODID, "overcast_damage")
    );

    public static void init(IEventBus bus) {
        FLUIDS.register(bus);
        EFFECTS.register(bus);
        // ENCHANTMENTS.register(bus);
    }

}