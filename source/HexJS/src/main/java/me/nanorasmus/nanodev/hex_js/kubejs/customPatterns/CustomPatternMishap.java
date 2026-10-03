package me.nanorasmus.nanodev.hex_js.kubejs.customPatterns;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.Mishap;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CustomPatternMishap extends Mishap {
    private final String message;

    public CustomPatternMishap(String message) {
        this.message = message;
    }

    @NotNull
    @Override
    public FrozenPigment accentColor(@NotNull CastingEnvironment env, @NotNull Mishap.Context context) {
        return dyeColor(DyeColor.RED);
    }

    @Nullable
    @Override
    public Component errorMessage(@NotNull CastingEnvironment env, @NotNull Mishap.Context context) {
        return Component.literal(message);
    }

    @Override
    public void execute(@NotNull CastingEnvironment env, @NotNull Mishap.Context context, @NotNull List<Iota> stack) {
        // Ничего не делаем со стеком, только показываем сообщение
    }
}