package me.nanorasmus.nanodev.hex_js.storage;

import me.nanorasmus.nanodev.hex_js.HexJS;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class StorageManager extends SavedData {
    public static final String saveId = HexJS.MOD_ID + "_save";
    public static StorageManager INSTANCE;

    public static final SavedData.Factory<StorageManager> FACTORY = new SavedData.Factory<>(
            StorageManager::new,
            StorageManager::load,
            null
    );

    public static PatternList defaultPatternList = new PatternList();
    public static HashMap<UUID, PatternList> playerPatternList = new HashMap<>();

    public static ArrayList<UUID> currentlyForcedPlayers = new ArrayList<>();

    public static void save(MinecraftServer server) {
        if (INSTANCE != null) {
            INSTANCE.setDirty();
        }
    }

    public static void load(MinecraftServer server) {
        INSTANCE = server.overworld().getDataStorage().computeIfAbsent(FACTORY, saveId);
    }

    public static PatternList getGlobalPatternList() { return defaultPatternList.copy(); }
    public static void setGlobalPatternList(PatternList override) { defaultPatternList = override; }
    public static void addToGlobalPatternList(ArrayList<String> additions) {
        defaultPatternList.angleSignatureList.addAll(additions);
    }
    public static void removeFromGlobalPatternList(String toRemove) {
        defaultPatternList.angleSignatureList.remove(toRemove);
    }

    public static boolean getGlobalIsWhitelist() { return defaultPatternList.isWhitelist; }
    public static void setGlobalIsWhitelist(boolean isWhitelist) { defaultPatternList.isWhitelist = isWhitelist; }

    public static void clearGlobalList() {
        defaultPatternList.clearPatternList();
    }

    public static void addGlobalRedirect(String input, String output) {
        defaultPatternList.addRedirect(input, output);
    }
    public static HashMap<String, String> getGlobalRedirects() {
        return defaultPatternList.redirectListRaw;
    }
    public static void setGlobalRedirects(HashMap<String, String> redirects) {
        defaultPatternList.setRedirects(redirects);
    }
    public static void clearGlobalRedirects() {
        defaultPatternList.clearRedirects();
    }

    public static PatternList getPatternList(UUID playerUUID) {
        return playerPatternList.getOrDefault(playerUUID, new PatternList());
    }

    public static void setPlayerPatternList(UUID playerUUID, PatternList playerPatterns) {
        playerPatternList.put(playerUUID, playerPatterns);
    }

    public static void addToPlayerPatternList(UUID playerUUID, ArrayList<String> additions) {
        PatternList playerPatterns = getPatternList(playerUUID);

        playerPatterns.angleSignatureList.addAll(additions);

        setPlayerPatternList(playerUUID, playerPatterns);
    }

    public static void removeFromPlayerPatternList(UUID playerUUID, String toRemove) {
        PatternList playerPatterns = getPatternList(playerUUID);

        playerPatterns.angleSignatureList.remove(toRemove);

        setPlayerPatternList(playerUUID, playerPatterns);
    }

    public static void clearPlayerPatternList(UUID playerUUID) {
        PatternList playerPatterns = getPatternList(playerUUID);

        playerPatterns.angleSignatureList.clear();

        setPlayerPatternList(playerUUID, playerPatterns);
    }

    public static boolean getIsPlayerPatternsWhitelist(UUID playerUUID) { return getPatternList(playerUUID).isWhitelist; }
    public static void setIsPlayerPatternsWhitelist(UUID playerUUID, boolean isWhitelist) {
        PatternList playerPatterns = getPatternList(playerUUID);

        playerPatterns.isWhitelist = isWhitelist;

        setPlayerPatternList(playerUUID, playerPatterns);
    }

    public static HashMap<String, String> getPlayerRedirects(UUID playerUUID) {
        return getPatternList(playerUUID).redirectListRaw;
    }
    public static void setPlayerRedirects(UUID playerUUID, HashMap<String, String> redirects) {
        PatternList playerPatterns = getPatternList(playerUUID);

        playerPatterns.setRedirects(redirects);

        setPlayerPatternList(playerUUID, playerPatterns);
    }
    public static void clearPlayerRedirects(UUID playerUUID) {
        PatternList playerPatterns = getPatternList(playerUUID);

        playerPatterns.clearRedirects();

        setPlayerPatternList(playerUUID, playerPatterns);
    }

    public static void addPlayerRedirect(UUID playerUUID, String input, String output) {
        PatternList playerPatterns = getPatternList(playerUUID);

        playerPatterns.addRedirect(input, output);

        setPlayerPatternList(playerUUID, playerPatterns);
    }

    public static void setPlayerMaxBookkeeperLength(UUID playerUUID, int newLength) {
        PatternList playerPatterns = getPatternList(playerUUID);

        playerPatterns.maxBookKeepersLength = newLength;

        setPlayerPatternList(playerUUID, playerPatterns);
    }

    public static int getPlayerMaxBookkeeperLength(UUID playerUUID) {
        PatternList playerPatterns = getPatternList(playerUUID);

        return playerPatterns.maxBookKeepersLength;
    }

    public static void clearPlayerMaxBookkeeperLength(UUID playerUUID) {
        PatternList playerPatterns = getPatternList(playerUUID);

        playerPatterns.maxBookKeepersLength = -1;

        setPlayerPatternList(playerUUID, playerPatterns);
    }

    public static StorageManager load(CompoundTag nbt, HolderLookup.Provider provider) {
        StorageManager manager = new StorageManager();

        for (int i = 0; i < nbt.getInt("saved_player_count"); i++) {
            // Get the saved patterns
            PatternList playerPatterns = new PatternList(nbt.getBoolean("player_"+i+"_is_whitelist"));
            for (int x = 0; x < nbt.getInt("player_"+i+"_pattern_count"); x++) {
                playerPatterns.angleSignatureList.add(nbt.getString("player_"+i+"_pattern_"+x));
            }

            // Get the saved redirects
            for (int x = 0; x < nbt.getInt("player_"+i+"_redirect_count"); x++) {
                playerPatterns.addRedirect(
                        nbt.getString("player_"+i+"_redirect_"+x+"_input"),
                        nbt.getString("player_"+i+"_redirect_"+x+"_output")
                );
            }

            // Bookkeeper's length
            playerPatterns.maxBookKeepersLength = nbt.getInt("player_"+i+"_max_bookkeeper_length");

            // Add to list
            playerPatternList.put(
                    nbt.getUUID("player_"+i),
                    playerPatterns
            );
        }

        return manager;
    }

    @Override
    public CompoundTag save(CompoundTag nbt, HolderLookup.Provider provider) {
        nbt.putInt("saved_player_count", playerPatternList.size());

        int i = 0;
        for (UUID key : playerPatternList.keySet()) {
            nbt.putUUID("player_"+i, key);

            // Save patterns
            PatternList playerPatterns = playerPatternList.get(key);
            nbt.putBoolean("player_"+i+"_is_whitelist", playerPatterns.isWhitelist);

            nbt.putInt("player_"+i+"_pattern_count", playerPatterns.angleSignatureList.size());
            for (int x = 0; x < playerPatterns.angleSignatureList.size(); x++) {
                nbt.putString("player_"+i+"_pattern_"+x, playerPatterns.angleSignatureList.get(x));
            }

            // Save redirects
            nbt.putInt("player_"+i+"_redirect_count", playerPatterns.redirectListRaw.size());
            int x = 0;
            for (String input : playerPatterns.redirectListRaw.keySet()) {
                nbt.putString("player_"+i+"_redirect_"+x+"_input", input);
                nbt.putString("player_"+i+"_redirect_"+x+"_output", playerPatterns.redirectListRaw.get(input));
                x++;
            }

            // Bookkeeper's length
            nbt.putInt("player_"+i+"_max_bookkeeper_length", playerPatterns.maxBookKeepersLength);

            i++;
        }

        return nbt;
    }
}