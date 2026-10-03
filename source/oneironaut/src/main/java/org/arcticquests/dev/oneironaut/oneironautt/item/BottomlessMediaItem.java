package org.arcticquests.dev.oneironaut.oneironautt.item;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.common.items.magic.ItemMediaHolder;
import com.mojang.datafixers.util.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut;
import org.arcticquests.dev.oneironaut.oneironautt.OneironautConfig;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BottomlessMediaItem extends ItemMediaHolder {

    public static final int priority = 10000;

    public BottomlessMediaItem(Properties settings){
        super(settings);
    }
    @Override
    public boolean isBarVisible(ItemStack pStack) {
        return false;
    }



    public static double arbitraryLog(double base, double num){
        return Math.log(num) / Math.log(base);
    }

    private static final Map<Entity, Pair<List<UUID>, Long>> playerPhialList = new HashMap<>();
    private static final Map<UUID, Pair<Entity, Long>> phialOwners = new HashMap<>();
    public static long time;
    @Nullable
    private static UUID getStackUuid(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return null;
        }
        CompoundTag tag = customData.copyTag();
        return tag.hasUUID("uuid") ? tag.getUUID("uuid") : null;
    }

    private static UUID getOrCreateStackUuid(ItemStack stack) {
        UUID existing = getStackUuid(stack);
        if (existing != null) {
            return existing;
        }

        UUID newUuid = UUID.randomUUID();
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putUUID("uuid", newUuid));
        return newUuid;
    }

    private long logMedia(ItemStack stack){
        UUID uuid = getStackUuid(stack);
        if (uuid == null){
            Oneironaut.LOGGER.info("Inexhaustible phial stack NBT does not contain a UUID tag.");
            return 0;
        }
        long lastCheckIn = phialOwners.get(uuid).getSecond();
        int lastPhialCount = playerPhialList.get(phialOwners.get(uuid).getFirst()).getFirst().size();
        //dashing your hopes against the rocks
        int base = lastPhialCount <= 36 ? 6 : 12;
        //NbtCompound currentData = playerPhialCounts.get(phialOwners.get(uuid).getFirst());
        long media = 1;
        float mediaMultiplier = 1.0f;
        if (lastCheckIn != time){
            if (Math.abs(lastCheckIn - time) <= 1){
                mediaMultiplier = OneironautConfig.getServer().getStaleIPhialLenience();
            } else {
                mediaMultiplier = 0.0f;
            }
        }
        if (lastPhialCount == 1){
            media = MediaConstants.DUST_UNIT / 10;
        } else {
            media = (int) (((arbitraryLog(base, lastPhialCount) + 0.75) / lastPhialCount) * (MediaConstants.DUST_UNIT / 10.0));
        }
        //int media = foundItems > 0 ? (int) (((arbitraryLog(6.0, foundItems) + 0.75) / foundItems) * (MediaConstants.DUST_UNIT / 10)) : 0;
        //Oneironautfinal.LOGGER.info("Media in each of the "+ lastPhialCount + " endless phials in inventory: "+media);
        //Oneironautfinal.LOGGER.info(media);
        return (long) Math.max(media * mediaMultiplier, 0);
    }

    private void resetLists(Pair<List<UUID>, Long> pair, UUID uuid, Entity entity){
        List<UUID> list = pair.getFirst();
        list.clear();
        list.add(uuid);
        playerPhialList.put(entity, new Pair<>(list, time));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
        if (!world.isClientSide && entity instanceof Player) {
            UUID uuid = getOrCreateStackUuid(stack);

            if (!phialOwners.containsKey(uuid)) {
                phialOwners.put(uuid, new Pair<>(entity, time));
            }
            phialOwners.put(uuid, new Pair<>(entity, time));
            if (!playerPhialList.containsKey(entity)) {
                playerPhialList.put(entity, new Pair<>(new ArrayList<>(), time));
            }
            Pair<List<UUID>, Long> currentData = playerPhialList.get(entity);
            List<UUID> list = currentData.getFirst();
            if (currentData.getSecond() != time) {
                resetLists(currentData, uuid, entity);
            } else {
                if (list.contains(uuid)) {
                    uuid = UUID.randomUUID();
                    final UUID finalUuid = uuid;
                    CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putUUID("uuid", finalUuid));
                }
                list.add(uuid);
            }
        }
    }
    @Override
    public void onCraftedBy(ItemStack stack, Level world, Player player) {
        UUID uuid = UUID.randomUUID();
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putUUID("uuid", uuid));
        phialOwners.put(uuid, new Pair<>((Entity) player, time));
    }

    @Override
    public long getMedia(ItemStack stack) {
        if (stack == null){
            //Oneironautfinal.LOGGER.info("Inexhaustible Phial's getMedia method called with a null pointer.");
            return 0;
        }
        try {
            return logMedia(stack);
        } catch (Exception e){
            return 0;
        }
    }

    @Override
    public long getMaxMedia(ItemStack stack) {
        if (stack == null){
            Oneironaut.LOGGER.info("Inexhaustible Phial's getMedia method called with a null pointer.");
            return 0;
        }
        return logMedia(stack);
    }

    @Override
    public void setMedia(ItemStack stack, long media) {}

    @Override
    public boolean canProvideMedia(ItemStack stack) {
        return true;
    }

    @Override
    public boolean canRecharge(ItemStack stack) {
        return false;
    }



}
