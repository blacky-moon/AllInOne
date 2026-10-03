package miyucomics.hexical.mixin;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.GarbageIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.Vec3Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem;
import at.petrak.hexcasting.common.casting.actions.rw.OpRead;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import miyucomics.hexical.inits.HexicalItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = OpRead.class, remap = false)
public class OpReadMixin {

    @WrapMethod(method = "execute")
    private List<Iota> readFromCompassCurio(List<Iota> args, CastingEnvironment env, Operation<List<Iota>> original) throws MishapBadOffhandItem {
        try {
            return original.call(args, env);
        } catch (Throwable exception) {
            // В 1.21+ используем DataComponents вместо getNbt()
            @SuppressWarnings("DataFlowIssue")
            CastingEnvironment.HeldItemInfo data = env.getHeldItemToOperateOn(item -> {
                if (!item.is(HexicalItems.CURIO_COMPASS)) return false;
                CustomData customData = item.get(DataComponents.CUSTOM_DATA);
                if (customData == null) return false;
                CompoundTag tag = customData.copyTag();
                return tag.contains("needle", Tag.TAG_INT_ARRAY) && tag.getIntArray("needle").length >= 3;
            });

            if (data == null)
                throw MishapBadOffhandItem.of(null, "iota.read");

            LivingEntity caster = env.getCastingEntity();
            if (caster == null)
                return List.of(new GarbageIota());

            @SuppressWarnings("DataFlowIssue")
            int[] coordinates = data.stack().get(DataComponents.CUSTOM_DATA).copyTag().getIntArray("needle");

            // Vec3d -> Vec3, getEyePos() -> getEyePosition()
            return List.of(new Vec3Iota(new Vec3(coordinates[0], coordinates[1], coordinates[2]).subtract(caster.getEyePosition()).normalize()));
        }
    }

    @Inject(
            method = "execute",
            at = @At(
                    value = "INVOKE",
                    target = "Lat/petrak/hexcasting/api/addldata/ADIotaHolder;readIota()Lat/petrak/hexcasting/api/casting/iota/Iota;"
            )
    )
    private static void dontEatTwoHexbursts(List<? extends Iota> args, CastingEnvironment env, CallbackInfoReturnable<List<Iota>> cir, @Local(name = "handStack") ItemStack handStack) {
        if (handStack.is(HexicalItems.HEXBURST_ITEM))
            handStack.grow(1);
    }
}