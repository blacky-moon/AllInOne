package miyucomics.hexical.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import miyucomics.hexical.features.wristpocket.WristpocketFieldKt;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@SuppressWarnings("UnusedReturnValue")
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow public abstract void setHealth(float health);
    @Shadow public abstract boolean removeAllEffects();
    @Shadow public abstract boolean addEffect(MobEffectInstance effect);


    @WrapOperation(
            method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;checkTotemDeathProtection(Lnet/minecraft/world/damagesource/DamageSource;)Z"
            )
    )
    private boolean undie(LivingEntity instance, DamageSource source, Operation<Boolean> original) {
        if (!(instance instanceof ServerPlayer player))
            return original.call(instance, source);

        ItemStack wristpocket = WristpocketFieldKt.getWristpocket(player);

        if (wristpocket.is(Items.TOTEM_OF_UNDYING)) {
            WristpocketFieldKt.setWristpocket(player, ItemStack.EMPTY);

            player.awardStat(Stats.ITEM_USED.get(Items.TOTEM_OF_UNDYING));
            CriteriaTriggers.USED_TOTEM.trigger(player, wristpocket);

            setHealth(1.0f);
            removeAllEffects();

            addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
            addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
            addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));

            player.level().broadcastEntityEvent(player, (byte) 35);

            return true;
        }

        return original.call(instance, source);
    }
}