package dev.kineticcat.complexhex.block;

import at.petrak.hexcasting.common.lib.HexBlocks;
import com.mojang.datafixers.util.Pair;
import dev.kineticcat.complexhex.item.ComplexHexCreativeTabs;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static dev.kineticcat.complexhex.Complexhex.id;

public class ComplexHexBlocks {
    /**
     * Registers every block collected in BLOCKS into the vanilla block
     * registry. MUST be called from a RegisterEvent listener for
     * Registries.BLOCK (while that registry is still open), NOT from
     * FMLCommonSetupEvent/FMLClientSetupEvent - by then the registry is
     * frozen and Registry.register throws IllegalStateException.
     *
     * Merely referencing this class (e.g. ComplexHexBlocks.HEXBOX) triggers
     * its <clinit>, which runs `new RecordBlock(...)` etc. - and in this MC
     * version, the Block constructor itself calls
     * MappedRegistry.createIntrusiveHolder, which ALSO requires the
     * registry to still be open. So the class must be touched (first,
     * to run <clinit>) and this method called, both while Registries.BLOCK
     * is open - i.e. both must happen inside the RegisterEvent(BLOCK)
     * branch, not later.
     */
    public static void init() {
        for (var e : BLOCKS.entrySet()) {
            Registry.register(BuiltInRegistries.BLOCK, e.getKey(), e.getValue());
        }
    }

    public static void registerBlocks(BiConsumer<Block, ResourceLocation> r) {
        for (var e : BLOCKS.entrySet()) {
            r.accept(e.getValue(), e.getKey());
        }
    }

    // Filled in by registerBlockItems() the first (and only) time it's
    // called, so the creative tab step below can hand out the SAME
    // BlockItem instance instead of constructing a second, unregistered one.
    private static final Map<ResourceLocation, Item> CREATED_BLOCK_ITEMS = new LinkedHashMap<>();

    public static void registerBlockItems(BiConsumer<Item, ResourceLocation> r) {
        for (var e : BLOCK_ITEMS.entrySet()) {
            Item item = new BlockItem(e.getValue().getFirst(), e.getValue().getSecond());
            CREATED_BLOCK_ITEMS.put(e.getKey(), item);
            r.accept(item, e.getKey());
        }
    }
    public static void registerBlockCreativeTab(Consumer<Item> r, CreativeModeTab tab) {
        for (var block : BLOCK_TABS.getOrDefault(tab, List.of())) {
            ResourceLocation blockId = BLOCKS.entrySet().stream()
                    .filter(e -> e.getValue() == block)
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElseThrow();
            Item item = CREATED_BLOCK_ITEMS.get(blockId);
            if (item != null) {
                r.accept(item);
            }
        }
    }
    private static final Map<ResourceLocation, Block> BLOCKS = new LinkedHashMap<>();
    private static final Map<ResourceLocation, Pair<Block, Item.Properties>> BLOCK_ITEMS = new LinkedHashMap<>();
    private static final Map<CreativeModeTab, List<Block>> BLOCK_TABS = new LinkedHashMap<>();

    public static final RecordBlock.Inert INERT_RECORD = block(
            "inert_record",
            new RecordBlock.Inert(Blocks.AMETHYST_BLOCK.properties().pushReaction(PushReaction.DESTROY))
    );
    public static final RecordBlock.Quenched QUENCHED_RECORD = block(
            "quenched_record",
            new RecordBlock.Quenched(Blocks.AMETHYST_BLOCK.properties())
    );

    public static final HexboxBlock HEXBOX = blockItem(
            "hexbox",
            new HexboxBlock(HexBlocks.SLATE_BLOCK.properties().pushReaction(PushReaction.BLOCK)),
            new Item.Properties()
    );

    public static <T extends Block> T block(String name, T block) {
        Block old = BLOCKS.put(id(name), block);
        if (old != null) {
            throw new IllegalArgumentException("complexhex's dev's a dumbass: " + name);
        }
        return block;
    }
    public static <T extends Block> T blockItem(String name, T block, Item.Properties props, CreativeModeTab tab) {
        block(name, block);
        Pair<Block, Item.Properties> old = BLOCK_ITEMS.put(id(name), new Pair<>(block, props));
        if (old != null) {
            throw new IllegalArgumentException("complexhex's dev's a dumbass: " + name);
        }
        BLOCK_TABS.computeIfAbsent(tab, t-> new ArrayList<>()).add(block);
        return block;
    }
    public static <T extends Block> T blockItem(String name, T block, Item.Properties props) {
        return blockItem(name, block, props, ComplexHexCreativeTabs.COMPLEXHEX);
    }

}