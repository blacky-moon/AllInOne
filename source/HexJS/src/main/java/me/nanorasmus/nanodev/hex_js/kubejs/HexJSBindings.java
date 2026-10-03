package me.nanorasmus.nanodev.hex_js.kubejs;

import at.petrak.hexcasting.api.addldata.ADIotaHolder;
import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.common.lib.HexDataComponents;
import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.common.lib.HexRegistries;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import com.mojang.datafixers.util.Pair;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.Param;
import dev.latvian.mods.rhino.util.HideFromJS;
import me.nanorasmus.nanodev.hex_js.HexJS;
import me.nanorasmus.nanodev.hex_js.helpers.IotaHelper;
import me.nanorasmus.nanodev.hex_js.kubejs.customIotas.CustomIota;
import me.nanorasmus.nanodev.hex_js.kubejs.customIotas.CustomIotaEntry;
import me.nanorasmus.nanodev.hex_js.kubejs.customIotas.CustomIotaRegistry;
import me.nanorasmus.nanodev.hex_js.kubejs.customPatterns.CustomPatternHolder;
import me.nanorasmus.nanodev.hex_js.kubejs.customPatterns.CustomPatternRegistry;
import me.nanorasmus.nanodev.hex_js.storage.StorageManager;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class HexJSBindings {

    // Get info from Hex

    @Info(
            value = "Gets the angle signature of a great spell, or an empty string if not found",
            params = {
                    @Param(name = "greatSpellID", value = "The resource ID of the great spell")
            }
    )
    public String getGreatSpellAngles(String greatSpellID) {
        // TODO: в 0.11 PatternRegistry удалён. Great spell'ы с per-world сигнатурой
        // хранятся в PatternRegistryManifest / через ActionRegistryEntry.
        // Сверь точный метод в исходниках hexcasting-common 0.11.x.
        Map<String, Pair<ResourceLocation, HexDir>> perWorldPatterns = getPerWorldPatterns();

        for (String angles : perWorldPatterns.keySet()) {
            if (greatSpellID.equals(perWorldPatterns.get(angles).getFirst().toString())) return angles;
        }

        return "";
    }

    @HideFromJS
    private Map<String, Pair<ResourceLocation, HexDir>> getPerWorldPatterns() {
        // TODO: реализовать через PatternRegistryManifest.getCanonicalStrokesPerWorld(...)
        return new HashMap<>();
    }

    // Custom Iotas
    @Info(
            value = "Registers a custom iota type",
            params = {
                    @Param(name = "identifier", value = "The ID of the iota type"),
                    @Param(name = "init", value = ""),
                    @Param(name = "isTruthy", value = ""),
                    @Param(name = "toleratesOther", value = ""),
                    @Param(name = "serialize", value = ""),
                    @Param(name = "deserialize", value = "")
            }
    )
    public void registerCustomIota(
            @NotNull String identifier,
            @NotNull Consumer<CustomIota> init,
            @Nullable Function<CustomIota, Boolean> isTruthy,
            @Nullable BiFunction<CustomIota, Iota, Boolean> toleratesOther,
            @Nullable Function<CustomIota, Tag> serialize,
            @Nullable BiFunction<CustomIota, ServerLevel, CustomIota> deserialize
    ) {

    }

    @Info(
            value = "Registers a custom pattern",
            params = {
                    @Param(name = "name", value = "The name of the pattern"),
                    @Param(name = "angles", value = "The angle signature of the pattern (String)"),
                    @Param(name = "isGreatSpell", value = "Is this a great spell? (Boolean)"),
                    @Param(name = "causesBlindDiversion", value = "Does this pattern cause blind diversion? (Boolean)")
            }
    )
    public void registerCustomPattern(String name, String angles, boolean isGreatSpell, boolean causesBlindDiversion) {
        String key = angles.toLowerCase();

        CustomPatternRegistry.registry.put(
                key,
                new CustomPatternHolder(
                        name,
                        IotaHelper.patternFromString(key),
                        isGreatSpell,
                        causesBlindDiversion
                )
        );

        HexJS.LOGGER.info("HexJS registered custom pattern: {}", key);
    }

    @Info(
            value = "Unregisters a custom pattern",
            params = {
                    @Param(name = "angles", value = "The angle signature of the pattern to unregister (String)")
            }
    )

    public void unregisterCustomPattern(String angles) {
        CustomPatternRegistry.registry.remove(angles.toLowerCase());
    }

    @Info("Unregisters all custom patterns")
    public void clearCustomPatterns() {
        CustomPatternRegistry.registry.clear();
    }

    // -- Player Force Casting --

    @Info(
            value = "Forces a player to cast a series of patterns, this will act as if they cast the patterns themselves",
            params = {
                    @Param(name = "player", value = "The player in question (Entity)"),
                    @Param(name = "patterns", value = "The patterns to cast (List of Strings)")
            }
    )
    public void forceCastPlayerEntity(ServerPlayer player, List<String> patterns) {
        // Give the player an override to the staff requirement
        StorageManager.currentlyForcedPlayers.add(player.getUUID());

        try {
            // TODO: в 0.11 CastingHarness заменён на CastingVM, а ControllerInfo на ExecutionClientView.
            // Получить VM игрока: IXplatAbstractions.INSTANCE.getStaffcastVM(player, InteractionHand.MAIN_HAND)
            CastingVM vm = IXplatAbstractions.INSTANCE.getStaffcastVM(player, InteractionHand.MAIN_HAND);

            List<Iota> spell = IotaHelper.patternIotasFromStrings(new ArrayList<>(patterns));

            // TODO: executeInContinuation / queueExecuteAndWrapIotas возвращает ExecutionClientView
            var clientView = vm.queueExecuteAndWrapIotas(spell, player.serverLevel());

            if (clientView.isStackClear()) {
                IXplatAbstractions.INSTANCE.setStaffcastImage(player, null);
                IXplatAbstractions.INSTANCE.setPatterns(player, List.of());
            } else {
                IXplatAbstractions.INSTANCE.setStaffcastImage(player, vm.getImage());
            }

            // TODO: пакет теперь MsgNewSpellPatternS2C(ExecutionClientView, int resolvedPatternsIndex)
            IXplatAbstractions.INSTANCE.sendPacketToPlayer(player,
                    new at.petrak.hexcasting.common.msgs.MsgNewSpellPatternS2C(clientView, 0));
        } finally {
            // Remove the override
            StorageManager.currentlyForcedPlayers.remove(player.getUUID());
        }
    }

    @Info(
            value = "Forces a player to cast a series of patterns, this will act as if they cast the patterns themselves",
            params = {
                    @Param(name = "uuid", value = "the uuid of the player in question (UUID)"),
                    @Param(name = "patterns", value = "The patterns to cast (List of Strings)")
            }
    )
    public void forceCastPlayerUUID(UUID uuid, List<String> patterns) {
        forceCastPlayerEntity(HexJS.server.getPlayerList().getPlayer(uuid), patterns);
    }

    @Info(
            value = "Forces a player to cast a series of patterns, this will act as if they cast the patterns themselves",
            params = {
                    @Param(name = "name", value = "the name of the player in question, this is not the DisplayName (String)"),
                    @Param(name = "patterns", value = "The patterns to cast (List of Strings)")
            }
    )
    public void forceCastPlayerName(String name, List<String> patterns) {
        forceCastPlayerEntity(HexJS.server.getPlayerList().getPlayerByName(name), patterns);
    }

    // -- Casting Items --
    @HideFromJS
    private ItemStack imbuePatternsFromAngles(Item base, ArrayList<String> patternAngles, long media) {
        ItemStack baseStack = base.getDefaultInstance();
        // В 0.11 findHexHolder отдаёт ADHexHolder всё ещё, но writeHex принимает media как long
        var hexHolder = IXplatAbstractions.INSTANCE.findHexHolder(baseStack);

        List<Iota> patterns = IotaHelper.patternIotasFromStrings(patternAngles);

        hexHolder.writeHex(patterns, null, media); // TODO: проверь сигнатуру (появился доп. параметр pigment)

        return baseStack;
    }

    @Info(
            value = "Creates a cypher pre-imbued with the specified spell and media",
            params = {
                    @Param(name = "spell", value = "A list of strings representing the patterns of the spell (List of Strings)"),
                    @Param(name = "media", value = "the amount of media to put in the cypher, in units of 1/10,000 of a default dust (long)")
            }
    )
    public ItemStack createCypher(List<String> spell, long media) {
        return imbuePatternsFromAngles(HexItems.CYPHER, new ArrayList<>(spell), media);
    }

    @Info(
            value = "Creates a trinket pre-imbued with the specified spell and media",
            params = {
                    @Param(name = "spell", value = "A list of strings representing the patterns of the spell (List of Strings)"),
                    @Param(name = "media", value = "the amount of media to put in the trinket, in units of 1/10,000 of a default dust (long)")
            }
    )
    public ItemStack createTrinket(List<String> spell, long media) {
        return imbuePatternsFromAngles(HexItems.TRINKET, new ArrayList<>(spell), media);
    }

    @Info(
            value = "Creates an artifact pre-imbued with the specified spell and media",
            params = {
                    @Param(name = "spell", value = "A list of strings representing the patterns of the spell (List of Strings)"),
                    @Param(name = "media", value = "the amount of media to put in the artifact, in units of 1/10,000 of a default dust (long)")
            }
    )
    public ItemStack createArtifact(List<String> spell, long media) {
        return imbuePatternsFromAngles(HexItems.ARTIFACT, new ArrayList<>(spell), media);
    }

    // -- Scrolls --

    /** Записывает в стек компоненты свитка (вместо NBT-тега). */
    private void applyScrollComponents(ItemStack scroll, String scrollID, HexDir startDir, String patternAngles) {
        HexPattern pattern = new HexPattern(startDir, IotaHelper.anglesFromString(patternAngles));

        scroll.set(HexDataComponents.PATTERN, pattern);
        if (scrollID != null) {
            ResourceLocation loc = ResourceLocation.parse(scrollID);
            ResourceKey<ActionRegistryEntry> actionKey = ResourceKey.create(HexRegistries.ACTION, loc);
            scroll.set(HexDataComponents.ACTION, actionKey);
        }
    }

    @Info(
            value = "Creates a small scroll pre-imbued with the specified pattern",
            params = {
                    @Param(name = "scrollName", value = "The desired name of the final scroll"),
                    @Param(name = "startDir", value = "The direction of the starting stroke of the pattern"),
                    @Param(name = "patternAngles", value = "The angle signature of the pattern to imbue")
            }
    )
    public ItemStack createSmallScroll(String scrollName, HexDir startDir, String patternAngles) {
        ItemStack scroll = new ItemStack(HexItems.SCROLL_SMOL);

        applyScrollComponents(scroll, null, startDir, patternAngles);
        scroll.set(DataComponents.CUSTOM_NAME, Component.literal(scrollName));

        return scroll;
    }

    @Info(
            value = "Creates a medium scroll pre-imbued with the specified pattern",
            params = {
                    @Param(name = "scrollName", value = "The desired name of the final scroll"),
                    @Param(name = "startDir", value = "The direction of the starting stroke of the pattern"),
                    @Param(name = "patternAngles", value = "The angle signature of the pattern to imbue")
            }
    )
    public ItemStack createMediumScroll(String scrollName, HexDir startDir, String patternAngles) {
        ItemStack scroll = new ItemStack(HexItems.SCROLL_MEDIUM);

        applyScrollComponents(scroll, null, startDir, patternAngles);
        scroll.set(DataComponents.CUSTOM_NAME, Component.literal(scrollName));

        return scroll;
    }

    @Info(
            value = "Creates a large scroll pre-imbued with the specified pattern",
            params = {
                    @Param(name = "scrollName", value = "The desired name of the final scroll"),
                    @Param(name = "startDir", value = "The direction of the starting stroke of the pattern"),
                    @Param(name = "patternAngles", value = "The angle signature of the pattern to imbue")
            }
    )
    public ItemStack createLargeScroll(String scrollName, HexDir startDir, String patternAngles) {
        ItemStack scroll = new ItemStack(HexItems.SCROLL_LARGE);

        applyScrollComponents(scroll, null, startDir, patternAngles);
        scroll.set(DataComponents.CUSTOM_NAME, Component.literal(scrollName));

        return scroll;
    }

    @Info(
            value = "Creates an ancient scroll pre-imbued with the specified pattern",
            params = {
                    @Param(name = "scrollID", value = "The registered language entry ID of the pattern name under the 'hexcasting.spell.minecraft:' category"),
                    @Param(name = "startDir", value = "The direction of the starting stroke of the pattern"),
                    @Param(name = "patternAngles", value = "The angle signature of the pattern to imbue")
            }
    )
    public ItemStack createAncientScroll(String scrollID, HexDir startDir, String patternAngles) {
        ItemStack scroll = new ItemStack(HexItems.SCROLL_LARGE);

        applyScrollComponents(scroll, scrollID, startDir, patternAngles);

        return scroll;
    }

    // -- Global и Per-player: без изменений, вставь свои методы как есть --
}