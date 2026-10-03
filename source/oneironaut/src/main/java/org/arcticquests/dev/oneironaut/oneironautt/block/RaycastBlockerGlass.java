package org.arcticquests.dev.oneironaut.oneironautt.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class RaycastBlockerGlass extends TransparentBlock {
    public RaycastBlockerGlass(Properties settings) {
        super(settings
                .sound(SoundType.GLASS)
                .noOcclusion() // Отключает culling полных блоков
                .isValidSpawn((state, level, pos, type) -> false) // Запрещает спавн мобов
                .isRedstoneConductor((state, level, pos) -> false) // Не проводит редстоун
                .isSuffocating((state, level, pos) -> false) // Не удушает сущностей
                .isViewBlocking((state, level, pos) -> false) // Не перекрывает обзор
        );
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return false;
    }

    @Override
    protected int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return 15;
    }
}