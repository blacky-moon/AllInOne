package me.nanorasmus.nanodev.hex_js;

import at.petrak.hexcasting.api.casting.castables.Action;
import at.petrak.hexcasting.api.casting.castables.SpecialHandler;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.common.lib.HexRegistries;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import dev.latvian.mods.rhino.Context;
import me.nanorasmus.nanodev.hex_js.kubejs.customIotas.CustomIota;
import me.nanorasmus.nanodev.hex_js.kubejs.customPatterns.CustomPatternHolder;
import me.nanorasmus.nanodev.hex_js.kubejs.customPatterns.CustomPatternRegistry;
import me.nanorasmus.nanodev.hex_js.storage.StorageManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class HexJS {
    public static final String MOD_ID = "hexjs";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static MinecraftServer server;
    public static Context ctx;

    /**
     * Внутренний класс-обертка SpecialHandler.
     * Он связывает конкретный Action (наш CustomPatternHolder) с паттерном.
     */
    private static class HexJSCustomPatternHandler implements SpecialHandler {
        private final Action action;
        private final Component name;

        public HexJSCustomPatternHandler(Action action, Component name) {
            this.action = action;
            this.name = name;
        }

        @Override
        public Action act() {
            return this.action;
        }

        @Override
        public Component getName() {
            return this.name;
        }
    }

    public static void init() {
        HexJSNeoForge.MOD_EVENT_BUS.addListener((RegisterEvent event) -> {

            // 1. Регистрация кастомных Iota типов
            if (event.getRegistryKey() == HexIotaTypes.REGISTRY.key()) {
                event.register(
                        HexIotaTypes.REGISTRY.key(),
                        modLoc("js_custom_iota"),
                        () -> CustomIota.TYPE
                );
            }

            // 2. Регистрация Special Handler Factory
            if (event.getRegistryKey() == HexRegistries.SPECIAL_HANDLER) {

                // Мы регистрируем Supplier, который возвращает Factory<SpecialHandler>
                event.register(
                        HexRegistries.SPECIAL_HANDLER,
                        modLoc("custom_patterns_factory"),
                        () -> new SpecialHandler.Factory<SpecialHandler>() {
                            @Override
                            public SpecialHandler tryMatch(HexPattern pattern, CastingEnvironment env) {
                                String signature = pattern.anglesSignature();

                                // Ищем в нашем реестре KubeJS
                                CustomPatternHolder holder = CustomPatternRegistry.registry.get(signature);

                                if (holder != null) {
                                    LOGGER.debug("HexJS matched custom pattern factory: {}", signature);
                                    return new HexJSCustomPatternHandler(holder, holder.getDisplayName());
                                }

                                return null;
                            }
                        }
                );
            }
        });

        NeoForge.EVENT_BUS.addListener((ServerStartingEvent event) -> {
            server = event.getServer();
            StorageManager.load(event.getServer());
        });

        NeoForge.EVENT_BUS.addListener((ServerStoppingEvent event) -> {
            StorageManager.save(event.getServer());
        });

        NeoForge.EVENT_BUS.addListener((ServerTickEvent.Pre event) -> {
            server = event.getServer();
        });
    }

    public static void initClient() {
    }

    public static void initServer() {
    }

    public static ResourceLocation modLoc(String key) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, key);
    }
}