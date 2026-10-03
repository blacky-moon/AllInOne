package org.arcticquests.dev.oneironaut.oneironautt.registry;

import at.petrak.hexcasting.api.block.circle.BlockCircleComponent;
import at.petrak.hexcasting.common.blocks.circles.BlockSlate;
import at.petrak.hexcasting.common.lib.HexBlocks;
import com.mojang.datafixers.types.Type;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut;
import org.arcticquests.dev.oneironaut.oneironautt.block.*;
import org.arcticquests.dev.oneironaut.oneironautt.block.blockentity.*;

import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public final class OneironautBlockRegistry {
    private OneironautBlockRegistry() {}

    private static final Type<?> NULL_TYPE = null;

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(Oneironaut.MODID);

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Oneironaut.MODID);

    public static void init(IEventBus modBus) {
        BLOCKS.register(modBus);
        BLOCK_ENTITIES.register(modBus);
    }

    // Donor: Blocks.AMETHYST_BLOCK
    public static final DeferredHolder<Block, Block> PSUEDOAMETHYST_BLOCK = BLOCKS.register("pseudoamethyst_block", () ->
            new Block(Blocks.AMETHYST_BLOCK.properties()
                    .destroyTime(1.5f)
                    .sound(SoundType.AMETHYST)
                    .explosionResistance(5)
                    .lightLevel(state -> 7)));

    // Donor: Blocks.AMETHYST_BLOCK (insubstantial variant)
    public static final DeferredHolder<Block, Block> PSUEDOAMETHYST_BLOCK_INSUBSTANTIAL = BLOCKS.register("insubstantial_pseudoamethyst_block", () ->
            new Block(Blocks.AMETHYST_BLOCK.properties()
                    .destroyTime(1f)
                    .sound(SoundType.AMETHYST)
                    .explosionResistance(4)
                    .lightLevel(state -> 5)
                    .noOcclusion()));

    // Donor: Blocks.BASALT
    public static final DeferredHolder<Block, Block> NOOSPHERE_BASALT = BLOCKS.register("noosphere_basalt", () ->
            new Block(Blocks.BASALT.properties()
                    .destroyTime(1f)
                    .sound(SoundType.BASALT)
                    .explosionResistance(4)));

    // Donor: Blocks.END_PORTAL
    public static final DeferredHolder<Block, NoosphereGateway> NOOSPHERE_GATE = BLOCKS.register("noosphere_gate", () ->
            new NoosphereGateway(Blocks.END_PORTAL.properties()
                    .lightLevel(state -> 15)
                    .noCollission()
                    .destroyTime(-1)));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<NoosphereGateEntity>> NOOSPHERE_GATE_ENTITY =
            BLOCK_ENTITIES.register("noosphere_gate_entity", () ->
                    BlockEntityType.Builder.of(NoosphereGateEntity::new, NOOSPHERE_GATE.get()).build(null));

    // Donor: Blocks.GLASS
    public static final DeferredHolder<Block, WispLantern> WISP_LANTERN = BLOCKS.register("wisp_lantern", () ->
            new WispLantern(Blocks.GLASS.properties()
                    .lightLevel(state -> 15)
                    .sound(SoundType.GLASS)));

    // Donor: Blocks.GLASS
    public static final DeferredHolder<Block, WispLanternTinted> WISP_LANTERN_TINTED = BLOCKS.register("wisp_lantern_tinted", () ->
            new WispLanternTinted(Blocks.GLASS.properties()
                    .sound(SoundType.GLASS)));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WispLanternEntity>> WISP_LANTERN_ENTITY =
            BLOCK_ENTITIES.register("wisp_lantern_entity", () ->
                    BlockEntityType.Builder.of(WispLanternEntity::new, WISP_LANTERN.get()).build(NULL_TYPE));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WispLanternEntityTinted>> WISP_LANTERN_ENTITY_TINTED =
            BLOCK_ENTITIES.register("wisp_lantern_entity_tinted", () ->
                    BlockEntityType.Builder.of(WispLanternEntityTinted::new, WISP_LANTERN_TINTED.get()).build(NULL_TYPE));

    public static final DeferredHolder<Block, ThoughtSlurryBlock> THOUGHT_SLURRY_BLOCK = BLOCKS.register("thought_slurry",
            () -> new ThoughtSlurryBlock(
                    OneironautMiscRegistry.THOUGHT_SLURRY,
                    BlockBehaviour.Properties.of()
                            .noOcclusion()
                            .mapColor(MapColor.COLOR_PURPLE)
            )
    );

    // Donor: Blocks.BUDDING_AMETHYST
    public static final DeferredHolder<Block, SuperBuddingBlock> SUPER_BUDDING =
            BLOCKS.register("super_budding", () -> new SuperBuddingBlock(Blocks.BUDDING_AMETHYST.properties()));

    // Donor: HexBlocks.SLATE_BLOCK
    public static final DeferredHolder<Block, SentinelTrapImpetus> SENTINEL_TRAP =
            BLOCKS.register("sentinel_trap", () -> new SentinelTrapImpetus(HexBlocks.SLATE_BLOCK.properties().destroyTime(2f)));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SentinelTrapImpetusEntity>> SENTINEL_TRAP_IMPETUS_ENTITY =
            BLOCK_ENTITIES.register("sentinel_trap_entity", () ->
                    BlockEntityType.Builder.of(SentinelTrapImpetusEntity::new, SENTINEL_TRAP.get()).build(NULL_TYPE));

    // Donor: HexBlocks.SLATE_BLOCK
    public static final DeferredHolder<Block, SentinelSensor> SENTINEL_SENSOR =
            BLOCKS.register("sentinel_sensor", () -> new SentinelSensor(HexBlocks.SLATE_BLOCK.properties()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SentinelSensorEntity>> SENTINEL_SENSOR_ENTITY =
            BLOCK_ENTITIES.register("sentinel_sensor_entity", () ->
                    BlockEntityType.Builder.of(SentinelSensorEntity::new, SENTINEL_SENSOR.get()).build(NULL_TYPE));

    // Donor: Blocks.DEEPSLATE
    public static final DeferredHolder<Block, Block> RAYCAST_BLOCKER =
            BLOCKS.register("raycast_blocker", () -> new Block(Blocks.DEEPSLATE.properties()));

    // Donor: Blocks.TINTED_GLASS
    public static final DeferredHolder<Block, Block> RAYCAST_BLOCKER_GLASS =
            BLOCKS.register("raycast_blocker_glass", () -> new RaycastBlockerGlass(Blocks.TINTED_GLASS.properties()));

    // Donor: Blocks.OBSIDIAN
    public static final DeferredHolder<Block, Block> HEX_RESISTANT_BLOCK =
            BLOCKS.register("hex_resistant_block", () -> new Block(Blocks.OBSIDIAN.properties().destroyTime(1.5f)));

    // Donor: Blocks.BLACK_CONCRETE
    public static final DeferredHolder<Block, Block> CIRCLE =
            BLOCKS.register("circle", () -> new CircleBlock(Blocks.BLACK_CONCRETE.properties().noOcclusion().instabreak()));

    // Donor: Blocks.PACKED_ICE
    public static final DeferredHolder<Block, Block> MEDIA_ICE =
            BLOCKS.register("media_ice", () -> new Block(Blocks.PACKED_ICE.properties().friction(1.1f).mapColor(MapColor.COLOR_PURPLE)));

    // Donor: Blocks.PACKED_ICE (produced by frost walker on thought slurry)
    public static final DeferredHolder<Block, Block> MEDIA_ICE_FROSTED =
            BLOCKS.register("media_ice_frosted", () -> new FrostedMediaIceBlock(Blocks.PACKED_ICE.properties()
                    .friction(1.08f).mapColor(MapColor.COLOR_PURPLE).randomTicks().strength(0.5f).sound(SoundType.GLASS)));

    // Donor: Blocks.SLIME_BLOCK
    public static final DeferredHolder<Block, MediaGelBlock> MEDIA_GEL =
            BLOCKS.register("media_gel", () -> new MediaGelBlock(Blocks.SLIME_BLOCK.properties()
                    .speedFactor(0.05f).jumpFactor(0.25f).mapColor(MapColor.COLOR_PURPLE).sound(SoundType.SLIME_BLOCK).noOcclusion()
                    .destroyTime(Blocks.SOUL_SAND.defaultDestroyTime())));

    // Donor: Blocks.SLIME_BLOCK (will eventually relate to cellular automata / media gel)
    public static final DeferredHolder<Block, CellBlock> CELL =
            BLOCKS.register("cell", () -> new CellBlock(Blocks.SLIME_BLOCK.properties()
                    .speedFactor(0.6f).jumpFactor(0.75f).mapColor(MapColor.COLOR_PURPLE).sound(SoundType.SLIME_BLOCK).noOcclusion()
                    .destroyTime(Blocks.SOUL_SAND.defaultDestroyTime())));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CellEntity>> CELL_ENTITY =
            BLOCK_ENTITIES.register("cell_entity", () ->
                    BlockEntityType.Builder.of(CellEntity::new, CELL.get()).build(NULL_TYPE));

    // Donor: HexBlocks.SLATE_BLOCK
    public static final DeferredHolder<Block, WispBattery> WISP_BATTERY =
            BLOCKS.register("wisp_battery", () ->
                    new WispBattery(HexBlocks.SLATE_BLOCK.properties()
                            .lightLevel(createLightLevelFromBoolBlockState(WispBattery.REDSTONE_POWERED, 15))));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WispBatteryEntity>> WISP_BATTERY_ENTITY =
            BLOCK_ENTITIES.register("wisp_battery_entity", () ->
                    BlockEntityType.Builder.of(WispBatteryEntity::new, WISP_BATTERY.get()).build(NULL_TYPE));

    // Donor: HexBlocks.SLATE_BLOCK
    public static final DeferredHolder<Block, WispBatteryFake> WISP_BATTERY_DECORATIVE =
            BLOCKS.register("decorative_wisp_battery", () ->
                    new WispBatteryFake(HexBlocks.SLATE_BLOCK.properties()
                            .lightLevel(createLightLevelFromBoolBlockState(WispBatteryFake.REDSTONE_POWERED, 15))));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WispBatteryEntityFake>> WISP_BATTERY_ENTITY_DECORATIVE =
            BLOCK_ENTITIES.register("decorative_wisp_battery_entity", () ->
                    BlockEntityType.Builder.of(WispBatteryEntityFake::new, WISP_BATTERY_DECORATIVE.get()).build(NULL_TYPE));

    // Donor: Blocks.AIR
    public static final DeferredHolder<Block, EdifiedTreeSpawnerBlock> EDIFIED_TREE_SPAWNER =
            BLOCKS.register("edified_tree_spawner", () -> new EdifiedTreeSpawnerBlock(Blocks.AIR.properties()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EdifiedTreeSpawnerBlockEntity>> EDIFIED_TREE_SPAWNER_ENTITY =
            BLOCK_ENTITIES.register("edified_tree_spawner_entity", () ->
                    BlockEntityType.Builder.of(EdifiedTreeSpawnerBlockEntity::new, EDIFIED_TREE_SPAWNER.get()).build(NULL_TYPE));

    // Donor: HexBlocks.SLATE_BLOCK
    public static final DeferredHolder<Block, HoverElevatorBlock> HOVER_ELEVATOR =
            BLOCKS.register("hover_elevator", () ->
                    new HoverElevatorBlock(HexBlocks.SLATE_BLOCK.properties()
                            .lightLevel(createLightLevelFromBoolBlockState(HoverElevatorBlock.POWERED, 15))));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HoverElevatorBlockEntity>> HOVER_ELEVATOR_ENTITY =
            BLOCK_ENTITIES.register("hover_elevator_entity", () ->
                    BlockEntityType.Builder.of(HoverElevatorBlockEntity::new, HOVER_ELEVATOR.get()).build(NULL_TYPE));

    // Donor: none in fabric (bare Settings.create())
    public static final DeferredHolder<Block, Block> HOVER_REPEATER =
            BLOCKS.register("hover_repeater", () ->
                    new HoverRepeaterBlock(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.DEEPSLATE)
                            .instrument(NoteBlockInstrument.BASEDRUM)
                            .strength(3.0F, 6.0F)
                            .sound(SoundType.DEEPSLATE)
                            .instabreak()
                            .noOcclusion()
                            .noCollission()));

    // Donor: Blocks.AMETHYST_CLUSTER / LARGE_AMETHYST_BUD / MEDIUM_AMETHYST_BUD / SMALL_AMETHYST_BUD
    public static final DeferredHolder<Block, AmethystClusterBlock> PSEUDOAMETHYST_CLUSTER =
            BLOCKS.register("pseudoamethyst_cluster", () -> new AmethystClusterBlock(7, 3, Blocks.AMETHYST_CLUSTER.properties()));

    public static final DeferredHolder<Block, AmethystClusterBlock> PSEUDOAMETHYST_BUD_LARGE =
            BLOCKS.register("pseudoamethyst_bud_large", () -> new AmethystClusterBlock(5, 3, Blocks.LARGE_AMETHYST_BUD.properties()));

    public static final DeferredHolder<Block, AmethystClusterBlock> PSEUDOAMETHYST_BUD_MEDIUM =
            BLOCKS.register("pseudoamethyst_bud_medium", () -> new AmethystClusterBlock(4, 3, Blocks.MEDIUM_AMETHYST_BUD.properties()));

    public static final DeferredHolder<Block, AmethystClusterBlock> PSEUDOAMETHYST_BUD_SMALL =
            BLOCKS.register("pseudoamethyst_bud_small", () -> new AmethystClusterBlock(3, 4, Blocks.SMALL_AMETHYST_BUD.properties()));

    // Donor: Blocks.RESPAWN_ANCHOR
    public static final DeferredHolder<Block, SpaceBombBlock> SPACE_BOMB =
            BLOCKS.register("spacebomb", () ->
                    new SpaceBombBlock(Blocks.RESPAWN_ANCHOR.properties()
                            .lightLevel(createLightLevelFromBoolBlockState(BlockSlate.ENERGIZED, 13))));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SpaceBombBlockEntity>> SPACE_BOMB_ENTITY =
            BLOCK_ENTITIES.register("spacebomb_entity", () ->
                    BlockEntityType.Builder.of(SpaceBombBlockEntity::new, SPACE_BOMB.get()).build(NULL_TYPE));

    // Donor: HexalBlocks.SLIPWAY (Hexal)
    public static final DeferredHolder<Block, InactiveSlipwayBlock> INACTIVE_SLIPWAY =
            BLOCKS.register("inactiveslipway", () -> new InactiveSlipwayBlock(ram.talia.hexal.common.lib.HexalBlocks.SLIPWAY.properties()));

    // Donor: HexBlocks.SLATE_BLOCK
    public static final DeferredHolder<Block, SlipwaySuppressorBlock> SLIPWAY_SUPPRESSOR =
            BLOCKS.register("slipwaysuppressor", () -> new SlipwaySuppressorBlock(HexBlocks.SLATE_BLOCK.properties()));

    // Donor: Blocks.SWEET_BERRY_BUSH
    public static final DeferredHolder<Block, RenderBerryBushBlock> RENDER_BUSH =
            BLOCKS.register("monkfruit_bush", () -> new RenderBerryBushBlock(Blocks.SWEET_BERRY_BUSH.properties()));

    // Donor: HexBlocks.SLATE_BLOCK
    public static final DeferredHolder<Block, ExtradimensionalBoundaryLocus> EXTRADIM_LOCUS =
            BLOCKS.register("extradimensional_border", () ->
                    new ExtradimensionalBoundaryLocus(HexBlocks.SLATE_BLOCK.properties()
                            .lightLevel(createLightLevelFromBoolBlockState(BlockCircleComponent.ENERGIZED, 14))));

    // Donor: Blocks.BEDROCK
    public static final DeferredHolder<Block, DeepNoosphereFloorBlock> DEEP_NOOSPHERE_FLOOR =
            BLOCKS.register("deep_border", () ->
                    new DeepNoosphereFloorBlock(Blocks.BEDROCK.properties().noTerrainParticles().mapColor(MapColor.COLOR_PURPLE)));

    // Donor: HexBlocks.SLATE_BLOCK
    public static final Supplier<BlockBehaviour.Properties> CONCEPT_MODIFIER_SETTINGS =
            () -> HexBlocks.SLATE_BLOCK.properties().lightLevel(state -> 15);

    private static final Function<CompoundTag, Double> ATTRIBUTE_CONCEPT_CALCULATOR = (nbt) -> {
        double potency = nbt.getDouble("potency");
        if (potency <= 1) {
            potency = (1 - potency) + 1;
            return -Math.pow(potency * 5.0, 2.0);
        } else {
            return Math.pow(potency * 10.0, 2.0);
        }
    };

    // Donor: Blocks.ZOMBIE_HEAD
    public static final DeferredHolder<Block, TranformingSkullBlock> TRANFORMING_SKULL =
            BLOCKS.register("transformingskull", () -> new TranformingSkullBlock(Blocks.ZOMBIE_HEAD.properties()));

    public static final DeferredHolder<Block, TranformingWallSkullBlock> TRANFORMING_WALL_SKULL =
            BLOCKS.register("transformingskull_wall", () -> new TranformingWallSkullBlock(Blocks.ZOMBIE_HEAD.properties()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TransformingSkullBlockEntity>> TRANFORMING_SKULL_ENTITY =
            BLOCK_ENTITIES.register("transformingskull_entity", () ->
                    BlockEntityType.Builder.of(TransformingSkullBlockEntity::new, TRANFORMING_SKULL.get(), TRANFORMING_WALL_SKULL.get()).build(NULL_TYPE));

    // Donor: none in fabric (bare Settings.create())
    public static final BlockBehaviour.Properties INSTANT_BREAKER_SETTINGS =
            BlockBehaviour.Properties.of()
                    .strength(-1.0F, 3600000.8F)
                    .noOcclusion()
                    .noTerrainParticles()
                    .pushReaction(PushReaction.BLOCK)
                    .noCollission()
                    .randomTicks();

    public static final DeferredHolder<Block, InstantBreakingBlock> INSTANT_BREAKER_RIFTRESIDUE =
            BLOCKS.register("rift_residue", () -> new InstantBreakingBlock(INSTANT_BREAKER_SETTINGS));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<InstantBreakingBlockEntity>> INSTANT_BREAKER_ENTITY =
            BLOCK_ENTITIES.register("instant_breaker_entity", () ->
                    BlockEntityType.Builder.of(InstantBreakingBlockEntity::new, INSTANT_BREAKER_RIFTRESIDUE.get()).build(NULL_TYPE));

    private static ToIntFunction<BlockState> createLightLevelFromBoolBlockState(BooleanProperty property, int litLevel) {
        return state -> state.hasProperty(property) && state.getValue(property) ? litLevel : 0;
    }

    public static final BooleanProperty ETERNAL = BooleanProperty.create("eternal");

    private static <T extends Block> DeferredHolder<Item, Item> registerBlockItem(String name, DeferredHolder<T, T> block) {
        return OneironautItemRegistry.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }
}