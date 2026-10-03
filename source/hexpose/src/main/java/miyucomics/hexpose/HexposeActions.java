package miyucomics.hexpose;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.api.casting.castables.Action;
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.env.CircleCastEnv;
import at.petrak.hexcasting.api.casting.eval.env.PackagedItemCastEnv;
import at.petrak.hexcasting.api.casting.eval.env.StaffCastEnv;
import at.petrak.hexcasting.api.casting.iota.*;
import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity;
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota;
import at.petrak.hexcasting.api.item.VariantItem;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.common.lib.hex.HexActions;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import miyucomics.hexpose.actions.blockstates.OpGetBlockProperties;
import miyucomics.hexpose.actions.blockstates.OpQueryBlockProperty;
import miyucomics.hexpose.actions.identifier.OpClassify;
import miyucomics.hexpose.actions.identifier.OpIdentify;
import miyucomics.hexpose.actions.instance_data.*;
import miyucomics.hexpose.actions.item_stack.*;
import miyucomics.hexpose.actions.misc.*;
import miyucomics.hexpose.actions.raycast.OpFluidRaycast;
import miyucomics.hexpose.actions.raycast.OpFluidSurfaceRaycast;
import miyucomics.hexpose.actions.raycast.OpPiercingRaycast;
import miyucomics.hexpose.actions.raycast.OpPiercingSurfaceRaycast;
import miyucomics.hexpose.actions.text.*;
import miyucomics.hexpose.actions.types.OpGetBlockTypeData;
import miyucomics.hexpose.actions.types.OpGetFoodTypeData;
import miyucomics.hexpose.actions.types.OpGetItemTypeData;
import miyucomics.hexpose.iotas.IdentifierIota;
import miyucomics.hexpose.iotas.ItemStackIota;
import miyucomics.hexpose.iotas.TextIota;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.LightLayer;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HexposeActions {
    public static void registerAll(RegisterEvent.RegisterHelper<ActionRegistryEntry> helper) {
        register("am_enlightened", "awqaqqq", HexDir.SOUTH_EAST, new OpGetPlayerData(player -> {
            var advancements = Objects.requireNonNull(player.getServer()).getAdvancements();
            var advancement = advancements.get(HexAPI.modLoc("enlightenment"));
            var tracker = player.getAdvancements();
            assert advancement != null;
            // Передаем логическое состояние готовности ачивки напрямую в булеву иоту
            return List.of(new BooleanIota(tracker.getOrStartProgress(advancement).isDone()));
        }));

        register("is_brainswept", "qqqaqqq", HexDir.SOUTH_EAST, new OpGetLivingEntityData(entity -> {
            if (entity instanceof Monster) {
                // Избавились от тернарника, передаем флаг промывки мозгов
                return List.of(new BooleanIota(IXplatAbstractions.INSTANCE.isBrainswept((Monster) entity)));
            }
            // Если это не монстр, он априори не может быть промыт гексом — возвращаем false
            return List.of(new BooleanIota(false));
        }));
        register("create_text", "awaqeeeee", HexDir.SOUTH_WEST, OpCreateText.INSTANCE);
        register("split_text", "awaqeeeeedwe", HexDir.SOUTH_WEST, OpSplitText.INSTANCE);
        register("combine_text", "dwdeqqqqqawq", HexDir.SOUTH_EAST, OpCombineList.INSTANCE);

        register("text_color", "awaqeeeeewded", HexDir.SOUTH_WEST, new OpStyleText(
                (args, style) -> {
                    Iota arg = args.get(1);
                    if (!(arg instanceof Vec3Iota)) throw new RuntimeException("Expected Vec3Iota");
                    Vec3 colorRaw = ((Vec3Iota) arg).getVec3();
                    int color = ((int) (Math.min(Math.max(colorRaw.x, 0.0), 1.0) * 255) << 16) |
                            ((int) (Math.min(Math.max(colorRaw.y, 0.0), 1.0) * 255) << 8) |
                            (int) (Math.min(Math.max(colorRaw.z, 0.0), 1.0) * 255);
                    return style.withColor(TextColor.fromRgb(color));
                },
                (style) -> {
                    var color = style.getColor();
                    if (color == null) return new Vec3Iota(new Vec3(1.0, 1.0, 1.0));
                    int colorInt = color.getValue();
                    double r = ((colorInt >> 16) & 0xFF) / 255.0;
                    double g = ((colorInt >> 8) & 0xFF) / 255.0;
                    double b = (colorInt & 0xFF) / 255.0;
                    return new Vec3Iota(new Vec3(r, g, b));
                }
        ));

        register("text_bold", "awaqeeeeedd", HexDir.SOUTH_WEST, new OpStyleText(
                (args, style) -> {
                    Iota arg = args.get(1);
                    if (!(arg instanceof BooleanIota)) throw new RuntimeException("Expected BooleanIota");
                    return style.withBold(((BooleanIota) arg).getBool());
                },
                (style) -> new BooleanIota(style.isBold())
        ));

        register("text_italics", "awaqeeeeede", HexDir.SOUTH_WEST, new OpStyleText(
                (args, style) -> {
                    Iota arg = args.get(1);
                    if (!(arg instanceof BooleanIota)) throw new RuntimeException("Expected BooleanIota");
                    return style.withItalic(((BooleanIota) arg).getBool());
                },
                (style) -> new BooleanIota(style.isItalic())
        ));

        register("text_underline", "awaqeeeeedw", HexDir.SOUTH_WEST, new OpStyleText(
                (args, style) -> {
                    Iota arg = args.get(1);
                    if (!(arg instanceof BooleanIota)) throw new RuntimeException("Expected BooleanIota");
                    return style.withUnderlined(((BooleanIota) arg).getBool());
                },
                (style) -> new BooleanIota(style.isUnderlined())
        ));

        register("text_strikethrough", "awaqeeeeedq", HexDir.SOUTH_WEST, new OpStyleText(
                (args, style) -> {
                    Iota arg = args.get(1);
                    if (!(arg instanceof BooleanIota)) throw new RuntimeException("Expected BooleanIota");
                    return style.withStrikethrough(((BooleanIota) arg).getBool());
                },
                (style) -> new BooleanIota(style.isStrikethrough())
        ));

        register("text_obfuscated", "awaqeeeeeda", HexDir.SOUTH_WEST, new OpStyleText(
                (args, style) -> {
                    Iota arg = args.get(1);
                    if (!(arg instanceof BooleanIota)) throw new RuntimeException("Expected BooleanIota");
                    return style.withObfuscated(((BooleanIota) arg).getBool());
                },
                (style) -> new BooleanIota(style.isObfuscated())
        ));

        register("fluid_raycast", "wqqaqwede", HexDir.EAST, OpFluidRaycast.INSTANCE);
        register("fluid_surface_raycast", "weedewqaq", HexDir.EAST, OpFluidSurfaceRaycast.INSTANCE);
        register("piercing_raycast", "wqqddqeqddq", HexDir.EAST, OpPiercingRaycast.INSTANCE);
        register("piercing_surface_raycast", "weeaaeqeaae", HexDir.EAST, OpPiercingSurfaceRaycast.INSTANCE);

        register("block_hardness", "qaqqqqqeeeeedq", HexDir.EAST, new OpGetBlockTypeData(block -> List.of(new DoubleIota(block.defaultDestroyTime()))));
        register("block_blast_resistance", "qaqqqqqewaawaawa", HexDir.EAST, new OpGetBlockTypeData(block -> List.of(new DoubleIota(block.getExplosionResistance()))));
        register("blockstate_rotation", "qaqqqqqwadeeed", HexDir.EAST, new OpGetBlockStateData(state -> {
            Property<Direction> facingProperty = null;

            if (state.hasProperty(BlockStateProperties.FACING)) {
                facingProperty = BlockStateProperties.FACING;
            } else if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
                facingProperty = BlockStateProperties.HORIZONTAL_FACING;
            }

            if (facingProperty != null) {
                Direction direction = state.getValue(facingProperty);

                Vec3 directionVec = Vec3.atLowerCornerOf(direction.getNormal());

                return List.of(new Vec3Iota(directionVec));
            }

            return List.of(new NullIota());
        }));

        register("blockstate_crop", "qaqqqqqwaea", HexDir.EAST, new OpGetBlockStateData(state -> {

            IntegerProperty ageProperty = null;
            for (Property<?> property : state.getProperties()) {
                if (property instanceof IntegerProperty intProp && property.getName().equals("age")) {
                    ageProperty = intProp;
                    break;
                }
            }
            if (ageProperty != null) {
                int currentAge = state.getValue(ageProperty);
                return List.of(new DoubleIota((double) currentAge));
            }
            return List.of(new NullIota());
        }));

        register("get_blockstates", "qaqqqeqqqwqaww", HexDir.EAST, OpGetBlockProperties.INSTANCE);
        register("query_blockstate", "qaqqqqqeawa", HexDir.EAST, OpQueryBlockProperty.INSTANCE);

        register("block_map_color", "qwedewqqqqq", HexDir.EAST, new OpGetBlockTypeData(block -> {
            int color = block.defaultMapColor().col;
            return List.of(new Vec3Iota(new Vec3(
                    ((color >> 16) & 0xFF) / 255.0,
                    ((color >> 8) & 0xFF) / 255.0,
                    (color & 0xFF) / 255.0
            )));
        }));

        register("get_chat", "dqqqaw", HexDir.SOUTH_EAST, OpGetChat.INSTANCE);
        register("get_message", "aeeedw", HexDir.SOUTH_WEST, OpGetMessage.INSTANCE);

        register("get_enchantments", "waqwwqawqwawaw", HexDir.WEST, new OpGetItemStackData((stack, env) -> {
            List<Iota> result = new ArrayList<>();

            // Безопасно получаем реестр без orElseThrow(), чтобы не крашить сервер, если его нет в контексте
            var registryOptional = env.getWorld().registryAccess().registry(Registries.ENCHANTMENT);
            if (registryOptional.isEmpty()) {
                return result; // Если реестр недоступен во время сохранения каста, просто возвращаем пустой список
            }
            var registry = registryOptional.get();

            var enchantments = stack.get(DataComponents.ENCHANTMENTS);
            if (enchantments != null) {
                for (var entry : enchantments.entrySet()) {
                    ResourceLocation id = registry.getKey(entry.getKey().value());
                    if (id != null) {
                        result.add(new IdentifierIota(id));
                    }
                }
            }
            if (stack.is(Items.ENCHANTED_BOOK)) {
                var stored = stack.get(DataComponents.STORED_ENCHANTMENTS);
                if (stored != null) {
                    for (var entry : stored.entrySet()) {
                        ResourceLocation id = registry.getKey(entry.getKey().value());
                        if (id != null) {
                            result.add(new IdentifierIota(id));
                        }
                    }
                }
            }
            return result;
        }));

        register("get_enchantment_strength", "waqwwqaweede", HexDir.WEST, OpGetEnchantmentStrength.INSTANCE);

        register("entity_width", "dwe", HexDir.NORTH_WEST, new OpGetEntityData(entity -> List.of(new DoubleIota(entity.getBbWidth()))));

        register("theodolite", "wqaa", HexDir.EAST, new OpGetEntityData(entity -> {
            float upPitch = (-entity.getXRot() + 90) * (float) Math.PI / 180;
            float yaw = -entity.getYHeadRot() * (float) Math.PI / 180;
            double h = Math.cos(yaw);
            double j = Math.cos(upPitch);
            return List.of(new Vec3Iota(new Vec3(
                    Math.sin(yaw) * j,
                    Math.sin(upPitch),
                    h * j
            )));
        }));

        register("get_living_tick", "wdwwqaqwwaqqwqq", HexDir.WEST, new OpGetEntityData(entity -> List.of(new DoubleIota(entity.tickCount))));
        register("get_flight_origin", "awawaawqdwaqawddw", HexDir.SOUTH_WEST, OpGetFlightOrigin.INSTANCE);

        register("get_health", "wddwaqqwawq", HexDir.SOUTH_EAST, new OpGetLivingEntityData(entity -> List.of(new DoubleIota(entity.getHealth()))));
        register("get_max_health", "wddwwawaeqwawq", HexDir.SOUTH_EAST, new OpGetLivingEntityData(entity -> List.of(new DoubleIota(entity.getMaxHealth()))));
        register("burning", "eewdead", HexDir.WEST, new OpGetEntityData(entity -> List.of(new DoubleIota(entity.getRemainingFireTicks() / 20.0))));

        // Теперь возвращает чистый булев тип (True/False)
        register("is_wet", "qqqqwaadq", HexDir.SOUTH_WEST, new OpGetEntityData(entity -> List.of(new BooleanIota(entity.isInWater()))));

        register("get_air", "wwaade", HexDir.EAST, new OpGetLivingEntityData(entity -> List.of(new DoubleIota(entity.getAirSupply() / 20.0))));
        register("get_max_air", "wwaadee", HexDir.EAST, new OpGetLivingEntityData(entity -> List.of(new DoubleIota(entity.getMaxAirSupply() / 20.0))));

        // Теперь возвращают чистый булев тип (True/False)
        register("is_sleeping", "aqaew", HexDir.NORTH_WEST, new OpGetLivingEntityData(entity -> List.of(new BooleanIota(entity.isSleeping()))));
        register("is_sprinting", "eaq", HexDir.WEST, new OpGetLivingEntityData(entity -> List.of(new BooleanIota(entity.isSprinting()))));
        register("is_baby", "awaqdwaaw", HexDir.SOUTH_WEST, new OpGetLivingEntityData(entity -> List.of(new BooleanIota(entity.isBaby()))));

        register("breedable", "awaaqdqaawa", HexDir.EAST, new OpGetLivingEntityData(entity -> {
            if (entity instanceof Animal) {
                return List.of(new BooleanIota(((Animal) entity).isInLove()));
            }
            return List.of(new NullIota());
        }));

        register("get_player_hunger", "qqqadaddw", HexDir.WEST, new OpGetPlayerData(player -> List.of(new DoubleIota(player.getFoodData().getFoodLevel()))));
        register("get_player_saturation", "qqqadaddq", HexDir.WEST, new OpGetPlayerData(player -> List.of(new DoubleIota(player.getFoodData().getSaturationLevel()))));

        register("entity_vehicle", "eqqedwewew", HexDir.EAST, new OpGetEntityData(entity -> {
            var vehicle = entity.getVehicle();
            if (vehicle != null) {
                return List.of(new EntityIota(vehicle));
            }
            return List.of(new NullIota());
        }));

        register("entity_passengers", "qeeqawqwqw", HexDir.EAST, new OpGetEntityData(entity -> {
            List<Iota> passengers = new ArrayList<>();
            for (Entity passenger : entity.getPassengers()) {
                passengers.add(new EntityIota(passenger));
            }
            return passengers;
        }));

        register("shooter", "aadedade", HexDir.EAST, OpShooter.INSTANCE);
        register("pet_owner", "qdaqwawqeewde", HexDir.WEST, OpPetOwner.INSTANCE);

        register("entity_name", "edeweedw", HexDir.SOUTH_WEST, new OpGetEntityData(entity -> {
            return List.of(new TextIota(entity.getDisplayName()));
        }));

        register("absorption_hearts", "waawedwdwd", HexDir.NORTH_EAST, new OpGetLivingEntityData(entity -> List.of(new DoubleIota(entity.getAbsorptionAmount()))));

        register("env_ambit", "wawaw", HexDir.EAST, OpGetAmbit.INSTANCE);
        register("env_staff", "waaq", HexDir.NORTH_EAST, new OpGetEnvData(env -> List.of(new BooleanIota(env instanceof StaffCastEnv))));
        register("env_offhand", "qaqqqwaaq", HexDir.NORTH_EAST, new OpGetEnvData(env -> List.of(new BooleanIota(env.getCastingHand() == InteractionHand.MAIN_HAND))));
        register("env_packaged_hex", "waaqwwaqqqqq", HexDir.NORTH_EAST, new OpGetEnvData(env -> List.of(new BooleanIota(env instanceof PackagedItemCastEnv))));
        register("env_circle", "waaqdeaqwqae", HexDir.NORTH_EAST, new OpGetEnvData(env -> List.of(new BooleanIota(env instanceof CircleCastEnv ))));

        register("edible", "adaqqqdd", HexDir.WEST, new OpGetItemTypeData(item -> {
            // создаём пустой стек
            ItemStack stack = new ItemStack(item);
            return List.of(new BooleanIota(item.getFoodProperties(stack, null) != null));
        }));
        register("get_hunger", "adaqqqddqe", HexDir.WEST, new OpGetFoodTypeData(food -> List.of(new DoubleIota(food.nutrition()))));
        register("get_saturation", "adaqqqddqw", HexDir.WEST, new OpGetFoodTypeData(food -> List.of(new DoubleIota(food.saturation()))));
        register("is_meat", "adaqqqddaed", HexDir.WEST, new OpGetFoodTypeData(food -> List.of(new DoubleIota(0.0)))); // FoodProperties не имеет isMeat, можно закомментировать или убрать
        register("is_snack", "adaqqqddaq", HexDir.WEST, new OpGetFoodTypeData(food -> List.of(new DoubleIota(0.0))));
        register("identify", "qqqqqe", HexDir.NORTH_EAST, OpIdentify.INSTANCE);
        register("classify", "edqdeq", HexDir.WEST, OpClassify.INSTANCE);

        register("get_stack", "edeedq", HexDir.WEST, OpItemIota.INSTANCE);
        register("create_stack", "qaqqae", HexDir.EAST, OpCreateStack.INSTANCE);
        register("get_mainhand", "qaqqqq", HexDir.NORTH_EAST, new OpGetHeldStack(InteractionHand.MAIN_HAND));
        register("get_offhand", "edeeee", HexDir.NORTH_WEST, new OpGetHeldStack(InteractionHand.OFF_HAND));
        register("get_armor", "qaqddqeeeeqd", HexDir.NORTH_EAST, OpGetArmor.INSTANCE);
        register("get_ender_chest", "qaqdqaqdeeewedw", HexDir.NORTH_EAST, OpGetEnderInventory.INSTANCE);
        register("get_inventory", "edeeeeeqdee", HexDir.WEST, OpGetInventory.INSTANCE);
        register("get_block_inventory", "qaqqqqqeaqq", HexDir.EAST, OpGetContainer.INSTANCE);
        register("count_stack", "qaqqwqqqw", HexDir.EAST, new OpGetItemStackData((stack, env) -> List.of(new DoubleIota(stack.getCount()))));
        register("damage_stack", "eeweeewdeq", HexDir.NORTH_EAST, new OpGetItemStackData((stack, env) -> List.of(new DoubleIota(stack.getDamageValue()))));


        register("count_max_stack", "edeeweeew", HexDir.WEST, new OpGetItemTypeData(item -> {
            ItemStack stack = new ItemStack(item);
            return List.of(new DoubleIota(item.getMaxStackSize(stack)));
        }));
        register("damage_max_stack", "qqwqqqwaqe", HexDir.NORTH_WEST, new OpGetItemTypeData(item -> {
            ItemStack stack = new ItemStack(item);
            return List.of(new DoubleIota(item.getMaxDamage(stack)));
        }));
        register("item_variant", "dwaawaqwa", HexDir.WEST, new OpGetItemStackData((stack, env) -> {
            if (stack.getItem() instanceof VariantItem) {
                int variant = ((VariantItem) stack.getItem()).getVariant(stack);
                return List.of(new DoubleIota(variant));
            }
            return List.of(new NullIota());
        }));

        register("item_variant_max", "dwaawaqwawq", HexDir.WEST, new OpGetItemStackData((stack, env) -> {
            if (stack.getItem() instanceof VariantItem) {
                int max = ((VariantItem) stack.getItem()).numVariants();
                return List.of(new DoubleIota(max));
            }
            return List.of(new NullIota());
        }));

        register("item_name", "qwawqwaqea", HexDir.SOUTH_EAST, new OpGetItemStackData((stack, env) -> List.of(new TextIota(stack.getHoverName()))));


        register("item_lore", "dwewdwedea", HexDir.NORTH_WEST, new OpGetItemStackData((stack, env) -> {
            var lore = stack.get(DataComponents.LORE);
            if (lore != null) {
                List<Iota> result = new ArrayList<>();
                for (var line : lore.lines()) {
                    result.add(new TextIota(line));
                }
                return result;
            }
            return new ArrayList<>();
        }));

        register("read_book", "awqqwaqd", HexDir.WEST, OpReadBook.INSTANCE);
        register("book_sources", "eaedweew", HexDir.EAST, OpBookSources.INSTANCE);
        register("item_rarity", "wqqed", HexDir.NORTH_EAST, new OpGetItemStackData((stack, env) -> List.of(new DoubleIota(stack.getRarity().ordinal()))));

        register("get_effects_entity", "wqqq", HexDir.SOUTH_WEST, new OpGetLivingEntityData(entity -> {
            List<Iota> effectsList = new ArrayList<>();
            for (var effect : entity.getActiveEffects()) {
                ResourceLocation id = BuiltInRegistries.MOB_EFFECT.getKey(effect.getEffect().value());
                if (id != null) {
                    effectsList.add(new IdentifierIota(id));
                }
            }
            return List.of(new ListIota(effectsList));
        }));

        register("get_effects_item", "wqqqadee", HexDir.SOUTH_WEST, OpGetPrescription.INSTANCE);
        register("get_effect_category", "wqqqaawd", HexDir.SOUTH_WEST, OpGetStatusEffectCategory.INSTANCE);
        register("get_effect_amplifier", "wqqqaqwa", HexDir.SOUTH_WEST, new OpGetStatusEffectInstanceData(instance -> List.of(new DoubleIota(instance.getAmplifier()))));
        register("get_effect_duration", "wqqqaqwdd", HexDir.SOUTH_WEST, new OpGetStatusEffectInstanceData(instance -> List.of(new DoubleIota(instance.getDuration()))));

        register("villager_level", "qeqwqwqwqwqeqawdaeaeaeaeaea", HexDir.EAST, new OpGetVillagerData(villager -> List.of(new DoubleIota(villager.getVillagerData().getLevel()))));

        register("villager_profession", "qeqwqwqwqwqeqawewawqwawadeeeee", HexDir.EAST, new OpGetVillagerData(villager -> {
            var id = BuiltInRegistries.VILLAGER_PROFESSION.getKey(villager.getVillagerData().getProfession());
            return List.of(new IdentifierIota(id));
        }));

        register("villager_type", "qeqwqwqwqwqeqaweqqqqqwded", HexDir.EAST, new OpGetVillagerData(villager -> {
            var id = BuiltInRegistries.VILLAGER_TYPE.getKey(villager.getVillagerData().getType());
            return List.of(new IdentifierIota(id));
        }));

        register("biome_to_villager", "qeqwqwqwqwqeqawewwqqwwqwwqqww", HexDir.EAST, OpVillagerTypeFromBiome.INSTANCE);

        register("get_media", "ddew", HexDir.WEST, OpGetMedia.INSTANCE);

        register("env_media", "dde", HexDir.WEST, new OpGetEnvData(env -> {
            long max = Long.MAX_VALUE - env.extractMedia(Long.MAX_VALUE, true);
            return List.of(new DoubleIota(max / (double) MediaConstants.DUST_UNIT));
        }));

        register("media_max_stack", "ddeaq", HexDir.EAST, new OpGetItemStackData((stack, env) -> {
            var holder = IXplatAbstractions.INSTANCE.findMediaHolder(stack);
            if (holder == null) {
                return List.of(new NullIota());
            }
            return List.of(new DoubleIota(holder.getMaxMedia() / (double) MediaConstants.DUST_UNIT));
        }));

        register("get_weather", "eweweweweweeeaedqdqde", HexDir.WEST, new OpGetWorldData(world -> {
            double value = world.isThundering() ? 2.0 : world.isRaining() ? 1.0 : 0.0;
            return List.of(new DoubleIota(value));
        }));



        register("get_day", "wwawwawwqqawwdwwdwwaqwqwqwqwq", HexDir.SOUTH_EAST, new OpGetWorldData(world -> List.of(new DoubleIota(world.getDayTime() / 24000.0))));

        register("get_time", "wddwaqqwqaddaqqwddwaqqwqaddaq", HexDir.SOUTH_EAST, new OpGetWorldData(world -> List.of(new DoubleIota(world.getGameTime()))));

        register("get_moon", "eweweweweweeweeedadw", HexDir.WEST, new OpGetWorldData(world -> List.of(new DoubleIota(world.getMoonPhase()))));

        register("get_light", "wqwqwqwqwqwaeqqqqaeqaeaeaeaw", HexDir.SOUTH_WEST, new OpGetPositionData((world, pos) -> List.of(new DoubleIota(world.getMaxLocalRawBrightness(pos)))));

        register("get_power", "qwqwqwqwqwqqwwaadwdaaww", HexDir.EAST, new OpGetPositionData((world, pos) -> List.of(new DoubleIota(world.getDirectSignal(pos, Direction.DOWN)))));

        register("get_comparator", "eweweweweweewwddawaddww", HexDir.WEST, new OpGetPositionData((world, pos) -> {
            var state = world.getBlockState(pos);
            if (state.hasAnalogOutputSignal()) {
                return List.of(new DoubleIota(state.getAnalogOutputSignal(world, pos)));
            }
            return List.of(new NullIota());
        }));

        register("get_biome", "qwqwqawdqqaqqdwaqwqwq", HexDir.WEST, new OpGetPositionData((world, pos) -> {
            var biome = world.getBiome(pos);
            var key = biome.unwrapKey();
            if (key.isPresent()) {
                return List.of(new IdentifierIota(key.get().location()));
            }
            return List.of(new NullIota());
        }));

        register("get_dimension", "qwqwqwqwqwqqaedwaqd", HexDir.WEST, new OpGetWorldData(world -> List.of(new IdentifierIota(world.dimension().location()))));

        register("get_einstein", "aqwawqwqqwqwqwqwqwq", HexDir.SOUTH_WEST, new OpGetWorldData(world -> {

            boolean hasNaturalClock = world.dimensionType().natural();
            return List.of(new BooleanIota(hasNaturalClock ? true : false));
        }));

        register("cat_variant", "wqwqqwqwawaaw", HexDir.SOUTH_WEST, new ConstMediaAction() {
            @Override
            public int getArgc() { return 1; }

            @Override
            public List<Iota> execute(List<? extends Iota> args, CastingEnvironment env) {
                // Получаем сущность
                Iota arg = args.get(0);
                if (!(arg instanceof EntityIota)) {
                    throw MishapInvalidIota.ofType(arg, 0, "entity");

                }
                Entity entity = ((EntityIota) arg).getEntity(env.getWorld());
                if (entity == null) {
                    throw MishapInvalidIota.ofType(arg, 0, "entity");

                }
                env.assertEntityInRange(entity);
                if (!(entity instanceof Cat)) {
                    throw MishapBadEntity.of(entity, "cat");
                }
                var variant = ((Cat) entity).getVariant();
                var id = BuiltInRegistries.CAT_VARIANT.getKey(variant.value());
                return List.of(new IdentifierIota(id));
            }
        });

        register("creeper_fuse", "dedwaqwede", HexDir.WEST, new ConstMediaAction() {
            @Override
            public int getArgc() { return 1; }

            @Override
            public List<Iota> execute(List<? extends Iota> args, CastingEnvironment env) {
                Iota arg = args.get(0);
                if (!(arg instanceof EntityIota)) {
                    throw MishapInvalidIota.ofType(arg, 0, "entity");

                }
                Entity entity = ((EntityIota) arg).getEntity(env.getWorld());
                if (entity == null) {
                    throw MishapInvalidIota.ofType(arg, 0, "entity");

                }
                env.assertEntityInRange(entity);
                if (!(entity instanceof Creeper)) {
                    throw MishapBadEntity.of(entity, "creeper");
                }
                return List.of(new DoubleIota(((Creeper) entity).getSwelling(0)));
            }
        });

        register("item_frame_rotation", "ewdwewdea", HexDir.NORTH_EAST, new ConstMediaAction() {
            @Override
            public int getArgc() { return 1; }

            @Override
            public List<Iota> execute(List<? extends Iota> args, CastingEnvironment env) {
                Iota arg = args.get(0);
                if (!(arg instanceof EntityIota)) {
                    throw MishapInvalidIota.ofType(arg, 0, "entity");
                }
                Entity entity = ((EntityIota) arg).getEntity(env.getWorld());
                if (entity == null) {
                    throw MishapInvalidIota.ofType(arg, 0, "entity");
                }
                env.assertEntityInRange(entity);
                if (!(entity instanceof ItemFrame)) {
                    throw MishapBadEntity.of(entity, "item_frame");
                }
                return List.of(new DoubleIota(((ItemFrame) entity).getRotation()));
            }
        });

        register("painting_variant", "wawwwqwwawwwqadaqeda", HexDir.SOUTH_WEST, new ConstMediaAction() {
            @Override
            public int getArgc() { return 1; }

            @Override
            public List<Iota> execute(List<? extends Iota> args, CastingEnvironment env) {
                Iota arg = args.get(0);
                if (!(arg instanceof EntityIota)) {
                    throw MishapInvalidIota.ofType(arg, 0, "entity");
                }
                Entity entity = ((EntityIota) arg).getEntity(env.getWorld());
                if (entity == null) {
                    throw MishapInvalidIota.ofType(arg, 0, "entity");
                }
                env.assertEntityInRange(entity);
                if (!(entity instanceof Painting)) {
                    throw MishapBadEntity.of(entity, "painting");
                }
                var variant = ((Painting) entity).getVariant();
                var registry = env.getWorld().registryAccess().registry(Registries.PAINTING_VARIANT).orElseThrow();
                var id = registry.getKey(variant.value());
                return List.of(new IdentifierIota(id));
            }
        });
    }

    private static void register(String name, String signature, HexDir startDir, Action action) {
        Registry.register(
                HexActions.REGISTRY,
                ResourceLocation.fromNamespaceAndPath(HexposeMain.MOD_ID, name),
                new ActionRegistryEntry(HexPattern.fromAngles(signature, startDir), action)
        );
    }
}