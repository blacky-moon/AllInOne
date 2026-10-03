package miyucomics.hexcassettes.inits;

import miyucomics.hexcassettes.HexcassettesMain;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class HexcassettesSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, HexcassettesMain.MOD_ID);

    public static final Supplier<SoundEvent> CASSETTE_EJECT = register("cassette_eject");
    public static final Supplier<SoundEvent> CASSETTE_FAIL = register("cassette_fail");
    public static final Supplier<SoundEvent> CASSETTE_INSERT = register("cassette_insert");
    public static final Supplier<SoundEvent> CASSETTE_LOOP = register("cassette_loop");

    private static Supplier<SoundEvent> register(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(HexcassettesMain.id(name)));
    }

    public static void init() {}
}