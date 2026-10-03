package miyucomics.hexcassettes.actions;

import at.petrak.hexcasting.api.casting.RenderedSpell;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster;
import miyucomics.hexcassettes.PlayerEntityMinterface;
import miyucomics.hexcassettes.data.CassetteState;

import java.util.List;

public class OpKillAll implements SpellAction {
    @Override
    public int getArgc() {
        return 0;
    }

    @Override
    public Result execute(List<? extends Iota> args, CastingEnvironment env) {
        var caster = env.getCastingEntity();
        if (!(caster instanceof PlayerEntityMinterface minterface))
            throw new MishapBadCaster();
        return new Result(new Spell(minterface.getCassetteState()), 0L, List.of(), 0L);
    }

    private record Spell(CassetteState state) implements RenderedSpell {
        @Override
        public void cast(CastingEnvironment env) {
            state.hexes.clear();
        }
    }
}