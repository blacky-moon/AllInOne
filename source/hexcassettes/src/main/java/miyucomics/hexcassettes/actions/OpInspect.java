package miyucomics.hexcassettes.actions;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.BooleanIota;
import at.petrak.hexcasting.api.casting.iota.EntityIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota;
import miyucomics.hexcassettes.HexcassettesMain;
import miyucomics.hexcassettes.PlayerEntityMinterface;
import miyucomics.hexpose.iotas.TextIota;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class OpInspect implements ConstMediaAction {
    @Override
    public int getArgc() {
        return 2;
    }

    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment env) {
        Iota playerIota = args.get(0);
        if (!(playerIota instanceof EntityIota entityIota))
            throw MishapInvalidIota.ofType(playerIota, 0, "entity");

        Entity entity = entityIota.getEntity(env.getWorld());
        if (!(entity instanceof Player player))
            throw MishapInvalidIota.ofType(playerIota, 0, "player");

        Component text;
        if (args.get(1) instanceof TextIota ti) {
            text = ti.getText();
        } else {
            throw MishapInvalidIota.ofType(args.get(1), 1, "text");
        }

        String pattern = HexcassettesMain.serializeKey(text, env.getWorld().registryAccess());
        boolean result = ((PlayerEntityMinterface) player).getCassetteState().hexes.containsKey(pattern);
        return List.of(new BooleanIota(result));
    }
}