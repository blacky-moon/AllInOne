package dev.kineticcat.complexhex.entity;

import at.petrak.hexcasting.api.pigment.FrozenPigment;
import dev.kineticcat.complexhex.api.util.Expr;
import dev.kineticcat.complexhex.api.util.Symbol;
import dev.kineticcat.complexhex.api.util.Value;
import dev.kineticcat.complexhex.api.util.Vector;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public class ParametricSurfaceEntity extends Entity {
    private static final EntityDataAccessor<CompoundTag> EXPRESSION =
            SynchedEntityData.defineId(ParametricSurfaceEntity.class, EntityDataSerializers.COMPOUND_TAG);
    private static final EntityDataAccessor<at.petrak.hexcasting.api.pigment.FrozenPigment> PIGMENT =
            SynchedEntityData.defineId(ParametricSurfaceEntity.class, ParametricLineEntity.PIGMENT_SERIALIZER);
    public static final String EXPR_TAG = "Xpression";
    public static final String PIGMENT_TAG = "Pigment";

    public ParametricSurfaceEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public Expr getExpr() { return Expr.deserialise(entityData.get(EXPRESSION));}
    public void setExpr(Expr expr) {entityData.set(EXPRESSION, expr.serialise());}

    public FrozenPigment getPigment() { return entityData.get(PIGMENT); }
    public void setPigment(FrozenPigment pigment) { entityData.set(PIGMENT, pigment); }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(EXPRESSION, new Vector(Symbol.U, Symbol.V, new Value(0.0)).serialise());
        builder.define(PIGMENT, FrozenPigment.DEFAULT.get());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        entityData.set(EXPRESSION, compoundTag.getCompound(EXPR_TAG));
        FrozenPigment.CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE, compoundTag.getCompound(PIGMENT_TAG))
                .resultOrPartial(err -> {})
                .ifPresent(p -> entityData.set(PIGMENT, p));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.put(EXPR_TAG, entityData.get(EXPRESSION));
        FrozenPigment.CODEC.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE, entityData.get(PIGMENT))
                .resultOrPartial(err -> {})
                .ifPresent(tag -> compoundTag.put(PIGMENT_TAG, tag));
    }

    @Override
    public boolean shouldRender(double d, double e, double f) {
        return true;
    }

    @Override
    public @NotNull AABB getBoundingBoxForCulling() {
        return new AABB(position().add(-10, -10, -10), position().add(10, 10, 10));
    }
}