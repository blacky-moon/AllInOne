package miyucomics.hexpose;

import at.petrak.hexcasting.common.lib.hex.HexActions;
import at.petrak.hexcasting.common.lib.hex.HexArithmetics;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import miyucomics.hexpose.arithmetic.TextArithmetic;
import miyucomics.hexpose.iotas.IdentifierIota;
import miyucomics.hexpose.iotas.ItemStackIota;
import miyucomics.hexpose.iotas.TextIota;
import miyucomics.hexpose.utils.ChatHandler;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(HexposeMain.MOD_ID)
public class HexposeMain {
    public static final String MOD_ID = "hexpose";

    public HexposeMain(IEventBus modBus) {
        modBus.addListener(this::registerAll);
        modBus.addListener(this::setup);
    }

    private void registerAll(RegisterEvent event) {
        // Регистрируем Iotas
        event.register(HexIotaTypes.REGISTRY.key(), helper -> {
            helper.register(ResourceLocation.fromNamespaceAndPath(MOD_ID, "identifier"), IdentifierIota.TYPE);
            helper.register(ResourceLocation.fromNamespaceAndPath(MOD_ID, "item_stack"), ItemStackIota.TYPE);
            helper.register(ResourceLocation.fromNamespaceAndPath(MOD_ID, "text"), TextIota.TYPE);
        });

        // Регистрируем Arithmetic
        event.register(HexArithmetics.REGISTRY.key(), helper -> {
            helper.register(ResourceLocation.fromNamespaceAndPath(MOD_ID, "text"), new TextArithmetic());
        });

        // Регистрируем Actions - передаем helper, а не registry
        event.register(HexActions.REGISTRY.key(), HexposeActions::registerAll);
    }

    private void setup(FMLCommonSetupEvent event) {
        event.enqueueWork(ChatHandler::init);
    }
}