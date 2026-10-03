package org.blacky_moon.hexverse.common.item;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.PatternIota;
import at.petrak.hexcasting.api.item.HexHolderItem;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.common.lib.HexDataComponents;
import at.petrak.hexcasting.common.lib.hex.HexActions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public abstract class HexToolItem extends Item implements HexHolderItem {

    private final Tier tier;

    public HexToolItem(Tier tier, Properties properties) {
        super(properties);
        this.tier = tier;
    }

    public Tier getTier() {
        return this.tier;
    }

    @Override
    public boolean canDrawMediaFromInventory(ItemStack stack) {
        return false;
    }

    @Override
    public boolean hasHex(ItemStack stack) {
        return stack.has(HexDataComponents.HEX_HOLDER_PATTERNS);
    }

    @Nullable
    @Override
    public List<Iota> getHex(ItemStack stack, ServerLevel level) {
        if (stack.has(HexDataComponents.HEX_HOLDER_PATTERNS)) {
            return stack.get(HexDataComponents.HEX_HOLDER_PATTERNS);
        }
        return null;
    }

    @Override
    public void writeHex(ItemStack stack, List<Iota> patterns, @Nullable FrozenPigment pigment, long media) {
        if (patterns != null && !patterns.isEmpty()) {
            stack.set(HexDataComponents.HEX_HOLDER_PATTERNS, patterns);
        }
        if (pigment != null) {
            stack.set(HexDataComponents.PIGMENT, pigment);
        }
    }

    @Override
    public void clearHex(ItemStack stack) {
        stack.remove(HexDataComponents.HEX_HOLDER_PATTERNS);
        stack.remove(HexDataComponents.PIGMENT);
    }

    @Nullable
    @Override
    public FrozenPigment getPigment(ItemStack stack) {
        if (stack.has(HexDataComponents.PIGMENT)) {
            return stack.get(HexDataComponents.PIGMENT);
        }
        return FrozenPigment.DEFAULT.get();
    }

    @Override
    public long getMedia(ItemStack stack) {
        return 0;
    }

    @Override
    public long getMaxMedia(ItemStack stack) {
        return 0;
    }

    @Override
    public void setMedia(ItemStack stack, long media) {
        // Ничего не делаем
    }

    @Override
    public boolean canProvideMedia(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canRecharge(ItemStack stack) {
        return false;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);

        if (hasHex(stack)) {
            FrozenPigment pigment = getPigment(stack);
            int color;

            if (pigment != null) {
                var colorProvider = pigment.getColorProvider();
                if (colorProvider != null) {
                    long time = context.level() != null ? Objects.requireNonNull(context.level()).getGameTime() : 0;
                    color = colorProvider.getColor((float)(time / 5 % 814621), Vec3.ZERO);
                } else {
                    color = 0xFFFFFF;
                }
            } else {
                color = 0xFFFFFF;
            }

            Component evalRune = PatternIota.display(HexActions.EVAL.prototype());
            Component coloredRune = evalRune.copy()
                    .withStyle(style -> style.withColor(color));
            tooltip.add(coloredRune);
        }
    }

    @Override
    public int getMaxDamage(@NotNull ItemStack stack) {
        return this.tier.getUses();
    }

    @Override
    public boolean isRepairable(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public boolean isValidRepairItem(@NotNull ItemStack stack, @NotNull ItemStack repairCandidate) {
        return this.tier.getRepairIngredient().test(repairCandidate);
    }
}