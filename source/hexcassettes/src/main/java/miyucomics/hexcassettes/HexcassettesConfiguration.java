package miyucomics.hexcassettes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class HexcassettesConfiguration {
    public int maxCassettes = 6;

    private static final Path CONFIG_PATH = FMLPaths.CONFIGDIR.get().resolve("hexcassettes.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static HexcassettesConfiguration INSTANCE;

    public static HexcassettesConfiguration getInstance() {
        if (INSTANCE == null) INSTANCE = load();
        return INSTANCE;
    }

    private static HexcassettesConfiguration load() {
        try {
            if (Files.exists(CONFIG_PATH)) {
                return GSON.fromJson(Files.readString(CONFIG_PATH), HexcassettesConfiguration.class);
            } else {
                HexcassettesConfiguration cfg = new HexcassettesConfiguration();
                save(cfg);
                return cfg;
            }
        } catch (IOException e) {
            System.out.println("Failed to load config: " + e.getMessage());
            HexcassettesConfiguration cfg = new HexcassettesConfiguration();
            save(cfg);
            return cfg;
        }
    }

    public static void save(HexcassettesConfiguration config) {
        try {
            Files.writeString(CONFIG_PATH, GSON.toJson(config));
        } catch (IOException e) {
            System.out.println("Failed to save config: " + e.getMessage());
        }
    }

    public static void save() {
        save(getInstance());
    }
}