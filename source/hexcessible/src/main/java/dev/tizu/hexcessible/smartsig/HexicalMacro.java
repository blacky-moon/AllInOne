package dev.tizu.hexcessible.smartsig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.Nullable;

import at.petrak.hexcasting.api.casting.math.HexAngle;
import at.petrak.hexcasting.api.casting.math.HexDir;
import dev.tizu.hexcessible.Utils;
import dev.tizu.hexcessible.entries.PatternEntries;
import net.neoforged.fml.ModList;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class HexicalMacro implements SmartSig.Conditional {

    @Override
    public boolean enabled() {
        return ModList.get().isLoaded("hexical");
    }

    @Override
    public @Nullable List<PatternEntries.Entry> get(String query) {
        return getAllMacros().stream().map(this::getFor).toList();
    }

    @Override
    public @Nullable PatternEntries.Entry get(List<HexAngle> sig) {
        var target = Utils.angle(sig);
        for (var macro : getAllMacros()) {
            if (macro.equals(target))
                return getFor(sig);
        }
        return null;
    }

    /**
     * Returns the angle-signature strings ("qaq", "deewde", ...) of every
     * macro stored in every grimoire the player is currently carrying
     * (main inventory, offhand, armor slots, ender chest). A grimoire
     * keeps its known patterns as keys of a "metadata" NBT compound tag
     * on the ItemStack itself — see Hexical's OpGrimoireIndex/
     * OpGrimoireWrite — so this is plain client-visible ItemStack NBT,
     * no networking required.
     */
    private List<String> getAllMacros() {
        var player = Minecraft.getInstance().player;
        if (player == null)
            return List.of();

        var targetItem = BuiltInRegistries.ITEM.get(
                ResourceLocation.fromNamespaceAndPath("hexical", "grimoire"));

        var inventory = player.getInventory();
        var stacks = Stream.of(inventory.items, inventory.offhand, inventory.armor,
                        player.getEnderChestInventory().getItems())
                .flatMap(Collection::stream)
                .filter(stack -> stack.is(targetItem))
                .toList();

        var signatures = new ArrayList<String>();
        for (var stack : stacks) {
            CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
            if (customData == null)
                continue;

            CompoundTag tag = customData.copyTag();
            if (!tag.contains("metadata"))
                continue;

            signatures.addAll(tag.getCompound("metadata").getAllKeys());
        }
        return signatures;
    }

    private PatternEntries.Entry getFor(String angleSig) {
        return getFor(Utils.angle(angleSig));
    }

    private PatternEntries.Entry getFor(List<HexAngle> sig) {
        var i18nkey = Component.translatable("hexcessible.smartsig.grimoire").getString();
        return new PatternEntries.Entry("hexical:grimoire_macro/" + Utils.angle(sig),
                i18nkey, () -> false, HexDir.EAST, List.of(sig), List.of(), 0);
    }
}