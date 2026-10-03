package miyucomics.hexcassettes;

import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;

public class CassetteCastEnv extends PlayerBasedCastEnv {
    public final String key;
    public final int depth;

    public CassetteCastEnv(ServerPlayer caster, InteractionHand castingHand, String key, int depth) {
        super(caster, castingHand);
        this.key = key;
        this.depth = depth;
        if (depth >= 9) {
            HexcassettesMain.QUINIO.get().trigger(caster);
        }
    }

    @Override
    public InteractionHand getCastingHand() {
        return this.castingHand;
    }

    @Override
    public void produceParticles(ParticleSpray particles, FrozenPigment pigment) {}

    @Override
    public FrozenPigment getPigment() {
        return IXplatAbstractions.INSTANCE.getPigment(this.caster);
    }

    @Override
    public long extractMediaEnvironment(long costLeft, boolean simulate) {
        if (caster.isCreative()) return 0;
        return this.extractMediaFromInventory(costLeft, true, simulate);
    }
}