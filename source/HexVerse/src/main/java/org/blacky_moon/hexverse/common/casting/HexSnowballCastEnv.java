package org.blacky_moon.hexverse.common.casting;

import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.MishapEnvironment;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HexSnowballCastEnv extends CastingEnvironment {
    private final LivingEntity caster;
    private final Vec3 center;
    private final double radius;
    private final FrozenPigment pigment;
    private final ItemStack stack;
    private long currentMedia;
    private final boolean isPlayerCaster;

    public HexSnowballCastEnv(ServerLevel level, @Nullable LivingEntity caster, Vec3 center, double radius,
                              FrozenPigment pigment, ItemStack stack, long currentMedia, long maxMedia) {
        super(level);
        this.caster = caster;
        this.center = center;
        this.radius = radius;
        this.pigment = pigment != null ? pigment : FrozenPigment.DEFAULT.get();
        this.stack = stack;
        this.currentMedia = currentMedia;
        this.isPlayerCaster = caster instanceof Player;
    }

    @Override
    public @Nullable LivingEntity getCastingEntity() {
        return caster;
    }

    @Override
    public MishapEnvironment getMishapEnvironment() {
        if (isPlayerCaster && caster instanceof ServerPlayer player) {
            return new MishapEnvironment(this.world, player) {
                @Override
                public void yeetHeldItemsTowards(Vec3 vec) {
                    var hand = player.getUsedItemHand();
                    var stack = player.getItemInHand(hand);
                    if (!stack.isEmpty()) {
                        yeetItem(stack, player.position(), vec);
                        player.setItemInHand(hand, ItemStack.EMPTY);
                    }
                }

                @Override
                public void dropHeldItems() {

                }

                @Override
                public void drown() {
                    if (caster != null) {
                        caster.hurt(caster.damageSources().drown(), 2.0F);
                    }
                }

                @Override
                public void damage(float amount) {
                    if (caster != null) {
                        caster.hurt(caster.damageSources().magic(), amount);
                    }
                }

                @Override
                public void removeXp(int amount) {
                    if (caster instanceof ServerPlayer p) {
                        p.giveExperiencePoints(-amount);
                    }
                }

                @Override
                public void blind(int ticks) {
                    if (caster instanceof ServerPlayer p) {
                        p.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                                net.minecraft.world.effect.MobEffects.BLINDNESS, ticks, 0
                        ));
                    }
                }

                @Override
                public void nauseate(int ticks) {
                    if (caster instanceof ServerPlayer p) {
                        p.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                                net.minecraft.world.effect.MobEffects.CONFUSION, ticks, 0
                        ));
                    }
                }
            };
        } else {
            return new MishapEnvironment(this.world, null) {
                @Override
                public void yeetHeldItemsTowards(Vec3 vec) {
                }

                @Override
                public void dropHeldItems() {

                }

                @Override
                public void drown() {
                    if (caster != null) {
                        caster.hurt(caster.damageSources().drown(), 2.0F);
                    }
                }

                @Override
                public void damage(float amount) {
                    if (caster != null) {
                        caster.hurt(caster.damageSources().magic(), amount);
                    }
                }

                @Override
                public void removeXp(int amount) {
                }

                @Override
                public void blind(int ticks) {

                }

                @Override
                public void nauseate(int ticks) {

                }
            };
        }
    }

    @Override
    protected long extractMediaEnvironment(long cost, boolean simulate) {
        if (currentMedia >= cost) {
            if (!simulate) {
                currentMedia -= cost;
            }
            return 0;
        }
        return cost;
    }

    @Override
    public boolean isVecInRangeEnvironment(Vec3 vec) {
        return vec.distanceToSqr(center) <= Math.pow(radius, 2);
    }

    @Override
    public boolean hasEditPermissionsAtEnvironment(BlockPos pos) {
        return isVecInRangeEnvironment(Vec3.atCenterOf(pos));
    }

    @Override
    public InteractionHand getCastingHand() {
        return InteractionHand.MAIN_HAND;
    }

    @Override
    public List<ItemStack> getUsableStacks(StackDiscoveryMode mode) {
        return List.of(stack);
    }

    @Override
    public List<HeldItemInfo> getPrimaryStacks() {
        return List.of(new HeldItemInfo(stack, InteractionHand.MAIN_HAND));
    }

    @Override
    public boolean replaceItem(java.util.function.Predicate<ItemStack> stackOk, ItemStack replaceWith, @Nullable InteractionHand hand) {
        return false;
    }

    @Override
    public FrozenPigment getPigment() {
        return pigment;
    }

    @Nullable
    @Override
    public FrozenPigment setPigment(@Nullable FrozenPigment pigment) {
        return this.pigment;
    }

    @Override
    public void produceParticles(ParticleSpray particles, FrozenPigment pigment) {
        particles.sprayParticles(this.world, pigment);
    }

    @Override
    public void printMessage(Component msg) {
        if (isPlayerCaster && caster instanceof Player player) {
            player.displayClientMessage(msg, true);
        }
    }

    @Override
    public Vec3 mishapSprayPos() {
        return center;
    }
}