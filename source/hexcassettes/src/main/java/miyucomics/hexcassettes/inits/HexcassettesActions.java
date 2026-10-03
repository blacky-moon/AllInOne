package miyucomics.hexcassettes.inits;

import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.api.casting.castables.Action;
import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.common.lib.hex.HexActions;
import miyucomics.hexcassettes.HexcassettesMain;
import miyucomics.hexcassettes.actions.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegisterEvent;

public class HexcassettesActions {
    public static void registerAll(RegisterEvent.RegisterHelper<ActionRegistryEntry> helper) {
        register(helper, "enqueue", "qeqwqwqwqwqeqaweqqqqqwweeweweewqdwwewewwewweweww", HexDir.EAST, new OpEnqueue());
        register(helper, "eject", "qeeeeewqqqqqeweeewwwewewewwweeawawqqwqwqqw", HexDir.EAST, new OpEject());
        register(helper, "dequeue", "eqeweweweweqedwqeeeeewwqqwqwqqweawwqwqwwqwwqwqww", HexDir.WEST, new OpDequeue());
        register(helper, "killall", "eqeweweweweqedwqeeeeewwqqwqwqqw", HexDir.WEST, new OpKillAll());
        register(helper, "specs", "qeqwqwqwqwqeqaweqqqqq", HexDir.EAST, new OpSpecs());
        register(helper, "busy", "qeqwqwqwqwqeqaweqqqqqaww", HexDir.EAST, new OpBusy());
        register(helper, "inspect", "eqeweweweweqedwqeeeee", HexDir.WEST, new OpInspect());
        register(helper, "foretell", "eqeweweweweqedwqeeeeedww", HexDir.WEST, new OpForetell());
        register(helper, "self", "qeqwqwqwqwqeqaweqqqqqwweeweweew", HexDir.EAST, new OpSelf());
    }

    private static void register(RegisterEvent.RegisterHelper<ActionRegistryEntry> helper, String name, String signature, HexDir startDir, Action action) {
        helper.register(
                HexcassettesMain.id(name),
                new ActionRegistryEntry(HexPattern.fromAngles(signature, startDir), action)
        );
    }
}