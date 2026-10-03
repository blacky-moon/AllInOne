package dev.kineticcat.complexhex.block.entity;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import dev.kineticcat.complexhex.block.ComplexHexBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class ComplexHexBlockEntities {
    /**
     * Registers every block entity type in BLOCK_ENTITIES into the vanilla
     * registry. MUST be called from a RegisterEvent listener for
     * Registries.BLOCK_ENTITY_TYPE while that registry is open, and only
     * AFTER ComplexHexBlocks.init() has run - the BlockEntityType built
     * here (HEXBOX) references ComplexHexBlocks.HEXBOX, which must already
     * be a real, registered Block instance.
     */
    public static void init() {
        for (var e : BLOCK_ENTITIES.entrySet()) {
            Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, e.getKey(), e.getValue());
        }
    }

    public static void registerTiles(BiConsumer<BlockEntityType<?>, ResourceLocation> r) {
        for (var e : BLOCK_ENTITIES.entrySet()) {
            r.accept(e.getValue(), e.getKey());
        }
    }

    private static final Map<ResourceLocation, BlockEntityType<?>> BLOCK_ENTITIES = new LinkedHashMap<>();

    public static final BlockEntityType<HexboxBlockEntity> HEXBOX = register(
            "hexbox",
            HexboxBlockEntity::new,
            ComplexHexBlocks.HEXBOX
    );

    private static <T extends BlockEntity> BlockEntityType<T> register(String id,
                                                                       BiFunction<BlockPos, BlockState, T> func, Block... blocks) {
        var ret = IXplatAbstractions.INSTANCE.createBlockEntityType(func, blocks);
        var old = BLOCK_ENTITIES.put(ResourceLocation.fromNamespaceAndPath(HexAPI.MOD_ID, id), ret);
        if (old != null) {
            throw new IllegalArgumentException("Duplicate id " + id);
        }
        return ret;
    }
}