package org.arcticquests.dev.oneironaut.oneironautt.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FrostedIceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautBlockRegistry;
import org.jetbrains.annotations.Nullable;

public class FrostedMediaIceBlock extends FrostedIceBlock {
    public FrostedMediaIceBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected void melt(BlockState state, Level world, BlockPos pos) {
        if (world.dimensionType().ultraWarm()) {
            world.removeBlock(pos, false);
            return;
        }
        world.setBlockAndUpdate(pos, OneironautBlockRegistry.THOUGHT_SLURRY_BLOCK.get().defaultBlockState());
        world.neighborChanged(pos, OneironautBlockRegistry.THOUGHT_SLURRY_BLOCK.get(), null);
    }

    @Override
    public void playerDestroy(Level world, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
        super.playerDestroy(world, player, pos, state, blockEntity, stack);

        var silkTouch = world.registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(Enchantments.SILK_TOUCH);

        if (EnchantmentHelper.getItemEnchantmentLevel(silkTouch, stack) == 0) {
            if (world.dimensionType().ultraWarm()) {
                world.removeBlock(pos, false);
                return;
            }
            BlockState material = world.getBlockState(pos.below());
            if (material.blocksMotion() || material.liquid()) {
                world.setBlockAndUpdate(pos, OneironautBlockRegistry.THOUGHT_SLURRY_BLOCK.get().defaultBlockState());
            }
        }
    }
}