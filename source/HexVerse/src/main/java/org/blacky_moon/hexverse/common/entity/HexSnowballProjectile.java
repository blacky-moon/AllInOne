package org.blacky_moon.hexverse.common.entity;

import at.petrak.hexcasting.api.addldata.ADHexHolder;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.iota.EntityIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.Vec3Iota;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.common.lib.HexDataComponents;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import miyucomics.hexpose.iotas.IdentifierIota;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.blacky_moon.hexverse.common.CommonInit;
import org.blacky_moon.hexverse.common.casting.HexSnowballCastEnv;
import org.blacky_moon.hexverse.common.item.HexHolder;
import org.blacky_moon.hexverse.common.item.HexSnowballItem;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HexSnowballProjectile extends ThrowableItemProjectile {

    private static final EntityDataAccessor<ItemStack> DATA_ITEM_STACK =
            SynchedEntityData.defineId(HexSnowballProjectile.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Long> DATA_MAX_MEDIA =
            SynchedEntityData.defineId(HexSnowballProjectile.class, EntityDataSerializers.LONG);

    private long maxMedia = 0;

    public HexSnowballProjectile(EntityType<? extends HexSnowballProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public HexSnowballProjectile(Level level, LivingEntity shooter) {
        super(CommonInit.HEX_SNOWBALL_PROJECTILE.get(), shooter, level);
    }
    public HexSnowballProjectile(Level level, Vec3 position) {
        super(CommonInit.HEX_SNOWBALL_PROJECTILE.get(), level);
        this.setPos(position);
        this.setOwner(null); // Явно указываем, что владельца нет
    }
    public void setMaxMedia(long maxMedia) {
        this.maxMedia = maxMedia;
        this.getEntityData().set(DATA_MAX_MEDIA, maxMedia);
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return CommonInit.HEX_SNOWBALL_DUST.get();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ITEM_STACK, new ItemStack(this.getDefaultItem()));
        builder.define(DATA_MAX_MEDIA, 0L);
    }

    @Override
    public void setItem(ItemStack stack) {
        this.getEntityData().set(DATA_ITEM_STACK, stack.copyWithCount(1));
        if (stack.getItem() instanceof HexSnowballItem snowballItem) {
            this.maxMedia = snowballItem.getMaxMedia();
            this.getEntityData().set(DATA_MAX_MEDIA, this.maxMedia);
        }
    }

    @Override
    public @NotNull ItemStack getItem() {
        return this.getEntityData().get(DATA_ITEM_STACK);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("Item", this.getItem().save(this.registryAccess()));
        tag.putLong("MaxMedia", this.getEntityData().get(DATA_MAX_MEDIA));
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Item", 10)) {
            this.setItem(ItemStack.parse(this.registryAccess(), tag.getCompound("Item"))
                    .orElseGet(() -> new ItemStack(this.getDefaultItem())));
        } else {
            this.setItem(new ItemStack(this.getDefaultItem()));
        }
        if (tag.contains("MaxMedia")) {
            this.maxMedia = tag.getLong("MaxMedia");
            this.getEntityData().set(DATA_MAX_MEDIA, this.maxMedia);
        }
    }

    private ParticleOptions getParticle() {
        ItemStack itemstack = this.getItem();
        return new ItemParticleOption(ParticleTypes.ITEM, itemstack);
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            ParticleOptions particleoptions = this.getParticle();
            for (int i = 0; i < 8; ++i) {
                this.level().addParticle(particleoptions, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        super.onHitEntity(result);

        if (!this.level().isClientSide) {
            LivingEntity target = (LivingEntity) result.getEntity();
            if (hasHexInItem()) {
                executeSpell(target, null);
            }
            this.level().broadcastEntityEvent(this, (byte) 3);
            this.discard();
        }
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult result) {
        super.onHitBlock(result);

        if (!this.level().isClientSide) {
            BlockPos pos = result.getBlockPos();
            ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(
                    this.level().getBlockState(pos).getBlock()
            );
            if (hasHexInItem()) {
                executeSpell(blockId, pos);
            }
            this.level().broadcastEntityEvent(this, (byte) 3);
            this.discard();
        }
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        super.onHit(result);
    }

    private boolean hasHexInItem() {
        ItemStack stack = this.getItem();
        return stack.has(HexDataComponents.HEX_HOLDER_PATTERNS);
    }

    private ADHexHolder getHexHolder(ItemStack stack) {
        return IXplatAbstractions.INSTANCE.findHexHolder(stack);
    }


    // В HexSnowballProjectile.java - замените метод executeSpell

    private void executeSpell(LivingEntity targetEntity, ResourceLocation blockId) {
        // Получаем владельца (может быть null для раздатчика)
        LivingEntity caster = null;
        if (this.getOwner() instanceof LivingEntity owner) {
            caster = owner;
        }

        ItemStack stack = this.getItem();
        if (!(stack.getItem() instanceof HexHolder hexHolder)) return;
        if (!hexHolder.hasHex(stack)) return;

        List<Iota> spell = hexHolder.getHex(stack, (ServerLevel) this.level());
        if (spell == null || spell.isEmpty()) return;

        try {
            List<Iota> initialStack;
            Vec3 castCenter;

            if (targetEntity != null) {
                castCenter = targetEntity.position();
                initialStack = List.of(new EntityIota(targetEntity));
            } else {
                castCenter = this.position();
                initialStack = List.of(
                        new IdentifierIota(blockId),
                        new Vec3Iota(this.position())
                );
            }

            double radius = 8.0;
            FrozenPigment pigment = hexHolder.getPigment(stack);
            if (pigment == null) {
                pigment = FrozenPigment.DEFAULT.get();
            }

            long maxMedia = hexHolder.getMaxMedia(stack);
            long currentMedia = hexHolder.getMedia(stack);

            // Используем обновленный конструктор
            var env = new HexSnowballCastEnv(
                    (ServerLevel) this.level(),
                    caster, // может быть null
                    castCenter,
                    radius,
                    pigment,
                    stack,
                    currentMedia,
                    maxMedia
            );

            CastingVM vm = new CastingVM(
                    new CastingImage(
                            initialStack, 0, new ArrayList<>(),
                            false, false, 0L,
                            new CompoundTag()
                    ),
                    env
            );

            vm.queueExecuteAndWrapIotas(spell, (ServerLevel) this.level());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeSpell(ResourceLocation blockId, BlockPos pos) {
        executeSpell(null, blockId);
    }
}