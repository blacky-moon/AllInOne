package me.nanorasmus.nanodev.hex_js.kubejs.customIotas;

import dev.latvian.mods.rhino.util.HideFromJS;

import java.util.HashMap;

public class CustomIotaRegistry {
    @HideFromJS
    public static HashMap<String, CustomIotaEntry> customIotas = new HashMap<>();
}
