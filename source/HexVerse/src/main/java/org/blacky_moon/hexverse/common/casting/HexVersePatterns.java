package org.blacky_moon.hexverse.common.casting;

import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.blacky_moon.hexverse.HexVerseMain;
import  at.petrak.hexcasting.api.casting.castables.Action;
public class HexVersePatterns {
    public static void registerAll(RegisterEvent.RegisterHelper<ActionRegistryEntry> helper) {
        register(helper, "imbue_equipment", "dqaqdqaqdqaeadawadadawadadawa", HexDir.WEST, new OpImbueEquipment());
    }

    private static void register(RegisterEvent.RegisterHelper<ActionRegistryEntry> helper, String name, String signature, HexDir startDir, Action action) {
        helper.register(
                ResourceLocation.fromNamespaceAndPath(HexVerseMain.MOD_ID, name),
                new ActionRegistryEntry(HexPattern.fromAngles(signature, startDir), action)
        );
    }
}
