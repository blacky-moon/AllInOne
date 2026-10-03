package miyucomics.hexcassettes.actions;

import at.petrak.hexcasting.api.casting.castables.Action;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.*;
import at.petrak.hexcasting.api.casting.math.EulerPathFinder;
import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.api.casting.mishaps.Mishap;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster;
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota;
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds;
import com.mojang.serialization.DataResult;
import miyucomics.hexcassettes.CassetteCastEnv;
import miyucomics.hexcassettes.HexcassettesMain;
import miyucomics.hexcassettes.PlayerEntityMinterface;
import miyucomics.hexcassettes.data.QueuedHex;
import miyucomics.hexpose.iotas.TextIota;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;

import java.util.ArrayList;
import java.util.List;

public class OpEnqueue implements Action {

    @Override
    public OperationResult operate(CastingEnvironment env, CastingImage image, SpellContinuation continuation) {
        var caster = env.getCastingEntity();
        if (!(caster instanceof PlayerEntityMinterface minterface))
            throw new MishapBadCaster();
        if (image.getStack().size() < 2)
            throw new MishapNotEnoughArgs(2, image.getStack().size());

        var cassetteState = minterface.getCassetteState();
        var stack = new ArrayList<Iota>(image.getStack());

        String key;
        if (env instanceof CassetteCastEnv cassetteEnv && !cassetteState.hexes.containsKey(cassetteEnv.key)) {
            key = cassetteEnv.key;
        } else {
            key = HexcassettesMain.serializeKey(
                    PatternIota.display(EulerPathFinder.findAltDrawing(
                            HexPattern.fromAngles("qeqwqwqwqwqeqaweqqqqqwweeweweewqdwwewewwewweweww", HexDir.EAST),
                            env.getWorld().getGameTime()
                    )),
                    env.getWorld().registryAccess()
            );
        }

        int labelled = 0;
        if (stack.get(stack.size() - 1) instanceof TextIota textIota) {
            stack.remove(stack.size() - 1);
            key = HexcassettesMain.serializeKey(textIota.getText(), env.getWorld().registryAccess());
            labelled++;
        }

        var potentialDelay = stack.remove(stack.size() - 1);
        if (!(potentialDelay instanceof DoubleIota doubleIota) ||
                doubleIota.getDouble() - Math.round(doubleIota.getDouble()) > DoubleIota.TOLERANCE ||
                Math.round(doubleIota.getDouble()) <= 0) {
            throw MishapInvalidIota.of(potentialDelay, labelled, "int.positive");
        }

        var potentialHex = stack.remove(stack.size() - 1);
        if (!(potentialHex instanceof ListIota))
            throw MishapInvalidIota.ofType(potentialHex, labelled + 1, "list");

        if (!cassetteState.hexes.containsKey(key) && cassetteState.hexes.size() >= cassetteState.owned)
            throw new NoFreeCassettes();

        int depth = 0;
        if (env instanceof CassetteCastEnv cassetteEnv && cassetteEnv.depth < 10)
            depth = cassetteEnv.depth + 1;

        DataResult<Tag> serialized = IotaType.TYPED_CODEC.encodeStart(NbtOps.INSTANCE, potentialHex);
        cassetteState.hexes.put(key, new QueuedHex((CompoundTag) serialized.getOrThrow(), (int) Math.round(doubleIota.getDouble()), depth));
        if (labelled == 0)
            stack.add(new TextIota(HexcassettesMain.deserializeKey(key, env.getWorld().registryAccess())));

        CastingImage newImage = image.copy(
                stack,
                image.getParenCount(),
                image.getParenthesized(),
                image.getEscapeNext(),
                image.getSimulateNext(),
                image.getOpsConsumed(),
                image.getUserData()
        );

        return new OperationResult(newImage, List.of(), continuation, HexEvalSounds.SPELL);
    }

    public static class NoFreeCassettes extends Mishap {
        @Override
        public FrozenPigment accentColor(CastingEnvironment env, Mishap.Context errorCtx) {
            return dyeColor(DyeColor.RED);
        }

        @Override
        public Component errorMessage(CastingEnvironment env, Mishap.Context errorCtx) {
            return Component.translatable(HexcassettesMain.MOD_ID + ":no_free_cassettes");
        }

        @Override
        public void execute(CastingEnvironment env, Mishap.Context errorCtx, List<Iota> stack) {}
    }
}