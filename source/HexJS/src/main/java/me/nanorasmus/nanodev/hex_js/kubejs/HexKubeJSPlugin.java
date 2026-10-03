package me.nanorasmus.nanodev.hex_js.kubejs;

import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.casting.iota.*;
import at.petrak.hexcasting.api.casting.math.HexAngle;
import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingRegistry;

import me.nanorasmus.nanodev.hex_js.HexJS;
import me.nanorasmus.nanodev.hex_js.helpers.IotaHelper;
import me.nanorasmus.nanodev.hex_js.kubejs.customIotas.CustomIota;
import me.nanorasmus.nanodev.hex_js.kubejs.customPatterns.CustomPatternCastedEvent;

public class HexKubeJSPlugin implements KubeJSPlugin {
    public static HexKubeJSPlugin singleton;

    public static EventGroup hexcastingEventGroup = EventGroup.of("HexcastingEvents");

    public static EventHandler patternCastedEventHandler = hexcastingEventGroup.server("registeredPatternCastEvent", () -> CustomPatternCastedEvent.class).hasResult();

    @Override
    public void init() {
        singleton = this;
        // В Hex Casting 1.20+ PatternRegistry.addSpecialHandler устарел и был удален.
        // Если требуется регистрация кастомных паттернов, используется HexRegistries.SPECIAL_HANDLER.
    }

    @Override
    public void registerBindings(BindingRegistry bindings) {
        bindings.add("Hexcasting", new HexJSBindings());

        // The event itself
        bindings.add("registeredPatternCastEvent", CustomPatternCastedEvent.class);

        // Helpers
        bindings.add("IotaHelper", IotaHelper.class);

        // Iota stuff
        bindings.add("Iota", Iota.class);
        bindings.add("BooleanIota", BooleanIota.class);
        bindings.add("DoubleIota", DoubleIota.class);
        bindings.add("EntityIota", EntityIota.class);
        bindings.add("GarbageIota", GarbageIota.class);
        bindings.add("ListIota", ListIota.class);
        bindings.add("NullIota", NullIota.class);
        bindings.add("PatternIota", PatternIota.class);
        bindings.add("Vec3Iota", Vec3Iota.class);

        bindings.add("CustomIota", CustomIota.class);

        // Misc hex stuff
        bindings.add("HexDir", HexDir.class);
        bindings.add("HexAngle", HexAngle.class);
        bindings.add("HexPattern", HexPattern.class);
        bindings.add("MediaConstants", MediaConstants.class);
        bindings.add("HexIotaTypes", HexIotaTypes.class);
    }

    @Override
    public void registerEvents(EventGroupRegistry registry) {
        registry.register(hexcastingEventGroup);
    }
}