package miyucomics.hexpose.actions.misc;

import at.petrak.hexcasting.api.addldata.ADMediaHolder;
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.circles.BlockEntityAbstractImpetus;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.*;
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.mod.HexConfig;
import at.petrak.hexcasting.api.utils.MediaHelper;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import miyucomics.hexpose.iotas.ItemStackIota;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.item.ItemEntity;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class OpGetMedia implements ConstMediaAction {
    public static final OpGetMedia INSTANCE = new OpGetMedia();

    private OpGetMedia() {}

    @Override
    public int getArgc() {
        return 1;
    }

    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment env) {
        Iota arg = args.get(0);
        Iota result;

        if (arg instanceof EntityIota) {
            Entity entity = ((EntityIota) arg).getEntity(env.getWorld());
            if (entity == null) {
                result = new NullIota();
            } else {
                env.assertEntityInRange(entity);

                if (entity instanceof Allay) {
                    result = new DoubleIota(100.0);
                } else if (entity instanceof ItemEntity) {
                    var holder = IXplatAbstractions.INSTANCE.findMediaHolder(((ItemEntity) entity).getItem());
                    if (holder == null) {
                        result = new NullIota();
                    } else {
                        result = new DoubleIota(holder.getMedia() / (double) MediaConstants.DUST_UNIT);
                    }
                } else if (entity instanceof ServerPlayer player) {
                    var sources = MediaHelper.scanPlayerForMediaStuff(player);
                    long costLeft = Long.MAX_VALUE;

                    for (var source : sources) {
                        long found = MediaHelper.extractMedia(source, costLeft, false, true);
                        costLeft -= found;
                        if (costLeft <= 0) break;
                    }

                    if (costLeft > 0) {
                        double mediaToHealth = HexConfig.common().mediaToHealthRate();
                        long healthToRemove = (long) Math.max(costLeft / mediaToHealth, 0.5);
                        long simulatedRemovedMedia = (long) Math.ceil(Math.min(player.getHealth() * mediaToHealth, healthToRemove));
                        costLeft -= simulatedRemovedMedia;
                    }

                    result = new DoubleIota((Long.MAX_VALUE - costLeft) / (double) MediaConstants.DUST_UNIT);
                } else {
                    result = new NullIota();
                }
            }
        } else if (arg instanceof Vec3Iota) {
            Vec3 vec = ((Vec3Iota) arg).getVec3();
            BlockPos pos = BlockPos.containing(vec);
            env.assertPosInRange(pos);

            var state = env.getWorld().getBlockState(pos);
            var blockEntity = env.getWorld().getBlockEntity(pos);

            Long media = null;
            if (state.getBlock() instanceof ADMediaHolder) {
                media = ((ADMediaHolder) state.getBlock()).getMedia();
            } else if (blockEntity instanceof ADMediaHolder) {
                media = ((ADMediaHolder) blockEntity).getMedia();
            } else if (blockEntity instanceof BlockEntityAbstractImpetus) {
                media = ((BlockEntityAbstractImpetus) blockEntity).getMedia();
            }

            if (media != null) {
                result = new DoubleIota(media / (double) MediaConstants.DUST_UNIT);
            } else {
                result = new NullIota();
            }
        } else if (arg instanceof ItemStackIota) {
            ItemStack stack = ((ItemStackIota) arg).getStack();
            var holder = IXplatAbstractions.INSTANCE.findMediaHolder(stack);
            if (holder == null) {
                result = new NullIota();
            } else {
                result = new DoubleIota(holder.getMedia() / (double) MediaConstants.DUST_UNIT);
            }
        } else {
            throw MishapInvalidIota.of(args.get(0), 0, "media_holding");
        }

        return List.of(result);
    }
}