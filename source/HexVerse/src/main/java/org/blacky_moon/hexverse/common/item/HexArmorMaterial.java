package org.blacky_moon.hexverse.common.item;

import at.petrak.hexcasting.common.lib.HexItems;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.Util;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class HexArmorMaterial {
    public static final ArmorMaterial INSTANCE = create();
    private static ArmorMaterial create() {
        Map<ArmorItem.Type, Integer> defenseMap = Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
            map.put(ArmorItem.Type.HELMET, 3);
            map.put(ArmorItem.Type.CHESTPLATE, 8);
            map.put(ArmorItem.Type.LEGGINGS, 6);
            map.put(ArmorItem.Type.BOOTS, 3);
        });
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath("hexverse", "hex_armor");
        List<ArmorMaterial.Layer> layers = List.of(new ArmorMaterial.Layer(texture));
        Supplier<Ingredient> repairIngredient = () -> Ingredient.of(HexItems.CHARGED_AMETHYST);
        Holder<SoundEvent> equipSound = SoundEvents.ARMOR_EQUIP_DIAMOND;
        return new ArmorMaterial(
                defenseMap,
                20,
                equipSound,
                repairIngredient,
                layers,
                2.0F,
                0.0F
        );
    }
}