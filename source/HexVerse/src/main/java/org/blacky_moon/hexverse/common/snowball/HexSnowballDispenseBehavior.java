package org.blacky_moon.hexverse.common.snowball;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;
import org.blacky_moon.hexverse.common.entity.HexSnowballProjectile;
import org.blacky_moon.hexverse.common.item.HexSnowballItem;
import org.jetbrains.annotations.NotNull;


public class HexSnowballDispenseBehavior extends DefaultDispenseItemBehavior {

    @Override
    public @NotNull ItemStack execute(BlockSource source, @NotNull ItemStack stack) {
        Level level = source.level();
        BlockPos pos = source.pos();
        Direction direction = source.state().getValue(DispenserBlock.FACING);

        if (!level.isClientSide) {
            Vec3 spawnPos = new Vec3(
                    pos.getX() + 0.5 + direction.getStepX() * 1.5,
                    pos.getY() + 0.5 + direction.getStepY() * 1.5,
                    pos.getZ() + 0.5 + direction.getStepZ() * 1.5
            );

            HexSnowballProjectile projectile = new HexSnowballProjectile(level, spawnPos);
            projectile.setItem(stack.copy());

            if (stack.getItem() instanceof HexSnowballItem snowballItem) {
                projectile.setMaxMedia(snowballItem.getMaxMedia());
            }

            projectile.shoot(
                    direction.getStepX(),
                    direction.getStepY(),
                    direction.getStepZ(),
                    1.5F, 1.0F
            );

            level.addFreshEntity(projectile);
            level.levelEvent(1002, pos, 0);
        }

        stack.shrink(1);
        return stack;
    }
}