package org.arcticquests.dev.oneironaut.oneironautt.casting.idea;

import at.petrak.hexcasting.api.casting.iota.GarbageIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut;
import org.arcticquests.dev.oneironaut.oneironautt.OneironautConfig;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class IdeaInscriptionManager extends SavedData {

    public static final String ID = Oneironaut.MODID + "_ideainscription";
    // setup for Idea Inscription
    private static Map<String, IdeaEntry<?>> entryMap = new HashMap<>();
    private static final int minuteInTicks = 20 * 60;
    private static final int hourInTicks = minuteInTicks * 60;
    protected static final int lifetime = OneironautConfig.getServer().getIdeaLifetime();

    @Override
    public CompoundTag save(CompoundTag nbt, HolderLookup.Provider registries) {
        Iterator<Map.Entry<String, IdeaEntry<?>>> iotaIterator = entryMap.entrySet().iterator();
        Map.Entry<String, IdeaEntry<?>> nextEntry;
        while (iotaIterator.hasNext()) {
            nextEntry = iotaIterator.next();
            nbt.put(nextEntry.getKey(), nextEntry.getValue().serialize(registries));
        }
        return nbt;
    }

    public static IdeaInscriptionManager createFromNbt(CompoundTag nbt, HolderLookup.Provider registries) {
        IdeaInscriptionManager ideas = new IdeaInscriptionManager();
        Map<String, IdeaEntry<?>> reconstructedIotaMap = new HashMap<>();
        Iterator<String> ideaIterator = nbt.getAllKeys().iterator();
        String currentIdeaKey;

        ServerLevel overworld = Oneironaut.getCachedServer() != null ? Oneironaut.getCachedServer().overworld() : null;

        while (ideaIterator.hasNext()) {
            currentIdeaKey = ideaIterator.next();
            CompoundTag currentNbt = nbt.getCompound(currentIdeaKey);
            if (currentNbt.contains(IdeaEntry.TAG_ENTRY_TYPE) && overworld != null) {
                // Pass registries parameter to IdeaEntry.deserialize
                reconstructedIotaMap.put(currentIdeaKey, IdeaEntry.deserialize(currentNbt, overworld, registries));
            } else if (overworld != null) {
                reconstructedIotaMap.put(currentIdeaKey, IdeaEntry.deserializeLegacyEntry(currentNbt, overworld, registries));
            }
        }
        entryMap = reconstructedIotaMap;
        return ideas;
    }

    public static IdeaInscriptionManager getServerState(MinecraftServer server) {
        if (server == null) {
            return null;
        }
        DimensionDataStorage stateManager = server.overworld().getDataStorage();

        SavedData.Factory<IdeaInscriptionManager> factory = new SavedData.Factory<>(
                IdeaInscriptionManager::new,
                IdeaInscriptionManager::createFromNbt
        );

        IdeaInscriptionManager ideas = stateManager.computeIfAbsent(factory, ID);
        ideas.setDirty();
        return ideas;
    }

    public static void cleanMap(MinecraftServer server, IdeaInscriptionManager ideaState){
        Set<String> keysToRemove = new HashSet<>();
        Iterator<String> keys = entryMap.keySet().iterator();
        long overworldTime = server.overworld().getGameTime();
        Oneironaut.LOGGER.info("Cleaning expired idea entries, current time is {}", overworldTime);
        String currentKey;
        IdeaEntry<?> currentData;
        long timestamp;
        while (keys.hasNext()){
            currentKey = keys.next();
            currentData = entryMap.get(currentKey);
            timestamp = currentData.creationTimestamp;
            if (currentData.isExpired(overworldTime)){
                Oneironaut.LOGGER.info("Found expired key {}, expired by {} ticks.", currentKey, overworldTime - timestamp);
                keysToRemove.add(currentKey);
            }
        }
        if (!keysToRemove.isEmpty()){
            Iterator<String> stringIter = keysToRemove.iterator();
            String currentString;
            while (stringIter.hasNext()){
                currentString = stringIter.next();
                entryMap.remove(currentString);
            }
            ideaState.setDirty();
            Oneironaut.LOGGER.info("Removed {} expired entries.", keysToRemove.size());
        }
    }

    public static void writeEntry(IdeaKeyable key, IdeaEntry<?> entry){
        if (entry.type == IdeaEntry.EntryType.IOTA && entry.payload instanceof Iota iota){
            if (!(iota.getType().equals(GarbageIota.TYPE))){
                entryMap.put(key.getKey(), entry);
            } else {
                eraseEntry(key);
            }
        } else {
            entryMap.put(key.getKey(), entry);
        }
    }

    public static void eraseEntry(IdeaKeyable key){
        if (key.getKey().equals("everything")){
            entryMap.clear();
        } else {
            entryMap.remove(key.getKey());
        }
    }

    @Nullable
    public static IdeaEntry<?> getEntry(IdeaKeyable key, ServerLevel world, @Nullable IdeaEntry.EntryType type){
        String keyString = key.getKey();
        IdeaEntry<?> entry = getValidEntry(keyString, world);
        if (entry != null){
            if (type == null || entry.type == type){
                return entry;
            }
        }
        return null;
    }

    @Nullable
    public static IdeaEntry<?> getEntry(IdeaKeyable key, ServerLevel world){
        return getEntry(key, world, null);
    }

    public static double getEntryTimestamp(IdeaKeyable key, ServerLevel world){
        String keyString = key.getKey();
        IdeaEntry<?> entry = getValidEntry(keyString, world);
        if (entry != null){
            return entry.creationTimestamp;
        } else {
            return -1;
        }
    }

    public static UUID getEntryWriter(IdeaKeyable key, ServerLevel world){
        String keyString = key.getKey();
        IdeaEntry<?> entry = getValidEntry(keyString, world);
        if (entry != null){
            return entry.writerID;
        }
        return null;
    }

    private static IdeaEntry<?> getValidEntry(String key, ServerLevel world){
        IdeaEntry<?> entry = entryMap.get(key);
        if (entry != null){
            if (entry.isExpired(world.getGameTime())){
                entryMap.remove(key);
            } else {
                return entry;
            }
        }
        return null;
    }
}