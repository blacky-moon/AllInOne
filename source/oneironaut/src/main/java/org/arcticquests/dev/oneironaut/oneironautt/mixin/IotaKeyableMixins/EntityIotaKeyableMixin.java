package org.arcticquests.dev.oneironaut.oneironautt.mixin.IotaKeyableMixins;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.EntityIota;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import org.arcticquests.dev.oneironaut.oneironautt.MiscAPIKt;
import org.arcticquests.dev.oneironaut.oneironautt.casting.idea.IdeaKeyable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.UUID;

@Mixin(EntityIota.class)
public abstract class EntityIotaKeyableMixin implements IdeaKeyable {

    @Shadow
    public abstract UUID getEntityId();

    @Shadow
    public abstract Entity getEntity(ServerLevel level);

    @Override
    public String getKey() {
        return this.getEntityId().toString();
    }

    @Override
    public boolean isValidKey(CastingEnvironment env) {
        Entity entity = this.getEntity((ServerLevel) env.getWorld());
        if (entity == null) {
            return false;
        }
        env.assertEntityInRange(entity);
        return (entity instanceof ServerPlayer player && MiscAPIKt.isPlayerEnlightened(player))
                || (entity instanceof Villager villager && IXplatAbstractions.INSTANCE.isBrainswept(villager))
                || (entity instanceof WanderingTrader trader && IXplatAbstractions.INSTANCE.isBrainswept(trader));
    }
}