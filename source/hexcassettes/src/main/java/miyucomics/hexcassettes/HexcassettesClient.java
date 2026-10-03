package miyucomics.hexcassettes;

import com.mojang.blaze3d.platform.InputConstants;
import miyucomics.hexcassettes.client.CassetteScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

public class HexcassettesClient {
    public static final KeyMapping CASSETTE_KEYBIND = new KeyMapping(
            "key.hexcassettes.ponder_cassette",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "key.categories.hexcassettes"
    );
}

@EventBusSubscriber(modid = HexcassettesMain.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
class HexcassettesClientModEvents {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {}

    @SubscribeEvent
    public static void onKeyRegister(RegisterKeyMappingsEvent event) {
        event.register(HexcassettesClient.CASSETTE_KEYBIND);
    }
}

@EventBusSubscriber(modid = HexcassettesMain.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
class HexcassettesClientGameEvents {
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft client = Minecraft.getInstance();
        while (HexcassettesClient.CASSETTE_KEYBIND.consumeClick()) {
            if (client.screen == null) {
                client.setScreen(new CassetteScreen());
            }
        }
    }
}