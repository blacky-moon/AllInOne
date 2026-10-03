package miyucomics.hexcassettes.actions;

import at.petrak.hexcasting.api.casting.RenderedSpell;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster;
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota;
import miyucomics.hexcassettes.HexcassettesMain; // <-- ДОБАВИТЬ
import miyucomics.hexcassettes.PlayerEntityMinterface;
import miyucomics.hexpose.iotas.TextIota;
import net.minecraft.network.chat.Component;

import java.util.List;

public class OpDequeue implements SpellAction {
    @Override
    public int getArgc() {
        return 1;
    }

    @Override
    public Result execute(List<? extends Iota> args, CastingEnvironment env) {
        if (!(env.getCastingEntity() instanceof PlayerEntityMinterface))
            throw new MishapBadCaster();

        Component pattern;
        if (args.get(0) instanceof TextIota ti) {
            pattern = ti.getText();
        } else {
            throw MishapInvalidIota.ofType(args.get(0), 0, "text");
        }

        return new Result(new Spell(pattern), 0L, List.of(), 0L);
    }

    private record Spell(Component pattern) implements RenderedSpell {
        @Override
        public void cast(CastingEnvironment env) {
            ((PlayerEntityMinterface) env.getCastingEntity()).getCassetteState().hexes.remove(
                    HexcassettesMain.serializeKey(pattern, env.getWorld().registryAccess())
            );
        }
    }
}