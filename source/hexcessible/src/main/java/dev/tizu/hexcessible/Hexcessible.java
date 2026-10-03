package dev.tizu.hexcessible;

import com.mojang.blaze3d.platform.InputConstants;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.KeyMapping;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(Hexcessible.MOD_ID)
public class Hexcessible {

    public static final String MOD_ID = "hexcessible";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


    private static HexcessibleConfig cfg = HexcessibleConfig.get();

    public Hexcessible(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("Hexcessible initialized!");

        modContainer.registerExtensionPoint(
                IConfigScreenFactory.class,
                (container, parentScreen) -> AutoConfig.getConfigScreen(HexcessibleConfig.class, parentScreen).get()
        );
    }

    public static HexcessibleConfig cfg() {
        return cfg;
    }

    public static String getAsset(String path) {
        try (
                var scanner = new java.util.Scanner(
                        Hexcessible.class.getResourceAsStream(path),
                        java.nio.charset.StandardCharsets.UTF_8
                ).useDelimiter("\\A")
        ) {
            return scanner.next();
        } catch (Exception e) {
            return "";
        }
    }
}