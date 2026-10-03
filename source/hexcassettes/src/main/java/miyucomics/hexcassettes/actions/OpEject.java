package miyucomics.hexcassettes.actions;

import at.petrak.hexcasting.api.casting.RenderedSpell;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.Mishap;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import miyucomics.hexcassettes.HexcassettesMain;
import miyucomics.hexcassettes.PlayerEntityMinterface;
import miyucomics.hexcassettes.data.CassetteState;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class OpEject implements SpellAction {
    @Override
    public int getArgc() {
        return 0;
    }

    @Override
    public Result execute(List<? extends Iota> args, CastingEnvironment env) {
        var caster = env.getCastingEntity();
        if (!(caster instanceof PlayerEntityMinterface minterface))
            throw new MishapBadCaster();
        if (minterface.getCassetteState().owned <= 0)
            throw new NoCassettesToEject();

        return new Result(new Spell(minterface.getCassetteState(), (ServerPlayer) caster), 0L, List.of(), 0L);
    }

    private record Spell(CassetteState state, ServerPlayer player) implements RenderedSpell {
        @Override
        public void cast(CastingEnvironment env) {
            state.owned -= 1;

            if (!state.hexes.isEmpty()) {
                String key = state.hexes.keySet().iterator().next();
                state.hexes.remove(key);
            }

            ItemStack cassette = new ItemStack(HexcassettesMain.CASSETTE.get());
            if (!player.addItem(cassette)) {
                player.drop(cassette, false);
            }

            state.sync(player);
        }
    }

    public static class NoCassettesToEject extends Mishap {
        @Override
        public FrozenPigment accentColor(CastingEnvironment env, Mishap.Context errorCtx) {
            return dyeColor(DyeColor.RED);
        }

        @Override
        public Component errorMessage(CastingEnvironment env, Mishap.Context errorCtx) {
            return Component.translatable(HexcassettesMain.MOD_ID + ":no_cassettes_to_eject");
        }

        @Override
        public void execute(CastingEnvironment env, Mishap.Context errorCtx, List<Iota> stack) {}
    }
}