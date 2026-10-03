package org.blacky_moon.hexverse.common.item;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.blacky_moon.hexverse.common.entity.HexSnowballProjectile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class HexSnowballItem extends Item implements HexHolder {

    public static Supplier<HexSnowballItem> HEX_SNOWBALL_DUST;
    public static Supplier<HexSnowballItem> HEX_SNOWBALL_SHARD;
    public static Supplier<HexSnowballItem> HEX_SNOWBALL_CRYSTAL;
    public static Supplier<HexSnowballItem> HEX_SNOWBALL_QUENCHED;

    public static final long MAX_MEDIA_DUST = MediaConstants.DUST_UNIT;
    public static final long MAX_MEDIA_SHARD = MediaConstants.SHARD_UNIT;
    public static final long MAX_MEDIA_CRYSTAL = MediaConstants.CRYSTAL_UNIT;
    public static final long MAX_MEDIA_QUENCHED = MediaConstants.QUENCHED_SHARD_UNIT;

    private final HexHolderDelegate delegate;

    public HexSnowballItem(Properties properties, long maxMedia) {
        super(properties);
        this.delegate = new HexHolderDelegate(maxMedia);
    }

    public long getMaxMedia() {
        return delegate.getMaxMedia(null);
    }


    @Override
    public boolean hasHex(ItemStack stack) {
        return delegate.hasHex(stack);
    }

    @Nullable
    @Override
    public List<Iota> getHex(ItemStack stack, ServerLevel level) {
        return delegate.getHex(stack, level);
    }

    @Override
    public void writeHex(ItemStack stack, List<Iota> patterns, @Nullable FrozenPigment pigment, long media) {
        delegate.writeHex(stack, patterns, pigment, media);
    }

    @Override
    public void clearHex(ItemStack stack) {
        delegate.clearHex(stack);
    }

    @Nullable
    @Override
    public FrozenPigment getPigment(ItemStack stack) {
        return delegate.getPigment(stack);
    }

    @Override
    public long getMedia(ItemStack stack) {
        return delegate.getMedia(stack);
    }

    @Override
    public long getMaxMedia(ItemStack stack) {
        return delegate.getMaxMedia(stack);
    }

    @Override
    public void setMedia(ItemStack stack, long media) {
        delegate.setMedia(stack, media);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }

        if (!level.isClientSide) {
            if (!hasHex(stack)) {
                player.displayClientMessage(Component.translatable("hexverse.no.spell.stored"), true);
                return InteractionResultHolder.fail(stack);
            }



            HexSnowballProjectile projectile = new HexSnowballProjectile(level, player);
            projectile.setItem(stack.copy());
            projectile.setMaxMedia(getMaxMedia());
            projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(projectile);

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.SNOWBALL_THROW, SoundSource.PLAYERS, 0.5F, 0.8F);

            player.getCooldowns().addCooldown(this, 10);

            if (!player.isCreative()) {
                stack.shrink(1);
            }
        }

        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        delegate.appendHoverText(stack, tooltip);
    }
}