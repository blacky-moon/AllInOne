package org.blacky_moon.hexverse.common;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.blacky_moon.hexverse.HexVerseMain;
import org.blacky_moon.hexverse.common.entity.HexSnowballProjectile;
import org.blacky_moon.hexverse.common.item.*;
import org.blacky_moon.hexverse.common.snowball.HexSnowballDispenseBehavior;

import java.util.function.Supplier;

public class CommonInit {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(HexVerseMain.MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, HexVerseMain.MOD_ID);

    public static final Supplier<HexSnowballItem> HEX_SNOWBALL_DUST = ITEMS.registerItem(
            "hex_snowball_dust",
            props -> new HexSnowballItem(
                    props,
                    HexSnowballItem.MAX_MEDIA_DUST
            ),
            new Item.Properties().stacksTo(32)
    );

    public static final Supplier<HexSnowballItem> HEX_SNOWBALL_SHARD = ITEMS.registerItem(
            "hex_snowball_shard",
            props -> new HexSnowballItem(
                    props,
                    HexSnowballItem.MAX_MEDIA_SHARD
            ),
            new Item.Properties().stacksTo(16)
    );

    public static final Supplier<HexSnowballItem> HEX_SNOWBALL_CRYSTAL = ITEMS.registerItem(
            "hex_snowball_crystal",
            props -> new HexSnowballItem(
                    props,
                    HexSnowballItem.MAX_MEDIA_CRYSTAL
            ),
            new Item.Properties().stacksTo(8)
    );

    public static final Supplier<HexSnowballItem> HEX_SNOWBALL_QUENCHED = ITEMS.registerItem(
            "hex_snowball_quenched",
            props -> new HexSnowballItem(
                    props,
                    HexSnowballItem.MAX_MEDIA_QUENCHED
            ),
            new Item.Properties().stacksTo(4)
    );

    public static final Supplier<EntityType<HexSnowballProjectile>> HEX_SNOWBALL_PROJECTILE =
            ENTITIES.register("hex_snowball",
                    () -> EntityType.Builder.<HexSnowballProjectile>of(
                                    HexSnowballProjectile::new,
                                    MobCategory.MISC
                            )
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(64)
                            .updateInterval(2)
                            .build("hex_snowball")
            );

    public static final Supplier<HexArmorItem> HEX_CHESTPLATE = ITEMS.registerItem(
            "hex_chestplate",
            props -> new HexArmorItem(
                    BuiltInRegistries.ARMOR_MATERIAL.wrapAsHolder(HexArmorMaterial.INSTANCE),
                    ArmorItem.Type.CHESTPLATE,
                    props
            ),
            new Item.Properties().stacksTo(1).durability(528)
    );

    public static final Supplier<HexArmorItem> HEX_LEGGINGS = ITEMS.registerItem(
            "hex_leggings",
            props -> new HexArmorItem(
                    BuiltInRegistries.ARMOR_MATERIAL.wrapAsHolder(HexArmorMaterial.INSTANCE),
                    ArmorItem.Type.LEGGINGS,
                    props
            ),
            new Item.Properties().stacksTo(1).durability(495)
    );

    public static final Supplier<HexArmorItem> HEX_BOOTS = ITEMS.registerItem(
            "hex_boots",
            props -> new HexArmorItem(
                    BuiltInRegistries.ARMOR_MATERIAL.wrapAsHolder(HexArmorMaterial.INSTANCE),
                    ArmorItem.Type.BOOTS,
                    props
            ),
            new Item.Properties().stacksTo(1).durability(429)
    );

    public static final Supplier<HexPickaxeItem> HEX_PICKAXE = ITEMS.registerItem(
            "hex_pickaxe",
            props -> new HexPickaxeItem(
                    BaseAmethystItem.AMETHYST_TIER,
                    new Item.Properties()
                            .stacksTo(1)
                            .durability(BaseAmethystItem.AMETHYST_TIER.getUses())
                            .attributes(PickaxeItem.createAttributes(
                                    BaseAmethystItem.AMETHYST_TIER, 1.0F, -2.8F))
            )
    );

    public static final Supplier<HexSwordItem> HEX_SWORD = ITEMS.registerItem(
            "hex_sword",
            props -> new HexSwordItem(
                    BaseAmethystItem.AMETHYST_TIER,
                    new Item.Properties()
                            .stacksTo(1)
                            .durability(BaseAmethystItem.AMETHYST_TIER.getUses())
                            .attributes(SwordItem.createAttributes(
                                    BaseAmethystItem.AMETHYST_TIER, 3.0F, -2.4F))
            )
    );

    public static void init(IEventBus modEventBus, ModContainer container) {
        ITEMS.register(modEventBus);
        ENTITIES.register(modEventBus);
        HexSnowballItem.HEX_SNOWBALL_DUST = HEX_SNOWBALL_DUST;
        HexSnowballItem.HEX_SNOWBALL_SHARD = HEX_SNOWBALL_SHARD;
        HexSnowballItem.HEX_SNOWBALL_CRYSTAL = HEX_SNOWBALL_CRYSTAL;
        HexSnowballItem.HEX_SNOWBALL_QUENCHED = HEX_SNOWBALL_QUENCHED;

        modEventBus.addListener(CommonInit::setup);
    }

    public static void setup(FMLCommonSetupEvent event) {
        event.enqueueWork(CommonInit::registerDispenserBehaviors);
    }

    public static void registerDispenserBehaviors() {
        DispenserBlock.registerBehavior(
                HEX_SNOWBALL_DUST.get(),
                new HexSnowballDispenseBehavior()
        );
        DispenserBlock.registerBehavior(
                HEX_SNOWBALL_SHARD.get(),
                new HexSnowballDispenseBehavior()
        );
        DispenserBlock.registerBehavior(
                HEX_SNOWBALL_CRYSTAL.get(),
                new HexSnowballDispenseBehavior()
        );
        DispenserBlock.registerBehavior(
                HEX_SNOWBALL_QUENCHED.get(),
                new HexSnowballDispenseBehavior()
        );
    }
}