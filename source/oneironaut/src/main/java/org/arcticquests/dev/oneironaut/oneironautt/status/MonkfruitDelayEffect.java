package org.arcticquests.dev.oneironaut.oneironautt.status;

import at.petrak.hexcasting.api.item.MediaHolderItem;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.common.lib.HexItems;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import kotlin.collections.CollectionsKt;
import ram.talia.hexal.common.entities.WanderingWisp;

import java.util.ArrayList;
import java.util.List;

public class MonkfruitDelayEffect extends MobEffect {
    public static final String ACCUMULATED_MEDIA_KEY = "oneironaut:monkfruit_accumulated_media";
    public static final long WISP_SPAWN_COST = MediaConstants.CRYSTAL_UNIT * 2; // 20,000 media

    public MonkfruitDelayEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xa1b25e);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity instanceof ServerPlayer player) {
            ServerLevel world = (ServerLevel) entity.level();
            double maxGaussian = 2.0;
            double rawGaussian = world.random.nextGaussian();
            double gaussian = Math.min(Math.max((rawGaussian * (maxGaussian / 2)) + (maxGaussian / 2), 0), maxGaussian);
            double overallReleased = gaussian + 1;

            List<ItemStack> mediaHolders = new ArrayList<>();
            for (ItemStack checkedStack : player.getInventory().items) {
                if (checkedStack.getItem() instanceof MediaHolderItem battery) {
                    if (battery.canRecharge(checkedStack) && battery.getMedia(checkedStack) < battery.getMaxMedia(checkedStack)) {
                        mediaHolders.add(checkedStack);
                    }
                }
            }
            for (ItemStack checkedStack : player.getInventory().armor) {
                if (checkedStack.getItem() instanceof MediaHolderItem battery) {
                    if (battery.canRecharge(checkedStack) && battery.getMedia(checkedStack) < battery.getMaxMedia(checkedStack)) {
                        mediaHolders.add(checkedStack);
                    }
                }
            }
            ItemStack offhandStack = player.getItemInHand(InteractionHand.OFF_HAND);
            if (offhandStack.getItem() instanceof MediaHolderItem battery) {
                if (battery.canRecharge(offhandStack) && battery.getMedia(offhandStack) < battery.getMaxMedia(offhandStack)) {
                    mediaHolders.add(offhandStack);
                }
            }

            double multiplier = 1.0 + (amplifier / 4.0);
            long totalGeneratedMedia = (long) (overallReleased * multiplier * MediaConstants.DUST_UNIT);

            int quantity = mediaHolders.size();
            if (quantity > 0) {
                long insertedPerHolder = totalGeneratedMedia / quantity;
                for (ItemStack battery : mediaHolders) {
                    MediaHolderItem type = (MediaHolderItem) battery.getItem();
                    type.insertMedia(battery, insertedPerHolder, false);
                }
            } else {
                CompoundTag persistentData = player.getPersistentData();
                long currentAccumulated = persistentData.getLong(ACCUMULATED_MEDIA_KEY);
                currentAccumulated += totalGeneratedMedia;

                if (currentAccumulated >= WISP_SPAWN_COST) {
                    currentAccumulated -= WISP_SPAWN_COST;

                    Vec3 spawnPos = player.position().add(0, 1.5, 0);
                    WanderingWisp wisp = new WanderingWisp(world, spawnPos);
                    wisp.setPigment(new FrozenPigment(
                            new ItemStack(CollectionsKt.elementAt(HexItems.DYE_PIGMENTS.values(),
                                    world.random.nextInt(HexItems.DYE_PIGMENTS.size()))),
                            Util.NIL_UUID
                    ));
                    world.addFreshEntity(wisp);
                }

                persistentData.putLong(ACCUMULATED_MEDIA_KEY, currentAccumulated);
            }
        }
        return true;
    }
}