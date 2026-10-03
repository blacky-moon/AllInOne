package dev.tizu.hexcessible;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

// Эта аннотация говорит загрузчику: "Зарегистрируй события из этого класса на шине MOD"
@EventBusSubscriber(modid = Hexcessible.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class HexcessibleKeybinds {
    public static final KeyMapping KEY_DOCS = new KeyMapping(
            "key.hexcessible.docs",
            InputConstants.KEY_Z, // По умолчанию Z
            "key.categories.hexcessible"
    );

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        // Регистрируем ту самую кнопку из главного класса
        event.register(KEY_DOCS);
        Hexcessible.LOGGER.info("Keybind 'Open Documentation' registered successfully.");
    }
}