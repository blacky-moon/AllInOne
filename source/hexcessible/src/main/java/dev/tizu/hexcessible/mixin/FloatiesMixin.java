package dev.tizu.hexcessible.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import at.petrak.hexcasting.api.client.ClientRenderHelper;
import dev.tizu.hexcessible.Hexcessible;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.player.Player;

@Mixin(ClientRenderHelper.class)
public class FloatiesMixin {
    private FloatiesMixin() {
    }

    @WrapMethod(method = "renderCastingStack", remap = false)
    private static void renderCastingStack(PoseStack ps, Player player, float pticks,
                                           Operation<Void> original) {
        if (!Hexcessible.cfg().hideFloaties)
            original.call(ps, player, pticks);
    }
}